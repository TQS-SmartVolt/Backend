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
// @TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@ActiveProfiles("testcontainers")
class ChargingStationControllerIT {

  @Container
  public static final PostgreSQLContainer<?> container =
      new PostgreSQLContainer<>("postgres:12")
          .withDatabaseName("meals_db")
          .withUsername("testuser")
          .withPassword("testpass");

  @DynamicPropertySource
  static void overrideProps(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", container::getJdbcUrl);
    registry.add("spring.datasource.username", container::getUsername);
    registry.add("spring.datasource.password", container::getPassword);
  }

  @LocalServerPort private int port;

  private String getBaseUrl() {
    return "http://localhost:" + port + "/api/v1/stations";
  }

  private String getLoginUrl() {
    return "http://localhost:" + port + "/api/v1/auth/sign-in";
  }

  String validSvToken;

  @BeforeEach
  public void setUp() {
    validSvToken =
        given()
            .contentType("application/json")
            .body("{\"email\":\"test@example.com\", \"password\":\"password123!\"}")
            .post(getLoginUrl())
            .then()
            .statusCode(HttpStatus.OK.value())
            .extract()
            .path("token");

    System.out.println("> Valid SV Token: " + validSvToken);
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-34")
  void getAllChargingStations_WhenOperatorNotExists_ThenThrowsResourceNotFoundException()
      throws Exception {
    String invalidSvToken = "invalid-token";

    given()
        .contentType("application/json")
        .cookie("sv_token", invalidSvToken)
        .when()
        .get(getBaseUrl())
        .then()
        .statusCode(HttpStatus.FORBIDDEN.value());
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-34")
  void getAllChargingStations_WhenOperatorExists_ThenListOfChargingStations() throws Exception {

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + validSvToken) // Using Bearer token for authorization
        .when()
        .get(getBaseUrl())
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("$", hasSize(greaterThan(0)));
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-34")
  void createChargingStation_WhenOperatorExists_ThenCreatesChargingStation() throws Exception {

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + validSvToken) // Using Bearer token for authorization
        .body(
            "{\"name\":\"Station 2\", \"latitude\": 12.34, \"longitude\": 56.78, \"address\": \"123 Main St\"}")
        .when()
        .post(getBaseUrl())
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .body("name", equalTo("Station 2"))
        .body("latitude", equalTo(12.34f))
        .body("longitude", equalTo(56.78f))
        .body("address", equalTo("123 Main St"));
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-35")
  void deactivateChargingStation_WhenChargingStationExists_ThenDeactivatesChargingStation()
      throws Exception {

    // First station ID for testing
    Long stationId = 1L;

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + validSvToken) // Using Bearer token for authorization
        .when()
        .patch(getBaseUrl() + "/" + stationId + "/status?activate=false")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("availability", equalTo(false));
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-35")
  void activateChargingStation_WhenChargingStationExists_ThenActivatesChargingStation()
      throws Exception {

    // First station ID for testing
    Long stationId = 1L;

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + validSvToken) // Using Bearer token for authorization
        .when()
        .patch(getBaseUrl() + "/" + stationId + "/status?activate=true")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("availability", equalTo(true));
  }

  @Test
  @Tag("IT-Fast")
  @Requirement("SV-35")
  void updateChargingStationStatus_WhenStationNotExists_ThenThrowsResourceNotFoundException()
      throws Exception {

    Long nonExistentStationId = 999L;

    given()
        .contentType("application/json")
        .header("Authorization", "Bearer " + validSvToken) // Using Bearer token for authorization
        .when()
        .patch(getBaseUrl() + "/" + nonExistentStationId + "/status?activate=true")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value());
  }
}
