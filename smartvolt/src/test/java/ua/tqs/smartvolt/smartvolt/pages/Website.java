package ua.tqs.smartvolt.smartvolt.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

public class Website {

  protected WebDriver driver;
  protected Wait<WebDriver> wait;

  private String websiteUrl;

  private int UAT_WEB_DELAY_SECONDS = 5;

  public Website(WebDriver driver) {
    this.driver = driver;
    driver.manage().window().maximize();
    driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(UAT_WEB_DELAY_SECONDS));
    this.wait = new WebDriverWait(driver, Duration.ofSeconds(UAT_WEB_DELAY_SECONDS));
    PageFactory.initElements(driver, this);

    this.websiteUrl =
        String.format(
            "%s://%s:%s",
            TestContext.FRONTEND_PROTOCOL, TestContext.FRONTEND_IP, TestContext.FRONTEND_PORT);
    System.out.println("Website URL: " + this.websiteUrl);
  }

  public void quit() {
    driver.quit();
  }

  public void navigateTo(String page) {
    System.out.println("Navigating to: " + websiteUrl + page);
    driver.get(websiteUrl + page);
  }

  public WebDriver getWebDriver() {
    return driver;
  }

  public Wait<WebDriver> getWait() {
    return wait;
  }

  public String getWebsiteUrl() {
    return websiteUrl;
  }
}
