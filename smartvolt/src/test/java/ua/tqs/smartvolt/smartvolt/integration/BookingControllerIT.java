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
  String operatorSvToken;

  @BeforeEach
  void setUp() {
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

    // Get token for the Station Operator
    operatorSvToken =
        given()
            .contentType("application/json")
            .body("{\"email\":\"test@example.com\", \"password\":\"password123\"}")
            .post(getLoginUrl())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("token");

    System.out.println("> EV Driver SV Token for Booking IT: " + driverSvToken);
    System.out.println("> Station Operator SV Token for Booking IT: " + operatorSvToken);
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
            .log()
            .all() // REMOVE OR COMMENT OUT THIS LINE - no longer needed for debugging
            .statusCode(HttpStatus.OK.value())
            .body("slot.slotId", equalTo(slotId.intValue()))
            .body(
                "startTime",
                is(startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"))))
            .body("driver.userId", equalTo(3))
            .body("status", equalTo("Not Used"))
            .body("cost", equalTo(0.75F))
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
        .all() // Keep logging temporarily for debugging
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body("error", equalTo("Bad Request"))
        .body("message", containsString("Start time cannot be null"));
    System.out.println(
        "DEBUG: Verified booking fails with missing start time, returning 400 BAD REQUEST.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242")
  void createBooking_InvalidSlotId_ReturnsNotFound() {
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
        .all() // Keep logging temporarily for debugging
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body("error", equalTo("Not Found"))
        .body("message", containsString("Slot not found with id: " + invalidSlotId));

    System.out.println(
        "DEBUG: Verified booking fails with invalid slot ID, returning 404 NOT FOUND.");
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
        .all() // Keep logging temporarily for debugging
        .statusCode(HttpStatus.FORBIDDEN.value());
    System.out.println("DEBUG: Verified booking fails without authorization.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242") // Assign a new requirement ID for this test
  void createBooking_AlreadyOccupiedSlot_ReturnsConflict() {
    Long slotId = 201L; // A valid slot ID from your V002__INSERT_DATA.sql

    // e.g., 2 days from now at 11:00 AM
    LocalDateTime startTime =
        LocalDateTime.now().plusDays(2).withHour(11).withMinute(0).withSecond(0).withNano(0);

    // Format for the expected message (consistent with the message in SlotAlreadyBookedException)
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    String formattedStartTime = startTime.format(formatter);

    BookingRequest request = new BookingRequest(slotId, startTime);

    // 1. Create the first booking successfully
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request)
        .when()
        .post(getBaseUrl() + "/start-payment")
        .then()
        .statusCode(HttpStatus.OK.value());

    System.out.println(
        "DEBUG: First booking created for slot " + slotId + " at " + formattedStartTime);

    // 2. Attempt to create a second booking for the same slot and start time
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request) // Same request
        .when()
        .post(getBaseUrl() + "/start-payment")
        .then()
        .log()
        .all() // Log the response for debugging if it fails
        .statusCode(HttpStatus.CONFLICT.value()) // Expect 409 Conflict
        .body("error", equalTo("Conflict")) // Match the error field from GlobalExceptionHandler
        .body(
            "message",
            containsString(
                "Slot "
                    + slotId
                    + " is already booked at "
                    + formattedStartTime)); // Match the message

    System.out.println("DEBUG: Second booking attempt for occupied slot failed as expected.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-242") // Assign a new requirement ID for this validation
  void createBooking_InvalidStartTimeInterval_ReturnsBadRequest() {
    Long slotId = 201L;
    // Choose a start time that is NOT on a 30-minute interval (e.g., 10:15, 10:40)
    LocalDateTime invalidStartTime =
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(15).withSecond(0).withNano(0);

    BookingRequest request = new BookingRequest(slotId, invalidStartTime);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .body(request)
        .when()
        .post(getBaseUrl() + "/start-payment")
        .then()
        .log()
        .all() // Log the response for debugging if it fails
        .statusCode(HttpStatus.BAD_REQUEST.value()) // Expect 400 Bad Request
        .body("error", equalTo("Bad Request")) // Match the error field from GlobalExceptionHandler
        .body(
            "message",
            containsString(
                "Booking start time must be on a 30-minute interval (e.g., HH:00 or HH:30).")); // Match the specific message

    System.out.println(
        "DEBUG: Verified booking fails with invalid start time interval, returning 400 BAD REQUEST.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-36")
  void getEnergyConsumption_ValidRequest_ReturnsEnergyConsumption() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + operatorSvToken)
        .when()
        .get(getBaseUrl() + "/consumption")
        .then()
        .log()
        .all() // Log the response for debugging if it fails
        .statusCode(HttpStatus.OK.value())
        .body("totalEnergy", greaterThanOrEqualTo(0.0f))
        .body("averageEnergyPerMonth", greaterThanOrEqualTo(0.0f))
        .body("monthEnergy", notNullValue());

    System.out.println("DEBUG: Successfully retrieved energy consumption data for the operator.");
  }
}
