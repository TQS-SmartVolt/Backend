package ua.tqs.smartvolt.smartvolt.steps.common;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.AbstractDriverOptions;
import org.testcontainers.containers.BrowserWebDriverContainer;
import ua.tqs.smartvolt.smartvolt.MyTestConfiguration;

public class BrowserSelenium {
  protected BrowserWebDriverContainer<?> browser;
  protected AbstractDriverOptions<?> options;

  private static String browserType = MyTestConfiguration.getBrowserType();

  public BrowserSelenium(BrowserWebDriverContainer<?> browser, AbstractDriverOptions<?> options) {
    this.browser = browser;
    this.options = options;
  }

  public static WebDriver getWebDriver() {
    switch (browserType.toLowerCase()) {
      case "chrome":
        WebDriverManager.chromedriver().setup();
        return new ChromeDriver();
      default:
        throw new IllegalArgumentException("Unsupported browser type: " + browserType);
    }
  }

  public static BrowserSelenium getBrowserContainer() {
    switch (browserType.toLowerCase()) {
      case "firefox":
        return getFirefox();
      case "chrome":
        return getChrome();
      case "edge":
        return getEdge();
      default:
        throw new IllegalArgumentException("Unsupported browser type: " + browserType);
    }
  }

  @SuppressWarnings("resource")
  private static BrowserSelenium getFirefox() {
    return new BrowserSelenium(
        new BrowserWebDriverContainer<>().withCapabilities(new FirefoxOptions()),
        new FirefoxOptions());
  }

  @SuppressWarnings("resource")
  private static BrowserSelenium getChrome() {
    return new BrowserSelenium(
        new BrowserWebDriverContainer<>().withCapabilities(new ChromeOptions()),
        new ChromeOptions());
  }

  @SuppressWarnings("resource")
  private static BrowserSelenium getEdge() {
    return new BrowserSelenium(
        new BrowserWebDriverContainer<>().withCapabilities(new EdgeOptions()),
        new EdgeOptions()); // Edge options can be set similarly
  }
}
