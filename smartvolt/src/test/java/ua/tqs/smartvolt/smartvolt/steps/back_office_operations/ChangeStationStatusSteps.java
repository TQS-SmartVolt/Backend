package ua.tqs.smartvolt.smartvolt.steps.back_office_operations;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.auth.LoginPage;
import ua.tqs.smartvolt.smartvolt.pages.operator.BackOfficePage;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ChangeStationStatusSteps {

  private WebDriver driver;
  private BackOfficePage backOfficePage;
  private LoginPage loginPage;
  private List<WebElement> stationCards;

  @Before
  public void setup() {
    System.out.println("Setting up WebDriver...");

    WebDriverManager.chromedriver().setup();
    this.driver = new ChromeDriver();
    this.backOfficePage = new BackOfficePage(driver);
    this.loginPage = new LoginPage(driver);
  }

  @After
  public void tearDown() {
    backOfficePage.quit();
  }

  @When("the operator clicks on {string} on station {int}")
  public void theOperatorClicksOn(String button, int stationIndex) {
    stationCards = backOfficePage.getStationCards();
    if (button.equals("Deactivate")) {
      backOfficePage.clickDeactivateStation(stationCards.get(stationIndex));
    } else if (button.equals("Activate")) {
      backOfficePage.clickActivateStation(stationCards.get(stationIndex));
    } else {
      throw new IllegalArgumentException("Unknown button: " + button);
    }
  }

  @When("the operator fills in the form with reason {string}")
  public void theOperatorFillsInTheFormWithReason(String reason) {
    backOfficePage.fillDeactivationReason(reason);
  }

  @When("the operator confirms the {string} action")
  public void theOperatorConfirmsTheAction(String action) {
    if (action.equals("deactivation")) {
      backOfficePage.confirmDeactivation();
    } else if (action.equals("activation")) {
      backOfficePage.confirmActivation();
    } else {
      throw new IllegalArgumentException("Unknown action: " + action);
    }
  }

  @Then("station {int} should have the status {string}")
  public void stationShouldHaveTheStatus(int stationIndex, String status) {
    WebElement stationCard = stationCards.get(stationIndex);
    assertEquals(
        status,
        backOfficePage.getStationCardAvailability(stationCard),
        "The station status does not match the expected value: " + status);
  }
}
