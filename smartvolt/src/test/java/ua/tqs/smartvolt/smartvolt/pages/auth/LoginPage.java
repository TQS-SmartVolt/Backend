package ua.tqs.smartvolt.smartvolt.pages.auth;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class LoginPage extends Website {

  @FindBy(css = "[data-testid='login-email-input']")
  private WebElement loginEmailInput;

  @FindBy(css = "[data-testid='login-password-input']")
  private WebElement loginPasswordInput;

  @FindBy(css = "[data-testid='login-submit-button']")
  private WebElement loginSubmitButton;

  public LoginPage(WebDriver driver) {
    super(driver);
  }

  // Access methods for WebElements
  public void login(String email, String password, boolean isOperator) {
    navigateTo("/login");

    // Email
    System.out.println("Entering email: " + email);
    wait.until(ExpectedConditions.visibilityOf(loginEmailInput));
    loginEmailInput.sendKeys(email);

    // Password
    System.out.println("Entering password: " + password);
    wait.until(ExpectedConditions.visibilityOf(loginPasswordInput));
    loginPasswordInput.sendKeys(password);

    // Submit
    System.out.println("Clicking submit button");
    wait.until(ExpectedConditions.visibilityOf(loginSubmitButton));
    loginSubmitButton.click();

    System.out.println("Waiting for redirection after login...");
    if (isOperator) {
      System.out.println("Expecting redirection to operator page");
      wait.until(ExpectedConditions.urlContains("/operator"));
    } else {
      wait.until(ExpectedConditions.urlContains("/service/stations-map"));
    }
  }
}
