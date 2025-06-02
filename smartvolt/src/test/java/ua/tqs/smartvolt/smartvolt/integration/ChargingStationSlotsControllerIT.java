package ua.tqs.smartvolt.smartvolt.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;
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

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testcontainers")
class ChargingStationSlotsControllerIT {

  @Container
  public static final PostgreSQLContainer<?> container =
      new PostgreSQLContainer<>("postgres:12")
          .withDatabaseName("smartvolt_slots_db") // Use a unique database name for this IT class
          .withUsername("testuser")
          .withPassword("testpass");

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
    registry.add(
        "spring.flyway.locations",
        () -> "classpath:db/migration"); // Ensure Flyway finds the SQL files
  }

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/stations";
  }

  private String getLoginUrl() {
    return "http://localhost:" + port + "/api/v1/auth/sign-in";
  }

  String driverSvToken;
  String operatorSvToken;

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

    operatorSvToken =
        given()
            .contentType("application/json")
            .body("{\"email\":\"test@example.com\", \"password\":\"password123\"}")
            .post(getLoginUrl())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("token");

    System.out.println("> EV Driver SV Token for Slots IT: " + driverSvToken);
    System.out.println("> Operator SV Token for Slots IT: " + operatorSvToken);
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_ValidRequest_ReturnsAvailableSlots() {
    Long stationId = 102L; // Station Slow (has only Slow slots)
    String chargingSpeed = "Slow";
    LocalDate today = LocalDate.now();
    String dateString = today.format(DateTimeFormatter.ISO_DATE);

    // Perform the API call and extract necessary information
    List<Integer> slotIds =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + driverSvToken)
            .queryParam("chargingSpeed", chargingSpeed)
            .queryParam("date", dateString)
            .when()
            .get(getBaseUrl() + "/" + stationId + "/slots")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pricePerKWh", equalTo(0.15F)) // Keep the exact price check
            .body(
                "availableSlotMapping",
                hasSize(96)) // Check total expected entries (2 slots * 48 half-hours)
            .extract()
            .jsonPath()
            .getList("availableSlotMapping.slotId"); // Extract all slotIds

    // Use Java assertions with Hamcrest to verify unique slot IDs
    Set<Integer> uniqueSlotIds = slotIds.stream().collect(Collectors.toSet());
    assertThat("Should have exactly 2 distinct physical slot IDs", uniqueSlotIds, hasSize(2));
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_MixedStationFiltersBySpeed_ReturnsCorrectSlots() {
    Long stationId = 105L; // Station Mixed (has Slow and Medium slots)
    LocalDate today = LocalDate.now();
    String dateString = today.format(DateTimeFormatter.ISO_DATE);

    // Test with 'Slow' charging speed
    String slowChargingSpeed = "Slow";
    List<Integer> slowSlotIds =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + driverSvToken)
            .queryParam("chargingSpeed", slowChargingSpeed)
            .queryParam("date", dateString)
            .when()
            .get(getBaseUrl() + "/" + stationId + "/slots")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pricePerKWh", equalTo(0.18F)) // Price for Slow at Station 105 from V002
            .body("availableSlotMapping", hasSize(48)) // 1 physical slow slot * 48 half-hours
            .extract()
            .jsonPath()
            .getList("availableSlotMapping.slotId");

    Set<Integer> uniqueSlowSlotIds = slowSlotIds.stream().collect(Collectors.toSet());
    assertThat(
        "Should have exactly 1 distinct physical slow slot ID", uniqueSlowSlotIds, hasSize(1));

    System.out.println("DEBUG: Verified Slow slots for Station 105.");

    // Test with 'Medium' charging speed
    String mediumChargingSpeed = "Medium";
    List<Integer> mediumSlotIds =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + driverSvToken)
            .queryParam("chargingSpeed", mediumChargingSpeed)
            .queryParam("date", dateString)
            .when()
            .get(getBaseUrl() + "/" + stationId + "/slots")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pricePerKWh", equalTo(0.28F)) // Price for Medium at Station 105 from V002
            .body("availableSlotMapping", hasSize(48)) // 1 physical medium slot * 48 half-hours
            .extract()
            .jsonPath()
            .getList("availableSlotMapping.slotId");

    Set<Integer> uniqueMediumSlotIds = mediumSlotIds.stream().collect(Collectors.toSet());
    assertThat(
        "Should have exactly 1 distinct physical medium slot ID", uniqueMediumSlotIds, hasSize(1));

    System.out.println("DEBUG: Verified Medium slots for Station 105.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_FutureDate_ReturnsAvailableSlots() {
    Long stationId = 104L; // Station Fast (has only Fast slots)
    String chargingSpeed = "Fast";
    LocalDate futureDate = LocalDate.now().plusDays(1); // Requesting slots for tomorrow
    String dateString = futureDate.format(DateTimeFormatter.ISO_DATE);

    List<Integer> slotIds =
        given()
            .contentType("application/json")
            .header("Authorization", "Bearer " + driverSvToken)
            .queryParam("chargingSpeed", chargingSpeed)
            .queryParam("date", dateString)
            .when()
            .get(getBaseUrl() + "/" + stationId + "/slots")
            .then()
            .statusCode(HttpStatus.OK.value())
            .body("pricePerKWh", equalTo(0.40F)) // Price for Fast at Station 104 from V002
            .body("availableSlotMapping", hasSize(48)) // 1 physical fast slot * 48 half-hours
            .extract()
            .jsonPath()
            .getList("availableSlotMapping.slotId");

    Set<Integer> uniqueSlotIds = slotIds.stream().collect(Collectors.toSet());
    assertThat(
        "Should have exactly 1 distinct physical slot ID for future date",
        uniqueSlotIds,
        hasSize(1));
    System.out.println("DEBUG: Verified slots for future date for Station 104.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_NoSlotsForSelectedSpeed_ReturnsEmptyListAndZeroPrice() {
    Long stationId = 103L; // Station Medium (only has Medium slots)
    String chargingSpeed = "Slow"; // Requesting a speed not available at this station
    LocalDate today = LocalDate.now();
    String dateString = today.format(DateTimeFormatter.ISO_DATE);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeed", chargingSpeed)
        .queryParam("date", dateString)
        .when()
        .get(getBaseUrl() + "/" + stationId + "/slots")
        .then()
        .statusCode(HttpStatus.OK.value()) // Expect HTTP 200 OK, as per service logic
        .body("availableSlotMapping", empty()) // Expect an empty list of slots
        .body("pricePerKWh", equalTo(0.0F)); // Expect price to be 0.0F for no slots
    System.out.println(
        "DEBUG: Verified scenario with no slots for selected speed for Station 103.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_InvalidStationId_ReturnsNotFound() {
    Long invalidStationId = 9999L; // An ID that definitely does not exist
    String chargingSpeed = "Slow"; // Provide valid parameters for other fields
    LocalDate today = LocalDate.now();
    String dateString = today.format(DateTimeFormatter.ISO_DATE);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeed", chargingSpeed)
        .queryParam("date", dateString)
        .when()
        .get(getBaseUrl() + "/" + invalidStationId + "/slots")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value()); // Expect 404 Not Found
    System.out.println("DEBUG: Verified scenario with invalid station ID.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_Unauthorized_ReturnsForbidden() {
    Long stationId = 102L; // Use a valid station ID
    String chargingSpeed = "Slow";
    LocalDate today = LocalDate.now();
    String dateString = today.format(DateTimeFormatter.ISO_DATE);
    String invalidToken = "invalid.token.here"; // A clearly invalid or expired token

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + invalidToken) // Provide an invalid token
        .queryParam("chargingSpeed", chargingSpeed)
        .queryParam("date", dateString)
        .when()
        .get(getBaseUrl() + "/" + stationId + "/slots")
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value()); // Expect 403 Forbidden
    System.out.println("DEBUG: Verified scenario with unauthorized access.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-24")
  void getChargingSlotsByStationId_PastDate_ReturnsEmptyListAndZeroPrice() {
    Long stationId = 102L; // Use a valid station ID
    String chargingSpeed = "Slow";
    LocalDate pastDate = LocalDate.now().minusDays(1); // Requesting slots for yesterday
    String dateString = pastDate.format(DateTimeFormatter.ISO_DATE);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeed", chargingSpeed)
        .queryParam("date", dateString)
        .when()
        .get(getBaseUrl() + "/" + stationId + "/slots")
        .then()
        .statusCode(HttpStatus.OK.value()) // Expect HTTP 200 OK
        .body("availableSlotMapping", empty()) // Expect an empty list of slots
        .body("pricePerKWh", equalTo(0.0F)); // Expect price to be 0.0F
    System.out.println("DEBUG: Verified scenario with past date for Station 102.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-68")
  void createChargingSlot_WhenValidRequest_ReturnsCreatedSlot() {
    Long stationId = 102L;
    String chargingSpeed = "Slow";
    double pricePerKWh = 50;

    String requestBody =
        String.format(
            Locale.US,
            "{\"pricePerKWh\": %.2f, \"chargingSpeed\": \"%s\"}",
            pricePerKWh,
            chargingSpeed);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + operatorSvToken)
        .body(requestBody)
        .when()
        .post(getBaseUrl() + "/" + stationId + "/slots")
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("chargingSpeed", equalTo(chargingSpeed))
        .body("pricePerKWh", equalTo((float) pricePerKWh))
        .body("power", equalTo(10.0F));

    System.out.println("DEBUG: Successfully created a new charging slot.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-68")
  void createChargingSlot_WhenInvalidChargingSpeed_ReturnsBadRequest() {
    Long stationId = 102L; // Use a valid station ID
    String invalidChargingSpeed = "UltraFast"; // Invalid speed not defined in the system
    double pricePerKWh = 50;

    String requestBody =
        String.format(
            Locale.US,
            "{\"pricePerKWh\": %.2f, \"chargingSpeed\": \"%s\"}",
            pricePerKWh,
            invalidChargingSpeed);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + operatorSvToken)
        .body(requestBody)
        .when()
        .post(getBaseUrl() + "/" + stationId + "/slots")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value())
        .body(containsString("Invalid charging speed: " + invalidChargingSpeed));

    System.out.println("DEBUG: Verified error for invalid charging speed.");
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-68")
  void createChargingSlot_WhenStationNotFound_ReturnsNotFound() {
    Long invalidStationId = 9999L; // An ID that definitely does not exist
    String chargingSpeed = "Slow";
    double pricePerKWh = 50;

    String requestBody =
        String.format(
            Locale.US,
            "{\"pricePerKWh\": %.2f, \"chargingSpeed\": \"%s\"}",
            pricePerKWh,
            chargingSpeed);

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + operatorSvToken)
        .body(requestBody)
        .when()
        .post(getBaseUrl() + "/" + invalidStationId + "/slots")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .body(containsString("Charging station not found with id: " + invalidStationId));

    System.out.println("DEBUG: Verified error for non-existent station ID.");
  }
}
