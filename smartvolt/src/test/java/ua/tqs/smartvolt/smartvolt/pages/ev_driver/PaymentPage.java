package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class PaymentPage extends Website {

  // Constructor
  public PaymentPage(WebDriver driver) {
    super(driver);
  }

  // --- Web Elements for Payment Details Display ---
  @FindBy(css = "[data-testid='payment-date']")
  private WebElement paymentDateDisplay;

  @FindBy(css = "[data-testid='payment-charging-power']")
  private WebElement paymentChargingPowerDisplay;

  @FindBy(css = "[data-testid='payment-duration']")
  private WebElement paymentDurationDisplay;

  @FindBy(css = "[data-testid='payment-energy-delivered']")
  private WebElement paymentEnergyDeliveredDisplay;

  @FindBy(css = "[data-testid='payment-price-per-kwh']")
  private WebElement paymentPricePerKWhDisplay;

  @FindBy(css = "[data-testid='payment-total-cost']")
  private WebElement paymentTotalCostDisplay;

  @FindBy(css = "[data-testid='card-number-input']")
  private WebElement cardNumberInput;

  @FindBy(css = "[data-testid='expiration-date-input']")
  private WebElement expirationDateInput;

  @FindBy(css = "[data-testid='cvc-input']")
  private WebElement cvcInput;

  @FindBy(css = "[data-testid='confirm-payment-button']")
  private WebElement confirmPaymentButton;

  @FindBy(css = "[data-testid='booking-confirmation-dialog']")
  private WebElement paymentConfirmationDialog;

  @FindBy(css = "[data-testid='confirm-booking-button']")
  private WebElement dialogConfirmButton;

  // --- Page Actions & Assertions ---

  /**
   * Checks if the payment details page is displayed. Assumes the URL will contain
   * "/payment-details" after successful booking.
   *
   * @return true if the URL matches the payment details page.
   */
  public boolean isPaymentDetailsPageDisplayed() {
    System.out.println("DEBUG: PaymentPage.isPaymentDetailsPageDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.urlContains("/payment")); // Adjust URL if different
      System.out.println("DEBUG: PaymentPage.isPaymentDetailsPageDisplayed() - URL is /payment.");
      return true;
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.isPaymentDetailsPageDisplayed() - Not on payment details page: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Verifies that all expected payment details fields are visible on the page.
   *
   * @return true if all specified data elements are displayed, false otherwise.
   */
  public boolean arePaymentDetailsDisplayed() {
    System.out.println(
        "DEBUG: PaymentPage.arePaymentDetailsDisplayed() - Verifying all payment detail elements.");
    try {
      boolean dateDisplayed =
          wait.until(ExpectedConditions.visibilityOf(paymentDateDisplay)).isDisplayed();
      boolean chargingPowerDisplayed =
          wait.until(ExpectedConditions.visibilityOf(paymentChargingPowerDisplay)).isDisplayed();
      boolean durationDisplayed =
          wait.until(ExpectedConditions.visibilityOf(paymentDurationDisplay)).isDisplayed();
      boolean energyDeliveredDisplayed =
          wait.until(ExpectedConditions.visibilityOf(paymentEnergyDeliveredDisplay)).isDisplayed();
      boolean pricePerKWhDisplayed =
          wait.until(ExpectedConditions.visibilityOf(paymentPricePerKWhDisplay)).isDisplayed();
      boolean totalCostDisplayed =
          wait.until(ExpectedConditions.visibilityOf(paymentTotalCostDisplay)).isDisplayed();

      if (dateDisplayed
          && chargingPowerDisplayed
          && durationDisplayed
          && energyDeliveredDisplayed
          && pricePerKWhDisplayed
          && totalCostDisplayed) {
        System.out.println(
            "DEBUG: PaymentPage.arePaymentDetailsDisplayed() - All payment details elements are visible.");
        return true;
      } else {
        System.err.println(
            "ERROR: PaymentPage.arePaymentDetailsDisplayed() - Some payment details elements are not visible:");
        System.err.println("  Date Displayed: " + dateDisplayed);
        System.err.println("  Charging Power Displayed: " + chargingPowerDisplayed);
        System.err.println("  Duration Displayed: " + durationDisplayed);
        System.err.println("  Energy Delivered Displayed: " + energyDeliveredDisplayed);
        System.err.println("  Price per kWh Displayed: " + pricePerKWhDisplayed);
        System.err.println("  Total Cost Displayed: " + totalCostDisplayed);
        return false;
      }
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.arePaymentDetailsDisplayed() - Failed to verify payment details visibility: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Fills in the payment form with the provided card details.
   *
   * @param cardNumber The card number to enter.
   * @param expirationDate The expiration date in MM/YY format.
   * @param cvc The CVC code for the card.
   */
  public void fillPaymentForm(String cardNumber, String expirationDate, String cvc) {
    System.out.println("DEBUG: PaymentPage.fillPaymentForm() - Filling payment form.");
    try {
      wait.until(ExpectedConditions.visibilityOf(cardNumberInput)).sendKeys(cardNumber);
      wait.until(ExpectedConditions.visibilityOf(expirationDateInput)).sendKeys(expirationDate);
      wait.until(ExpectedConditions.visibilityOf(cvcInput)).sendKeys(cvc);
      System.out.println(
          "DEBUG: PaymentPage.fillPaymentForm() - Payment form filled successfully.");
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.fillPaymentForm() - Failed to fill payment form: " + e.getMessage());
    }
  }

  /** Clicks the confirm payment button to submit the payment. */
  public void confirmPayment() {
    System.out.println("DEBUG: PaymentPage.confirmPayment() - Clicking confirm payment button.");
    try {
      wait.until(ExpectedConditions.elementToBeClickable(confirmPaymentButton)).click();
      System.out.println("DEBUG: PaymentPage.confirmPayment() - Payment confirmed successfully.");
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.confirmPayment() - Failed to confirm payment: " + e.getMessage());
    }
  }

  /**
   * Checks if the payment confirmation dialog is displayed.
   *
   * @return true if the payment confirmation dialog is visible, false otherwise.
   */
  public boolean isPaymentConfirmationDialogDisplayed() {
    System.out.println(
        "DEBUG: PaymentPage.isPaymentConfirmationDialogDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.visibilityOf(paymentConfirmationDialog));
      System.out.println(
          "DEBUG: PaymentPage.isPaymentConfirmationDialogDisplayed() - Payment confirmation dialog is visible.");
      return paymentConfirmationDialog.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.isPaymentConfirmationDialogDisplayed() - Payment confirmation dialog not displayed: "
              + e.getMessage());
      return false;
    }
  }

  /** Clicks the confirm button in the payment confirmation dialog. */
  public void clickConfirmPaymentDialogButton() {
    System.out.println(
        "DEBUG: PaymentPage.clickConfirmPaymentDialogButton() - Clicking confirm button in payment dialog.");
    try {
      wait.until(ExpectedConditions.elementToBeClickable(dialogConfirmButton)).click();
      System.out.println(
          "DEBUG: PaymentPage.clickConfirmPaymentDialogButton() - Confirm button clicked successfully.");
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.clickConfirmPaymentDialogButton() - Failed to click confirm button: "
              + e.getMessage());
    }
  }
}
