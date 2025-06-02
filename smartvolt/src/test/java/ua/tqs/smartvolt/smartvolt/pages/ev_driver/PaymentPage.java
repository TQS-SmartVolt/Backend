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
}
