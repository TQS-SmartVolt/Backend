package ua.tqs.smartvolt.smartvolt.pages.auth;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Wait;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class LoginPage extends Website {

  @FindBy(css = "[data-testid='login-email-input']")
  private WebElement loginEmailInput;

  @FindBy(css = "[data-testid='login-password-input']")
  private WebElement loginPasswordInput;

  @FindBy(css = "[data-testid='login-submit-button']")
  private WebElement loginSubmitButton;

  public LoginPage() {
    super();
  }

  public LoginPage(WebDriver driver, Wait<WebDriver> wait) {
    super(driver, wait);
  }

  // Access methods for WebElements
  public void login(String email, String password) {
    navigateTo("/login");

    // Email
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='login-email-input']")));
    loginEmailInput.sendKeys(email);

    // Password
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid='login-password-input']")));
    loginPasswordInput.sendKeys(password);

    // Submit
    wait.until(
        ExpectedConditions.elementToBeClickable(
            By.cssSelector("[data-testid='login-submit-button']")));
    loginSubmitButton.click();

    driver.manage().timeouts().implicitlyWait(java.time.Duration.ofSeconds(10));
  }
}
