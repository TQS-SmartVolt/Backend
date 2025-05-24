package ua.tqs.smartvolt.smartvolt.pages;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.Wait;
import org.openqa.selenium.support.ui.WebDriverWait;

public class Website {

  protected WebDriver driver;
  protected Wait<WebDriver> wait;

  private String WEBSITE_URL = "http://localhost"; // TODO: .env
  private int WEB_DELAY = 5; // TODO: .env

  public Website() {
    WebDriverManager.chromedriver().setup();
    this.driver = new ChromeDriver();
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
}
