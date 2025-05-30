package ua.tqs.smartvolt.smartvolt.steps.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.auth.LoginPage;
import ua.tqs.smartvolt.smartvolt.pages.operator.BackOfficePage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class RegisterStationSteps {

  private WebDriver driver;
  private BackOfficePage backOfficePage;
  private LoginPage loginPage;
  private List<WebElement> stationCards;

  @Before
  public void setup() {
    System.out.println("Setting up WebDriver...");

    WebDriverManager.chromedriver().setup();
    ChromeOptions options = new ChromeOptions();
    options.addArguments("--user-data-dir=/tmp/unique-user-data-dir-" +
    System.currentTimeMillis());
    this.driver = new ChromeDriver(options);
    this.backOfficePage = new BackOfficePage(driver);
    this.loginPage = new LoginPage(driver);
  }

  @After
  public void tearDown() {
    backOfficePage.quit();
  }

  @Given("the website is available at page {string}")
  public void givenTheWebsiteIsAvailableAtPage(String page) {
    backOfficePage.navigateTo(page);
  }

  @Given("the operator is logged in with email {string} and password {string}")
  public void theOperatorIsLoggedInWithEmailAndPassword(String email, String password) {
    loginPage.login(email, password, true);
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
      "the operator fills in the form with name {string}, address {string}, latitude {double}, and longitude {double}")
  public void theOperatorFillsInTheForm(
      String name, String address, double latitude, double longitude) {
    backOfficePage.fillStationDetails(name, address, latitude, longitude);
  }

  @When("the operator confirms the registration")
  public void theOperatorConfirmsTheRegistration() {
    backOfficePage.confirmAddStation();
  }

  @Then("{int} stations should have been found")
  @Then("{int} station should have been found")
  public void stationsShouldHaveBeenFound(int expectedCount) {
    stationCards = backOfficePage.getStationCards();
    assertTrue(
        stationCards.size() == expectedCount,
        "No station cards found. At least one station should be registered.");
  }

  @Then(
      "station {int} should have the name {string}, status {string}, address {string}, and {int} slots")
  public void stationShouldHaveTheNameStatusAddressAndSlots(
      int index, String name, String status, String address, int slots) {
    WebElement stationCard = backOfficePage.getStationByIndex(index);

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
