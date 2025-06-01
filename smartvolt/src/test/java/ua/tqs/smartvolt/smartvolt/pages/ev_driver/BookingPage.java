package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class BookingPage extends Website {

  // Constructor
  public BookingPage(WebDriver driver) {
    super(driver);
  }

  // --- Web Elements for EVStationDetails component ---
  @FindBy(css = "[data-testid='stationName']")
  private WebElement stationNameDisplay;

  @FindBy(css = "[data-testid='chargingSpeedSelect']")
  private WebElement chargingSpeedSelect;

  @FindBy(css = "[data-testid='powerValue']")
  private WebElement powerDisplay;

  @FindBy(css = "[data-testid='addressValue']")
  private WebElement addressDisplay;

  // --- Web Elements for ScrollableOptionGrid components ---
  // Using dynamic data-testid for grid titles directly
  private By dateGridTitleBy = By.cssSelector("[data-testid='gridTitle-Select a Date']");
  private By timeSlotGridTitleBy = By.cssSelector("[data-testid='gridTitle-Available Time Slots']");
  private By noSlotsMessageBy = By.cssSelector("[data-testid='no-slots-message']");

  private By timeSlotGridButtonBy = By.cssSelector("[data-test-id='grid-button']");

  // --- Web Elements for SimpleButton component ---
  // Assuming the Confirm Booking button uses the default "simpleButton" if no specific testId is
  // passed
  @FindBy(css = "[data-testid='simple-button']")
  private WebElement confirmBookingButton;

  // --- Page Actions & Assertions ---

  public boolean isStationNameDisplayed(String expectedStationName) {
    System.out.println(
        "DEBUG: BookingPage.isStationNameDisplayed() - Entering method. Expected: "
            + expectedStationName);
    try {
      wait.until(
          ExpectedConditions.textToBePresentInElement(stationNameDisplay, expectedStationName));
      System.out.println(
          "DEBUG: BookingPage.isStationNameDisplayed() - Station name '"
              + expectedStationName
              + "' is visible.");
      return stationNameDisplay.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isStationNameDisplayed() - Station name not displayed or text mismatch: "
              + e.getMessage());
      return false;
    }
  }

  public boolean isChargingSpeedSelectDisplayed() {
    System.out.println("DEBUG: BookingPage.isChargingSpeedSelectDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.visibilityOf(chargingSpeedSelect));
      System.out.println(
          "DEBUG: BookingPage.isChargingSpeedSelectDisplayed() - Charging Speed select is visible.");
      return chargingSpeedSelect.isDisplayed() && chargingSpeedSelect.isEnabled();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isChargingSpeedSelectDisplayed() - Charging Speed select not displayed: "
              + e.getMessage());
      return false;
    }
  }

  public void selectChargingSpeed(String speed) {
    System.out.println("DEBUG: BookingPage.selectChargingSpeed() - Selecting speed: " + speed);
    try {
      wait.until(ExpectedConditions.elementToBeClickable(chargingSpeedSelect));
      Select dropdown = new Select(chargingSpeedSelect);
      dropdown.selectByVisibleText(speed);
      System.out.println(
          "DEBUG: BookingPage.selectChargingSpeed() - Speed '" + speed + "' selected.");
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.selectChargingSpeed() - Failed to select speed '"
              + speed
              + "': "
              + e.getMessage());
      throw new RuntimeException("Could not select charging speed: " + speed, e);
    }
  }

  public boolean isPowerDisplayDisplayed() {
    System.out.println("DEBUG: BookingPage.isPowerDisplayDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.visibilityOf(powerDisplay));
      System.out.println(
          "DEBUG: BookingPage.isPowerDisplayDisplayed() - Power display is visible.");
      return powerDisplay.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isPowerDisplayDisplayed() - Power display not displayed: "
              + e.getMessage());
      return false;
    }
  }

  public boolean isAddressDisplayDisplayed(String expectedAddress) {
    System.out.println(
        "DEBUG: BookingPage.isAddressDisplayDisplayed() - Entering method. Expected: "
            + expectedAddress);
    try {
      wait.until(
          ExpectedConditions.textToBePresentInElement(
              addressDisplay, expectedAddress)); // Verify text
      System.out.println(
          "DEBUG: BookingPage.isAddressDisplayDisplayed() - Address '"
              + expectedAddress
              + "' is visible and matches.");
      return addressDisplay.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isAddressDisplayDisplayed() - Address display not displayed or text mismatch: "
              + e.getMessage());
      return false;
    }
  }

  public boolean isDateSelectionGridDisplayed() {
    System.out.println("DEBUG: BookingPage.isDateSelectionGridDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.visibilityOfElementLocated(dateGridTitleBy));
      System.out.println(
          "DEBUG: BookingPage.isDateSelectionGridDisplayed() - Date Selection Grid (via title) is visible.");
      return driver.findElement(dateGridTitleBy).isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isDateSelectionGridDisplayed() - Date Selection Grid (via title) not displayed: "
              + e.getMessage());
      return false;
    }
  }

  public boolean isTimeSlotSelectionGridDisplayed() {
    System.out.println("DEBUG: BookingPage.isTimeSlotSelectionGridDisplayed() - Entering method.");
    try {
      // Check for the "Available Time Slots" grid title
      wait.until(ExpectedConditions.visibilityOfElementLocated(timeSlotGridTitleBy));
      System.out.println(
          "DEBUG: BookingPage.isTimeSlotSelectionGridDisplayed() - Time Slot Selection Grid (via title) is visible.");
      return driver.findElement(timeSlotGridTitleBy).isDisplayed();
    } catch (Exception e) {
      System.out.println(
          "DEBUG: BookingPage.isTimeSlotSelectionGridDisplayed() - Time Slot Selection Grid (via title) not found (might be 'No slots' message instead).");
      return false;
    }
  }

  public boolean isNoSlotsMessageDisplayed() {
    System.out.println("DEBUG: BookingPage.isNoSlotsMessageDisplayed() - Entering method.");
    try {
      // This message is conditionally rendered when no slots are available.
      wait.until(ExpectedConditions.visibilityOfElementLocated(noSlotsMessageBy));
      System.out.println(
          "DEBUG: BookingPage.isNoSlotsMessageDisplayed() - 'No available time slots for this speed.' message is visible.");
      return driver.findElement(noSlotsMessageBy).isDisplayed();
    } catch (Exception e) {
      System.out.println(
          "DEBUG: BookingPage.isNoSlotsMessageDisplayed() - 'No available time slots for this speed.' message not found.");
      return false;
    }
  }

  // Check if time slots are displayed and have content
  public boolean areTimeSlotsDisplayedWithClearStartTimes() {
    System.out.println(
        "DEBUG: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - Entering method.");
    try {
      // First, ensure the time slot grid title is visible
      wait.until(ExpectedConditions.visibilityOfElementLocated(timeSlotGridTitleBy));
      System.out.println(
          "DEBUG: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - Time Slot Grid title is present.");

      // Find all elements with data-test-id='grid-button'
      List<WebElement> timeSlotButtons = driver.findElements(timeSlotGridButtonBy);

      // Ensure there's at least one slot
      if (timeSlotButtons.isEmpty()) {
        System.err.println(
            "ERROR: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - No time slot buttons found.");
        return false;
      }

      // Verify each button is visible and has non-empty text
      for (WebElement button : timeSlotButtons) {
        wait.until(ExpectedConditions.visibilityOf(button));
        String slotText = button.getText().trim();
        System.out.println("DEBUG: Found time slot: '" + slotText + "'");
        if (slotText.isEmpty()) {
          System.err.println(
              "ERROR: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - Found an empty time slot button.");
          return false; // Slot text is empty
        }
      }
      System.out.println(
          "DEBUG: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - All found time slots are visible and have clear text.");
      return true;
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - Error verifying time slots: "
              + e.getMessage());
      return false;
    }
  }

  public boolean isConfirmBookingButtonDisplayed() {
    System.out.println("DEBUG: BookingPage.isConfirmBookingButtonDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.visibilityOf(confirmBookingButton));
      System.out.println(
          "DEBUG: BookingPage.isConfirmBookingButtonDisplayed() - Confirm Booking button is visible.");
      return confirmBookingButton.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isConfirmBookingButtonDisplayed() - Confirm Booking button not displayed: "
              + e.getMessage());
      return false;
    }
  }

  public void clickConfirmBookingButton() {
    System.out.println(
        "DEBUG: BookingPage.clickConfirmBookingButton() - Clicking Confirm Booking button.");
    wait.until(ExpectedConditions.elementToBeClickable(confirmBookingButton));
    confirmBookingButton.click();
  }

  /**
   * Checks if the main elements of the booking page for a given station are displayed with the
   * correct station name and address.
   *
   * @param expectedStationName The expected name of the station to verify on the page.
   * @param expectedAddressValue The expected address of the station to verify on the page. <--
   *     ADDED
   * @return true if key booking page elements are visible, false otherwise.
   */
  public boolean areBookingPageElementsDisplayedForStation(
      String expectedStationName, String expectedAddressValue) { // <-- ADDED expectedAddressValue
    System.out.println(
        "DEBUG: BookingPage.areBookingPageElementsDisplayedForStation() - Verifying elements for station '"
            + expectedStationName
            + "' and address '"
            + expectedAddressValue
            + "'."); // <-- UPDATED LOG
    try {
      wait.until(ExpectedConditions.urlContains("/booking"));
      System.out.println(
          "DEBUG: BookingPage.areBookingPageElementsDisplayedForStation() - URL is /booking.");

      boolean stationNamePresentAndCorrect = isStationNameDisplayed(expectedStationName);
      boolean chargingSpeedSelectPresent = isChargingSpeedSelectDisplayed();
      boolean powerValuePresent = isPowerDisplayDisplayed();
      boolean addressValuePresentAndCorrect =
          isAddressDisplayDisplayed(expectedAddressValue); // <-- USE THE NEW METHOD WITH ARGUMENT

      boolean dateGridTitlePresent = isDateSelectionGridDisplayed();
      boolean timeGridOrNoSlotsMessagePresent =
          isTimeSlotSelectionGridDisplayed() || isNoSlotsMessageDisplayed();
      boolean confirmButtonPresent = isConfirmBookingButtonDisplayed();

      if (stationNamePresentAndCorrect
          && chargingSpeedSelectPresent
          && powerValuePresent
          && addressValuePresentAndCorrect
          && // <-- INCLUDE addressValuePresentAndCorrect
          dateGridTitlePresent
          && timeGridOrNoSlotsMessagePresent
          && confirmButtonPresent) {
        System.out.println(
            "DEBUG: BookingPage.areBookingPageElementsDisplayedForStation() - All main booking page elements are displayed and correct for '"
                + expectedStationName
                + "' and address '"
                + expectedAddressValue
                + "'."); // <-- UPDATED LOG
        return true;
      } else {
        System.err.println(
            "ERROR: BookingPage.areBookingPageElementsDisplayedForStation() - Some elements missing or incorrect for '"
                + expectedStationName
                + "':"
                + "\n  Station Name (Text Checked): "
                + stationNamePresentAndCorrect
                + "\n  Charging Speed Select: "
                + chargingSpeedSelectPresent
                + "\n  Power Value: "
                + powerValuePresent
                + "\n  Address Value (Text Checked): "
                + addressValuePresentAndCorrect
                + // <-- INCLUDE
                "\n  Date Grid Title: "
                + dateGridTitlePresent
                + "\n  Time Grid Title / No Slots Message: "
                + timeGridOrNoSlotsMessagePresent
                + "\n  Confirm Button: "
                + confirmButtonPresent);
        return false;
      }
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.areBookingPageElementsDisplayedForStation() - Failed to verify booking page elements: "
              + e.getMessage());
      return false;
    }
  }
}
