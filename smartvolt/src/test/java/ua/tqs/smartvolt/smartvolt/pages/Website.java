package ua.tqs.smartvolt.smartvolt.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Website {

  protected WebDriver driver;
  protected Wait<WebDriver> wait;

  private String WEBSITE_URL = "http://localhost"; // TODO: .env
  private int WEB_DELAY = 1; // TODO: .env

  public Website(WebDriver driver) {
    this.driver = driver;
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(WEB_DELAY));
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(WEB_DELAY));
    PageFactory.initElements(driver, this);
  }

  public void quit() {
    driver.quit();
  }

  public void navigateTo(String page) {
    driver.get(WEBSITE_URL + page);
  }

  public WebDriver getWebDriver() {
    return driver;
  }

  public Wait<WebDriver> getWait() {
    return wait;
  }
}
