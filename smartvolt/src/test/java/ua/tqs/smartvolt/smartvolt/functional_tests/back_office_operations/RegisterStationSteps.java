package ua.tqs.smartvolt.smartvolt.functional_tests.back_office_operations;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;

public class RegisterStationSteps {
  private WebDriver driver;

  @Before
  public void setUp() {
    WebDriverManager.chromedriver().setup();
    driver = new ChromeDriver();
    driver.manage().window().maximize();
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
  }
}
