package ua.tqs.smartvolt.smartvolt.integration;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
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

  // spotless:off
  @Test
  void whenGetMenuById_thenReturnCorrectDetails() {
    given()
        .contentType("application/json")
        .when()
        .get(getBaseUrl() + "/1")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("name", equalTo("Lunch Menu 1"))
        .body("numMenuOptions", equalTo(4))
        .body("numAvailableOptions", equalTo(4))
        .body("weatherCode", notNullValue())
        .body("weatherTemperature", notNullValue());
  }

  // spotless:on

  @Test
  void whenGetMealOptions_thenReturnGroupedMeals() {
    given()
        .contentType("application/json")
        .when()
        .get(getBaseUrl() + "/1/meal-options")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("SOUP.size()", greaterThan(0))
        .body("MAIN_COURSE.size()", greaterThan(0))
        .body("DESSERT.size()", greaterThan(0))
        .body("DRINK.size()", greaterThan(0));
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

  // @Test
  // @Tag("IT-Fast")
  // @Requirement("SV-34")
  // void testGetAllChargingStations_WhenOperatorNotExists_ThrowsResourceNotFoundException()
  // 		throws Exception {
  // 	Long operatorId = 999L; // Non-existing operator ID

  // 	// When & Then
  // 	mockMvc
  // 			.perform(get("/api/v1/stations?operatorId=" + operatorId))
  // 			.andExpect(status().isNotFound())
  // 			.andExpect(content().string("Operator not found with id: " + operatorId));
  // }

  // 	@Test
  // 	@Tag("IT-Fast")
  // 	@Requirement("SV-34")
  // 	void testGetAllChargingStations_WhenOperatorExists_ReturnsListOfChargingStations()
  // 			throws Exception {
  // 		// Given
  // 		StationOperator operator = new StationOperator();
  // 		operator.setName("Test Operator");
  // 		operator.setEmail("test@example.com");
  // 		operator.setPassword("password");
  // 		stationOperatorRepository.save(operator);

  // 		ChargingStation station1 = new ChargingStation();
  // 		station1.setName("Station 1");
  // 		station1.setLatitude(12.34);
  // 		station1.setLongitude(56.78);
  // 		station1.setAddress("Address 1");
  // 		station1.setAvailability(true);
  // 		station1.setOperator(operator);
  // 		chargingStationRepository.save(station1);

  // 		// When & Then
  // 		mockMvc
  // 				.perform(get("/api/v1/stations?operatorId=" + operator.getUserId()))
  // 				.andExpect(status().isOk())
  // 				.andExpect(content().contentType("application/json"))
  // 				.andExpect(jsonPath("$[0].name").value("Station 1"));
  // 	}

  // 	@Test
  //     @Tag("IT-Fast")
  //     @Requirement("SV-34")
  //     void testCreateChargingStation_WhenOperatorExists_CreatesChargingStation() throws Exception
  // {
  //         // Given
  //         StationOperator operator = new StationOperator();
  //         operator.setName("Test Operator");
  //         operator.setEmail("test@example.com");
  //         operator.setPassword("password");
  //         stationOperatorRepository.save(operator);

  //         String requestBody = "{ \"name\": \"Station 1\", \"latitude\": 12.34, \"longitude\":
  // 56.78, \"operatorId\": "
  //                 + operator.getUserId()
  //                 + " }";
  //         // When & Then
  //         mockMvc

  // .perform(post("/api/v1/stations").contentType("application/json").content(requestBody))
  //                 .andExpect(status().isCreated())
  //                 .andExpect(content().contentType("application/json"))
  //                 .andExpect(jsonPath("$.name").value("Station 1"))
  //                 .andExpect(jsonPath("$.latitude").value(12.34))
  //                 .andExpect(jsonPath("$.longitude").value(56.78));
  //     }
}
