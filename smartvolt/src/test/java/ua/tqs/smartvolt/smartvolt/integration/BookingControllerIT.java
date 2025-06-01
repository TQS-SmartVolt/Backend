package ua.tqs.smartvolt.smartvolt.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
@Tag("IT-Booking")
class BookingControllerIT {

  @Container
  public static final PostgreSQLContainer<?> container =
      new PostgreSQLContainer<>("postgres:12")
          .withDatabaseName("smartvolt_booking_db")
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

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/bookings";
  }

  private String getLoginUrl() {
    return "http://localhost:" + port + "/api/v1/auth/sign-in";
  }

  String driverSvToken;

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

    System.out.println("> EV Driver SV Token for Booking IT: " + driverSvToken);
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242")
  void createBooking_ValidRequest_ReturnsCreatedBookingAndSavesToDb() {
    Long slotId = 201L; // Corrected: Based on V002__INSERT_DATA.sql
    // Choose a start time that's in the future and aligns with a half-hour slot
    LocalDateTime startTime =
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);

    BookingRequest request = new BookingRequest(slotId, startTime);

    // CHANGE THIS LINE: Change String to Long
    Long bookingId =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + driverSvToken)
            .body(request)
            .when()
            .post(getBaseUrl() + "/start-payment")
            .then()
            // .log().all() // REMOVE OR COMMENT OUT THIS LINE - no longer needed for debugging
            .statusCode(HttpStatus.OK.value())
            .body("slot.slotId", equalTo(slotId.intValue()))
            .body(
                "startTime",
                is(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .body("driver.userId", equalTo(3))
            .body("status", equalTo("Not Used"))
            .body("cost", equalTo(1.5F))
            .extract()
            .jsonPath() // Add .jsonPath() to get a JsonPath object
            .getLong("bookingId"); // Then use getLong() to explicitly extract as a Long

    assertNotNull(bookingId, "Booking ID should not be null, indicating successful creation");

    System.out.println("DEBUG: Successfully created booking with ID: " + bookingId);
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242")
  void createBooking_MissingStartTime_ReturnsInternalServerError() {
    Long slotId = 201L;
    BookingRequest request = new BookingRequest(slotId, null);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request)
        .when()
        .post(getBaseUrl() + "/start-payment")
        .then()
        .log()
        .all() // Keep logging here if you want to see the 500 response details
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    System.out.println("DEBUG: Verified booking fails with missing start time, returning 500.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242")
  void createBooking_InvalidSlotId_ReturnsInternalServerError() {
    Long invalidSlotId = 99999L;
    LocalDateTime startTime =
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    BookingRequest request = new BookingRequest(invalidSlotId, startTime);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request)
        .when()
        .post(getBaseUrl() + "/start-payment")
        .then()
        .log()
        .all() // Keep logging here if you want to see the 500 response details
        .statusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
    System.out.println("DEBUG: Verified booking fails with invalid slot ID, returning 500.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242")
  void createBooking_Unauthorized_ReturnsForbidden() {
    Long slotId = 201L;
    LocalDateTime startTime =
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    BookingRequest request = new BookingRequest(slotId, startTime);

    given()
        .contentType("application/json")
        .body(request)
        .when()
        .post(getBaseUrl() + "/start-payment")
        .then()
        .log()
        .all() // Keep logging here for completeness
        .statusCode(HttpStatus.FORBIDDEN.value());
    System.out.println("DEBUG: Verified booking fails without authorization.");
  }
}
