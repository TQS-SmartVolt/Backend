package ua.tqs.smartvolt.smartvolt.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
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

  @Test
  @Tag("IT-Fast") // Use Medium tag as it involves multiple API calls and data manipulation
  @Requirement("SV-33") // New requirement for View Personal Charging Statistics
  void getUserStatistics_AfterNewBookings_UpdatesCorrectly() {
    int currentMonth = LocalDateTime.now().getMonthValue();

    // 1. Initial Check: Get current consumption and spending for the current month
    double initialCurrentMonthConsumption = getMonthlyConsumption(driverSvToken, currentMonth);
    double initialCurrentMonthSpending = getMonthlySpending(driverSvToken, currentMonth);

    System.out.printf(
        "DEBUG: Initial consumption for month %d: %.2f kWh%n",
        currentMonth, initialCurrentMonthConsumption);
    System.out.printf(
        "DEBUG: Initial spending for month %d: %.2f €%n",
        currentMonth, initialCurrentMonthSpending);

    // 2. Create two new bookings for the current month
    // Booking 1: Slot 201 (Slow, 10kW, 0.15€/kWh)
    BookingData b1Data = createBooking(driverSvToken, 201L, 10.0, 0.15, 1); // 1 hour offset
    System.out.printf(
        "DEBUG: Created booking 1 (%.2f kWh, %.2f €) for current month %d.%n",
        b1Data.energyDelivered(), b1Data.cost(), currentMonth);

    // Booking 2: Slot 203 (Medium, 20kW, 0.25€/kWh)
    BookingData b2Data = createBooking(driverSvToken, 203L, 20.0, 0.25, 2); // 2 hours offset
    System.out.printf(
        "DEBUG: Created booking 2 (%.2f kWh, %.2f €) for current month %d.%n",
        b2Data.energyDelivered(), b2Data.cost(), currentMonth);

    // 3. Second Check: Get updated consumption and spending for the current month
    double updatedCurrentMonthConsumption = getMonthlyConsumption(driverSvToken, currentMonth);
    double updatedCurrentMonthSpending = getMonthlySpending(driverSvToken, currentMonth);

    System.out.printf(
        "DEBUG: Updated consumption for month %d: %.2f kWh%n",
        currentMonth, updatedCurrentMonthConsumption);
    System.out.printf(
        "DEBUG: Updated spending for month %d: %.2f €%n",
        currentMonth, updatedCurrentMonthSpending);

    // 4. Verification
    double expectedConsumptionIncrease = b1Data.energyDelivered() + b2Data.energyDelivered();
    double expectedSpendingIncrease = b1Data.cost() + b2Data.cost();

    double delta = 0.001;

    assertThat(
        updatedCurrentMonthConsumption,
        closeTo(initialCurrentMonthConsumption + expectedConsumptionIncrease, delta));
    assertThat(
        updatedCurrentMonthSpending,
        closeTo(initialCurrentMonthSpending + expectedSpendingIncrease, delta));

    System.out.printf(
        "DEBUG: Consumption Expected: %.2f kWh, Actual: %.2f kWh%n",
        initialCurrentMonthConsumption + expectedConsumptionIncrease,
        updatedCurrentMonthConsumption);
    System.out.printf(
        "DEBUG: Spending Expected: %.2f €, Actual: %.2f €%n",
        initialCurrentMonthSpending + expectedSpendingIncrease, updatedCurrentMonthSpending);

    System.out.println("DEBUG: Consumption and spending updated correctly after new bookings.");
  }

  private double getMonthlyConsumption(String token, int month) {
    List<Double> consumptionList =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .when()
            .get(getUsersBaseUrl() + "/consumption")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("consumptionPerMonth", Double.class);
    return consumptionList.get(month - 1);
  }

  private double getMonthlySpending(String token, int month) {
    List<Double> spendingList =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + token)
            .when()
            .get(getUsersBaseUrl() + "/spending")
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .jsonPath()
            .getList("spendingPerMonth", Double.class);
    return spendingList.get(month - 1);
  }

  private BookingData createBooking(
      String token, Long slotId, double power, double pricePerKWh, int hourOffset) {
    LocalDateTime bookingStartTime = LocalDateTime.now().plusHours(hourOffset);

    // Adjust to nearest 00 or 30 minute mark
    int currentMinute = bookingStartTime.getMinute();
    if (currentMinute > 30) {
      bookingStartTime = bookingStartTime.withMinute(30).withSecond(0).withNano(0);
    } else {
      bookingStartTime = bookingStartTime.withMinute(0).withSecond(0).withNano(0);
    }

    BookingRequest request = new BookingRequest(slotId, bookingStartTime);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + token)
        .body(request)
        .when()
        .post(getBookingsBaseUrl() + "/start-payment")
        .then()
        .statusCode(HttpStatus.OK.value());

    double energyDelivered = power * 0.5; // Assuming 0.5h duration based on system logic
    double cost = energyDelivered * pricePerKWh;

    return new BookingData(energyDelivered, cost);
  }

  // A record to hold calculated booking data
  private record BookingData(double energyDelivered, double cost) {}
}
