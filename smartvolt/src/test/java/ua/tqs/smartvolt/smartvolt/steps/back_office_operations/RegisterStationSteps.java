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
import ua.tqs.smartvolt.smartvolt.pages.back_office_operations.BackOfficePage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterStationSteps {

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

  @Then("stations should have been found")
  public void stationsShouldHaveBeenFound() {
    stationCards = backOfficePage.getStationCards();
    assertTrue(
        !stationCards.isEmpty(),
        "No station cards found. At least one station should be registered.");
  }

  @Then(
      "a station should have the name {string}, status {string}, address {string}, and {int} slots")
  public void stationShouldHaveTheNameStatusAddressAndSlots(
      String name, String status, String address, int slots) {
    WebElement stationCard = backOfficePage.getStationCardByName(name);

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
