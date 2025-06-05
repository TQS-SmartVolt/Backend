package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class UnlockPage extends Website {

  // Constructor
  public UnlockPage(WebDriver driver) {
    super(driver);
  }

  // --- Web Elements for Payment Details Display ---
  @FindBy(css = "[data-testid='station-card-0']")
  private WebElement stationCardIndex0;

  // --- Page Actions & Assertions ---

  /**
   * Checks if the unlock page is displayed. Assumes the URL will contain "/service/unlock" after
   * successful booking.
   *
   * @return true if the URL matches the unlock page.
   */
  public boolean isUnlockPageDisplayed() {
    System.out.println("DEBUG: PaymentPage.isUnlockPageDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.urlContains("/service/unlock"));
      System.out.println("DEBUG: PaymentPage.isUnlockPageDisplayed() - URL is /service/unlock.");
      return true;
    } catch (Exception e) {
      System.err.println(
          "ERROR: PaymentPage.isUnlockPageDisplayed() - Not on unlock page: " + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if the slot entry for the given time is displayed.
   *
   * @param time The time to check for in the slot list.
   * @return true if the slot entry is displayed, false otherwise.
   */
  public boolean isSlotEntryDisplayed(String time) {
    System.out.println(
        "DEBUG: UnlockPage.isSlotEntryDisplayed() - Checking slot entry for time: " + time);
    try {
      WebElement slotEntry =
          driver.findElement(By.cssSelector("[data-testid^='station-card-" + time + "']"));
      return slotEntry.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: UnlockPage.isSlotEntryDisplayed() - Slot entry not found: " + e.getMessage());
      return false;
    }
  }
}
