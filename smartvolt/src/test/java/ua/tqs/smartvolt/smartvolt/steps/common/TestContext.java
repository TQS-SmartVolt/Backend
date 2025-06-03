package ua.tqs.smartvolt.smartvolt.steps.common;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import ua.tqs.smartvolt.smartvolt.MyTestConfiguration;
import ua.tqs.smartvolt.smartvolt.pages.Website;
import ua.tqs.smartvolt.smartvolt.pages.auth.LoginPage;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.BookingPage;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.PaymentPage;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ServiceStationsMapPage;
import ua.tqs.smartvolt.smartvolt.pages.operator.BackOfficePage;

public class TestContext {
  private WebDriver driver;
  private LoginPage loginPage;
  private BackOfficePage backOfficePage;
  private Website website;
  private ServiceStationsMapPage serviceStationsMapPage;
  private BookingPage bookingPage;
  private PaymentPage paymentPage;

  public static String FRONTEND_PROTOCOL = "http";
  public static String FRONTEND_IP = MyTestConfiguration.getHost();
  public static String FRONTEND_PORT = "80";

  private BrowserSelenium container;

  public void initialize() {

    if (FRONTEND_IP.equals("host.testcontainers.internal")) {

      // Testcontainers!
      container = BrowserSelenium.getBrowserContainer();
      org.testcontainers.Testcontainers.exposeHostPorts(Integer.parseInt(FRONTEND_PORT));
      container.browser.start();

      this.driver = new RemoteWebDriver(container.browser.getSeleniumAddress(), container.options);

    } else {
      this.driver = BrowserSelenium.getWebDriver();
    }

    this.loginPage = new LoginPage(driver);
    this.backOfficePage = new BackOfficePage(driver);
    this.serviceStationsMapPage = new ServiceStationsMapPage(driver);
    this.bookingPage = new BookingPage(driver);
    this.paymentPage = new PaymentPage(driver);
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

  public BookingPage getBookingPage() {
    return bookingPage;
  }

  public PaymentPage getPaymentPage() {
    return paymentPage;
  }

  public void quit() {
    if (driver != null) {
      driver.quit();
    }
    if (container != null && container.browser != null) {
      container.browser.stop();
      container.browser.close();
    }
  }
}
