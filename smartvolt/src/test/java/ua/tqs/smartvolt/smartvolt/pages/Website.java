package ua.tqs.smartvolt.smartvolt.pages;

import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.springframework.beans.factory.annotation.Value;

public class Website {

  protected WebDriver driver;
  protected Wait<WebDriver> wait;

  @Value("${FRONTEND_PORT}")
  private String frontendPort;

  @Value("${FRONTEND_IP}")
  private String frontendIp;

  @Value("${FRONTEND_PROTOCOL}")
  private String frontendprotocol;

  private String websiteUrl;

  @Value("${UAT_WEB_DELAY_SECONDS}")
  private int UAT_WEB_DELAY_SECONDS;

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
