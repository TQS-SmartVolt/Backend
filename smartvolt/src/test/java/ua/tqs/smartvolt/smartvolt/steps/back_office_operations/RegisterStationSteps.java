package ua.tqs.smartvolt.smartvolt.steps.back_office_operations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import ua.tqs.smartvolt.smartvolt.pages.back_office_operations.BackOfficePage;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("testcontainers")
public class RegisterStationSteps {

  // TODO: make this with .env so we can use a Staging with a real database
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

  private BackOfficePage backOfficePage;
  private List<WebElement> stationCards;

  @Before
  public void setup() {
    System.out.println("Setting up WebDriver...");
    this.backOfficePage = new BackOfficePage();
  }

  @After
  public void tearDown() {
    backOfficePage.quit();
  }

  @Given("the website is available at page {string}")
  public void givenTheWebsiteIsAvailableAtPage(String page) {
    backOfficePage.navigateTo(page);
  }

  @When("the operator clicks on {string}")
  public void theOperatorClicksOn(String button) {
    if (button.equals("Add Station")) {
      backOfficePage.clickAddStation();
    } else {
      throw new IllegalArgumentException("Unknown button: " + button);
    }
  }

  @When("the modal should {string}")
  public void theModalShouldAppear(String condition) {
    assertTrue(
        condition.equals("appear")
            ? backOfficePage.isActionModalVisible()
            : backOfficePage.isActionModalNotVisible(),
        "The modal visibility does not match the expected condition: " + condition);
  }

  @When(
      "the operator fills in the form with name {string}, latitude {double}, and longitude {double}")
  public void theOperatorFillsInTheForm(String name, double latitude, double longitude) {
    backOfficePage.fillStationDetails(name, latitude, longitude);
  }

  @When("the operator confirms the registration")
  public void theOperatorConfirmsTheRegistration() {
    backOfficePage.confirmAddStation();
  }

  @Then("{int} station should have been found")
  @Then("{int} stations should have been found")
  public void stationsShouldHaveBeenFound(int expectedCount) {
    stationCards = backOfficePage.getStationCards();
    assertEquals(
        expectedCount,
        stationCards.size(),
        "The number of stations found does not match the expected count.");
  }

  @Then(
      "station {int} should have the name {string}, status {string}, address {string}, and {int} slots")
  public void stationShouldHaveTheNameStatusAddressAndSlots(
      int stationIndex, String name, String status, String address, int slots) {
    WebElement stationCard = backOfficePage.getStationCardByIndex(stationIndex - 1);

    assertEquals(
        name,
        backOfficePage.getStationCardTitle(stationCard),
        "The station name does not match the expected value.");
    assertEquals(
        address,
        backOfficePage.getStationCardAddress(stationCard),
        "The station address does not match the expected value.");
    assertEquals(
        status,
        backOfficePage.getStationCardAvailability(stationCard),
        "The station status does not match the expected value.");
    assertEquals(
        slots,
        backOfficePage.getStationCardNumSlots(stationCard),
        "The number of slots in the station card does not match the expected value.");
  }
}
