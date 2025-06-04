package ua.tqs.smartvolt.smartvolt.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testcontainers")
@Tag("IT-User")
class UserControllerIT {

  @Container
  public static final PostgreSQLContainer<?> container =
      new PostgreSQLContainer<>("postgres:12")
          .withDatabaseName("smartvolt_user_db") // Use a distinct database name for this test
          .withUsername("testuser")
          .withPassword("testpass");

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add("spring.flyway.locations", () -> "classpath:db/migration");
  }

  @LocalServerPort private int port;

  // Base URL for the User controller endpoints
  private String getUsersBaseUrl() {
    return "http://localhost:" + port + "/api/v1/users";
  }

  // Base URL for the Auth controller endpoints
  private String getLoginUrl() {
    return "http://localhost:" + port + "/api/v1/auth/sign-in";
  }

  // Base URL for the Booking controller endpoints (needed to create test data)
  private String getBookingsBaseUrl() {
    return "http://localhost:" + port + "/api/v1/bookings";
  }

  private String driverSvToken;
  private String operatorSvToken; // Also get operator token for negative test if needed

  @BeforeEach
  public void setUp() {
    // Get token for the EV Driver
    driverSvToken =
        given()
            .contentType("application/json")
            .body("{\"email\":\"evdriver@example.com\", \"password\":\"password123\"}")
            .post(getLoginUrl())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("token");

    System.out.println("> EV Driver SV Token for User IT: " + driverSvToken);

    // Get token for the Station Operator (for negative tests)
    operatorSvToken =
        given()
            .contentType("application/json")
            .body("{\"email\":\"test@example.com\", \"password\":\"password123\"}")
            .post(getLoginUrl())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("token");

    System.out.println("> Station Operator SV Token for User IT: " + operatorSvToken);
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-32")
  void getChargingHistory_ValidDriver_ReturnsHistoryInTableFormat() {
    // --- Setup: Create sample bookings for the EV Driver (userId=3) ---
    // Use slots from V002__INSERT_DATA.sql:
    // 201: Slow, 10kW, 0.15€/kWh, Station 102 ('Station Slow')
    // 203: Medium, 20kW, 0.25€/kWh, Station 103 ('Station Medium')
    // 204: Fast, 30kW, 0.40€/kWh, Station 104 ('Station Fast')

    // Booking 1: Slow charging
    LocalDateTime startTime1 =
        LocalDateTime.now().plusDays(5).withHour(9).withMinute(0).withSecond(0).withNano(0);
    Long slotId1 = 201L;
    BookingRequest request1 = new BookingRequest(slotId1, startTime1);
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request1)
        .when()
        .post(getBookingsBaseUrl() + "/start-payment")
        .then()
        .statusCode(HttpStatus.OK.value());
    System.out.println("DEBUG: Created booking 1 for history test.");

    // Booking 2: Medium charging
    LocalDateTime startTime2 =
        LocalDateTime.now().plusDays(4).withHour(14).withMinute(30).withSecond(0).withNano(0);
    Long slotId2 = 203L;
    BookingRequest request2 = new BookingRequest(slotId2, startTime2);
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request2)
        .when()
        .post(getBookingsBaseUrl() + "/start-payment")
        .then()
        .statusCode(HttpStatus.OK.value());
    System.out.println("DEBUG: Created booking 2 for history test.");

    // Booking 3: Fast charging
    LocalDateTime startTime3 =
        LocalDateTime.now().plusDays(3).withHour(11).withMinute(0).withSecond(0).withNano(0);
    Long slotId3 = 204L;
    BookingRequest request3 = new BookingRequest(slotId3, startTime3);
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request3)
        .when()
        .post(getBookingsBaseUrl() + "/start-payment")
        .then()
        .statusCode(HttpStatus.OK.value());
    System.out.println("DEBUG: Created booking 3 for history test.");

    // --- Action: Retrieve charging history ---
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .when()
        .get(getUsersBaseUrl() + "/bookings")
        .then()
        .log()
        .all() // Log the response for debugging
        .statusCode(HttpStatus.OK.value())
        .body("$", hasSize(greaterThanOrEqualTo(3))) // Ensure at least 3 bookings are returned
        // Assertions for Booking 1 (Slow charging)
        .body("startTime", hasItem(startTime1.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .body("chargingStationName", hasItem("Station Slow"))
        .body("chargingSpeed", hasItem("Slow"))
        .body("power", hasItem(10.0F)) // Use float for double comparison
        .body("energyDelivered", hasItem(5.0F)) // 10kW * 0.5h = 5kWh
        .body("pricePerKWh", hasItem(0.15F))
        .body("cost", hasItem(0.75F)) // 5kWh * 0.15€/kWh = 0.75€
        // Assertions for Booking 2 (Medium charging)
        .body("startTime", hasItem(startTime2.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .body("chargingStationName", hasItem("Station Medium"))
        .body("chargingSpeed", hasItem("Medium"))
        .body("power", hasItem(20.0F))
        .body("energyDelivered", hasItem(10.0F)) // 20kW * 0.5h = 10kWh
        .body("pricePerKWh", hasItem(0.25F))
        .body("cost", hasItem(2.50F)) // 10kWh * 0.25€/kWh = 2.50€
        // Assertions for Booking 3 (Fast charging)
        .body("startTime", hasItem(startTime3.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
        .body("chargingStationName", hasItem("Station Fast"))
        .body("chargingSpeed", hasItem("Fast"))
        .body("power", hasItem(30.0F))
        .body("energyDelivered", hasItem(15.0F)) // 30kW * 0.5h = 15kWh
        .body("pricePerKWh", hasItem(0.40F))
        .body("cost", hasItem(6.00F)); // 15kWh * 0.40€/kWh = 6.00€

    System.out.println("DEBUG: Successfully retrieved and verified charging history.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-32")
  void getChargingHistory_Unauthorized_ReturnsForbidden() {
    given()
        .contentType("application/json")
        // No Authorization header
        .when()
        .get(getUsersBaseUrl() + "/bookings")
        .then()
        .log()
        .all()
        .statusCode(HttpStatus.FORBIDDEN.value());
    System.out.println("DEBUG: Verified charging history access fails without authorization.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-32")
  void getChargingHistory_NonEvDriverRole_ReturnsForbidden() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + operatorSvToken) // Use operator token
        .when()
        .get(getUsersBaseUrl() + "/bookings")
        .then()
        .log()
        .all()
        .statusCode(HttpStatus.FORBIDDEN.value()); // Expect Forbidden as only EV_DRIVER is allowed
    System.out.println("DEBUG: Verified charging history access fails for non-EV_DRIVER role.");
  }
}
