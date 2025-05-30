package ua.tqs.smartvolt.smartvolt.steps.common;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import ua.tqs.smartvolt.smartvolt.pages.Website;
import ua.tqs.smartvolt.smartvolt.pages.auth.LoginPage;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ServiceStationsMapPage;
import ua.tqs.smartvolt.smartvolt.pages.operator.BackOfficePage;

public class TestContext {
  private WebDriver driver;
  private LoginPage loginPage;
  private BackOfficePage backOfficePage;
  private Website website;
  private ServiceStationsMapPage serviceStationsMapPage;

  public void initialize() {
    WebDriverManager.chromedriver().setup();
    this.driver = new ChromeDriver();
    this.loginPage = new LoginPage(driver);
    this.backOfficePage = new BackOfficePage(driver);
    this.serviceStationsMapPage = new ServiceStationsMapPage(driver);
    this.website = new Website(driver);
  }

  public WebDriver getDriver() {
    return driver;
  }

  public LoginPage getLoginPage() {
    return loginPage;
  }

  public BackOfficePage getBackOfficePage() {
    return backOfficePage;
  }

  public Website getWebsite() { // Add getter for Website
    return website;
  }

  public ServiceStationsMapPage getServiceStationsMapPage() {
    return serviceStationsMapPage;
  }

  public void quit() {
    if (driver != null) {
      driver.quit();
    }
  }
}
