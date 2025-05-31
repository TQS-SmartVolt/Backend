package ua.tqs.smartvolt.smartvolt.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
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
class ServiceStationsMapControllerIT {

  @Container
  public static final PostgreSQLContainer<?> container =
      new PostgreSQLContainer<>("postgres:12")
          .withDatabaseName("smartvolt_db") // Use a more specific database name
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

  @BeforeEach
  public void setUp() {
    // Get token for the EV Driver
    driverSvToken =
        given()
            .contentType("application/json")
            .body("{\"email\":\"evdriver@example.com\", \"password\":\"password123!\"}")
            .post(getLoginUrl())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("token");

    System.out.println("> EV Driver SV Token: " + driverSvToken);
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_FilterSlow_ReturnsOnlySlowStations() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeeds", "Slow")
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("stations", hasSize(2)) // Corrected JSON path
        .body("stations.name", hasItems("Station Slow", "Station Mixed")) // Corrected JSON path
        .body(
            "stations.name",
            not(hasItems("Station Medium", "Station Fast", "Station 1"))); // Corrected JSON path
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_FilterMedium_ReturnsOnlyMediumStations() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeeds", "Medium")
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("stations", hasSize(2)) // Corrected JSON path
        .body("stations.name", hasItems("Station Medium", "Station Mixed")) // Corrected JSON path
        .body(
            "stations.name",
            not(hasItems("Station Slow", "Station Fast", "Station 1"))); // Corrected JSON path
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_FilterFast_ReturnsOnlyFastStations() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeeds", "Fast")
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("stations", hasSize(1)) // Corrected JSON path
        .body("stations.name", hasItems("Station Fast")) // Corrected JSON path
        .body(
            "stations.name",
            not(
                hasItems(
                    "Station Slow",
                    "Station Medium",
                    "Station Mixed",
                    "Station 1"))); // Corrected JSON path
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_FilterSlowAndMedium_ReturnsCorrectStations() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeeds", "Slow,Medium")
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("stations", hasSize(3)) // Corrected JSON path
        .body(
            "stations.name",
            hasItems("Station Slow", "Station Medium", "Station Mixed")) // Corrected JSON path
        .body("stations.name", not(hasItems("Station Fast", "Station 1"))); // Corrected JSON path
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_NoFilters_ReturnsBadRequest() { // Method name updated for
    // clarity
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        // No queryParam("chargingSpeeds") as per the requirement for 400 Bad Request
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.BAD_REQUEST.value()); // Expected 400 as a filter is required
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_Unauthorized_ReturnsForbidden() {
    String invalidToken = "invalid.token.here";
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + invalidToken)
        .queryParam("chargingSpeeds", "Slow") // Still test with a param
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_InvalidSpeed_ReturnsEmptyListOrError() {
    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + driverSvToken)
        .queryParam("chargingSpeeds", "InvalidSpeed")
        .when()
        .get(getBaseUrl() + "/map")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }
}
