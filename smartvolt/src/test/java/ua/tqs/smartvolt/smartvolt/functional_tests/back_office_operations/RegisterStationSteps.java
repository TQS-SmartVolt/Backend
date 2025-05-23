package ua.tqs.smartvolt.smartvolt.functional_tests.back_office_operations;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class RegisterStationSteps {
  private WebDriver driver;
  private WebDriverWait wait;

  @Before
  public void setUp() {
    System.out.println("Setting up WebDriver...");
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
    wait = new WebDriverWait(driver, Duration.ofSeconds(10));
  }

  @After
  public void tearDown() {
    if (driver != null) {
      driver.quit();
    }
  }

  @Given("I am on the operator page")
  public void i_am_on_the_operator_page() {
    driver.get("http://localhost:5173/operator");
    wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("add_station")));
  }

  @When("I add a station with name {string} latitude {double} longitude {double}")
  public void i_add_a_station_with_name_latitude_longitude(
      String name, double latitude, double longitude) {
    driver.findElement(By.id("add_station")).click();

    // Wait for the modal to appear
    WebElement modal =
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("action-modal")));
    modal.findElement(By.id("station-name")).sendKeys(name);
    modal.findElement(By.id("station-latitude")).sendKeys(String.valueOf(latitude));
    modal.findElement(By.id("station-longitude")).sendKeys(String.valueOf(longitude));

    modal.findElement(By.id("confirm-add-station")).click();
  }

  @Then("I should see the station in the list with name {string} status {string}")
  public void i_should_see_the_station_in_the_list_with_name_status(String name, String status) {

    // Wait for message to disappear
    wait.until(ExpectedConditions.invisibilityOfElementLocated(By.id("no-stations-message")));

    List<WebElement> stationCards =
        driver.findElements(By.cssSelector("[data-testid^='station-card-']"));

    boolean found = false;
    for (WebElement card : stationCards) {
      WebElement titleElement = card.findElement(By.cssSelector("[data-testid='station-title']"));
      String stationName = titleElement.getText();

      List<WebElement> labelElements =
          card.findElements(By.cssSelector("[data-testid^='station-label-']"));
      boolean statusMatches =
          labelElements.stream().anyMatch(label -> label.getText().equalsIgnoreCase(status));

      if (stationName.equals(name) && statusMatches) {
        found = true;
        break;
      }
    }
    assertTrue(
        found,
        "Station with name '" + name + "' and status '" + status + "' not found in the list.");
  }
}
