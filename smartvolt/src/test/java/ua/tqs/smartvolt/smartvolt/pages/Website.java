package ua.tqs.smartvolt.smartvolt.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Website {

  protected WebDriver driver;
  protected Wait<WebDriver> wait;

  private String frontendprotocol = "http";
  private String frontendIp = "localhost";
  private String frontendPort = "80";

  private String websiteUrl;

  private int UAT_WEB_DELAY_SECONDS = 1;

  public Website(WebDriver driver) {
    this.driver = driver;
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(UAT_WEB_DELAY_SECONDS));
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(UAT_WEB_DELAY_SECONDS));
    PageFactory.initElements(driver, this);

    this.websiteUrl = String.format("%s://%s:%s", frontendprotocol, frontendIp, frontendPort);
  }

  public void quit() {
    driver.quit();
  }

  public void navigateTo(String page) {
    driver.get(websiteUrl + page);
  }

  public WebDriver getWebDriver() {
    return driver;
  }

  public Wait<WebDriver> getWait() {
    return wait;
  }
}
