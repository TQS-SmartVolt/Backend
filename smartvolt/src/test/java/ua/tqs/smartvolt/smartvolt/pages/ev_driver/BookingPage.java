package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;
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
  private By dateGridTitleBy = By.cssSelector("[data-testid='gridTitle-Select a Date']");
  private By timeSlotGridTitleBy = By.cssSelector("[data-testid='gridTitle-Available Time Slots']");
  private By noSlotsMessageBy = By.cssSelector("[data-testid='no-slots-message']");

  // REFINED LOCATORS for grid buttons to be more specific to their sections
  // This targets grid-buttons within the 'Select a Date' grid's content div
  private By dateGridButtonsBy =
      By.cssSelector("[data-testid='gridTitle-Select a Date'] ~ div [data-test-id='grid-button']");
  // This targets grid-buttons within the 'Available Time Slots' grid's content div
  private By timeSlotGridButtonsBy =
      By.cssSelector(
          "[data-testid='gridTitle-Available Time Slots'] ~ div [data-test-id='grid-button']");

  // --- Web Elements for SimpleButton component ---
  @FindBy(css = "[data-testid='simple-button']")
  private WebElement confirmBookingButton;

  // --- New: Warning message element ---
  @FindBy(css = "[data-testid='booking-warning-message']")
  private WebElement warningMessageDisplay;

  // --- Web Elements for Booking Confirmation Dialog ---
  @FindBy(css = "[data-testid='booking-confirmation-dialog']")
  private WebElement bookingConfirmationDialog;

  @FindBy(css = "[data-testid='confirm-payment-button']")
  private WebElement confirmPaymentButton;

  @FindBy(css = "[data-testid='cancel-booking-button']")
  private WebElement cancelBookingButton;

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
      wait.until(ExpectedConditions.visibilityOfElementLocated(timeSlotGridTitleBy));
      System.out.println(
          "DEBUG: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - Time Slot Grid title is present.");

      // Use the more specific locator for time slot buttons
      List<WebElement> timeSlotButtons = driver.findElements(timeSlotGridButtonsBy);

      if (timeSlotButtons.isEmpty()) {
        System.err.println(
            "ERROR: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - No time slot buttons found.");
        return false;
      }

      for (WebElement button : timeSlotButtons) {
        wait.until(ExpectedConditions.visibilityOf(button));
        String slotText = button.getText().trim();
        System.out.println("DEBUG: Found time slot: '" + slotText + "'");
        if (slotText.isEmpty()) {
          System.err.println(
              "ERROR: BookingPage.areTimeSlotsDisplayedWithClearStartTimes() - Found an empty time slot button.");
          return false;
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
   * @param expectedAddressValue The expected address of the station to verify on the page.
   * @return true if key booking page elements are visible, false otherwise.
   */
  public boolean areBookingPageElementsDisplayedForStation(
      String expectedStationName, String expectedAddressValue) {
    System.out.println(
        "DEBUG: BookingPage.areBookingPageElementsDisplayedForStation() - Verifying elements for station '"
            + expectedStationName
            + "' and address '"
            + expectedAddressValue
            + "'.");
    try {
      wait.until(ExpectedConditions.urlContains("/booking"));
      System.out.println(
          "DEBUG: BookingPage.areBookingPageElementsDisplayedForStation() - URL is /booking.");

      boolean stationNamePresentAndCorrect = isStationNameDisplayed(expectedStationName);
      boolean chargingSpeedSelectPresent = isChargingSpeedSelectDisplayed();
      boolean powerValuePresent = isPowerDisplayDisplayed();
      boolean addressValuePresentAndCorrect = isAddressDisplayDisplayed(expectedAddressValue);

      boolean dateGridTitlePresent = isDateSelectionGridDisplayed();
      boolean timeGridOrNoSlotsMessagePresent =
          isTimeSlotSelectionGridDisplayed() || isNoSlotsMessageDisplayed();
      boolean confirmButtonPresent = isConfirmBookingButtonDisplayed();

      if (stationNamePresentAndCorrect
          && chargingSpeedSelectPresent
          && powerValuePresent
          && addressValuePresentAndCorrect
          && dateGridTitlePresent
          && timeGridOrNoSlotsMessagePresent
          && confirmButtonPresent) {
        System.out.println(
            "DEBUG: BookingPage.areBookingPageElementsDisplayedForStation() - All main booking page elements are displayed and correct for '"
                + expectedStationName
                + "' and address '"
                + expectedAddressValue
                + "'.");
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
                + "\n  Date Grid Title: "
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

  // --- NEW METHODS FOR USER STORY 2.2 ---

  /**
   * Selects a date from the date selection grid. Assumes dates are displayed as "DayOfWeek\nMonth
   * Day" (e.g., "Monday\nJune 02") within grid-button elements. Calculate tomorrow's date (day of
   * week and month/day) and select it.
   */
  public void selectDate() {
    System.out.println("DEBUG: BookingPage.selectDate() - Selecting date for tomorrow.");
    String finalTargetDayOfWeek;
    String finalTargetMonthDay;

    LocalDate tomorrow = LocalDate.now().plusDays(1);
    finalTargetDayOfWeek = tomorrow.getDayOfWeek().getDisplayName(TextStyle.FULL, Locale.ENGLISH);
    finalTargetMonthDay = tomorrow.format(DateTimeFormatter.ofPattern("MMMM dd", Locale.ENGLISH));

    // Find all elements with the specific date grid button locator
    List<WebElement> dateButtons =
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(dateGridButtonsBy));

    WebElement selectedDateButton =
        dateButtons.stream()
            .filter(
                button -> {
                  String buttonText = button.getText().trim();
                  System.out.println(
                      "DEBUG: Checking date button text: '"
                          + buttonText
                          + "' for '"
                          + finalTargetDayOfWeek
                          + "' and '"
                          + finalTargetMonthDay
                          + "'");
                  return buttonText.contains(finalTargetDayOfWeek)
                      && buttonText.contains(finalTargetMonthDay);
                })
            .findFirst()
            .orElseThrow(
                () ->
                    new RuntimeException(
                        "Date '"
                            + finalTargetDayOfWeek
                            + " "
                            + finalTargetMonthDay
                            + "' not found in the date selection grid."));

    wait.until(ExpectedConditions.elementToBeClickable(selectedDateButton));
    selectedDateButton.click();
    System.out.println(
        "DEBUG: BookingPage.selectDate() - Date '"
            + finalTargetDayOfWeek
            + " "
            + finalTargetMonthDay
            + "' clicked.");
  }

  /**
   * Selects a time slot from the time selection grid. Assumes time slots are displayed as "HH:MM"
   * within grid-button elements.
   *
   * @param timeText The time slot to select, e.g., "10:00" or "14:30".
   */
  // BookingPage.java - inside selectTimeSlot method
  public void selectTimeSlot(String timeText) {
    System.out.println(
        "DEBUG: BookingPage.selectTimeSlot() - Attempting to select time slot: " + timeText);
    List<WebElement> timeSlotButtons =
        wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(timeSlotGridButtonsBy));

    if (timeSlotButtons.isEmpty()) {
      System.err.println(
          "ERROR: BookingPage.selectTimeSlot() - No time slot buttons found for selector: "
              + timeSlotGridButtonsBy);
      throw new RuntimeException("No time slot buttons available to select from.");
    }
    System.out.println(
        "DEBUG: BookingPage.selectTimeSlot() - Found "
            + timeSlotButtons.size()
            + " time slot buttons.");

    WebElement selectedTimeSlotButton =
        timeSlotButtons.stream()
            .filter(
                button -> {
                  String buttonActualText = button.getText().trim();
                  return buttonActualText.startsWith(timeText);
                })
            .findFirst()
            .orElseThrow(
                () -> {
                  System.err.println(
                      "ERROR: BookingPage.selectTimeSlot() - Failed to find time slot '"
                          + timeText
                          + "'.");
                  System.out.println("DEBUG: Listing all available time slot buttons:");
                  timeSlotButtons.forEach(
                      button -> System.out.println("  - '" + button.getText().trim() + "'"));
                  return new RuntimeException(
                      "Time slot '" + timeText + "' not found in the time selection grid.");
                });

    wait.until(ExpectedConditions.elementToBeClickable(selectedTimeSlotButton));
    selectedTimeSlotButton.click();
    System.out.println(
        "DEBUG: BookingPage.selectTimeSlot() - Time slot '" + timeText + "' clicked.");
  }

  /**
   * Checks if the booking confirmation dialog is displayed.
   *
   * @return true if the dialog is visible, false otherwise.
   */
  public boolean isBookingConfirmationDialogDisplayed() {
    System.out.println(
        "DEBUG: BookingPage.isBookingConfirmationDialogDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.visibilityOf(bookingConfirmationDialog));
      System.out.println(
          "DEBUG: BookingPage.isBookingConfirmationDialogDisplayed() - Booking confirmation dialog is visible.");
      return bookingConfirmationDialog.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isBookingConfirmationDialogDisplayed() - Booking confirmation dialog not displayed: "
              + e.getMessage());
      return false;
    }
  }

  /** Clicks the "Confirm Payment" button inside the confirmation dialog. */
  public void clickConfirmPaymentButton() {
    System.out.println(
        "DEBUG: BookingPage.clickConfirmPaymentButton() - Clicking Confirm Payment button.");
    wait.until(ExpectedConditions.elementToBeClickable(confirmPaymentButton));
    confirmPaymentButton.click();
  }

  /** Clicks the "Cancel Booking" button inside the confirmation dialog. */
  public void clickCancelBookingButton() {
    System.out.println(
        "DEBUG: BookingPage.clickCancelBookingButton() - Clicking Cancel Booking button.");
    wait.until(ExpectedConditions.elementToBeClickable(cancelBookingButton));
    cancelBookingButton.click();
  }

  /**
   * Checks if a specific warning message is displayed.
   *
   * @param expectedMessage The expected text of the warning message.
   * @return true if the warning message is visible and contains the expected text, false otherwise.
   */
  public boolean isWarningMessageDisplayed(String expectedMessage) {
    System.out.println(
        "DEBUG: BookingPage.isWarningMessageDisplayed() - Verifying warning message: '"
            + expectedMessage
            + "'");
    try {
      wait.until(ExpectedConditions.visibilityOf(warningMessageDisplay));
      String actualMessage = warningMessageDisplay.getText().trim();
      System.out.println(
          "DEBUG: BookingPage.isWarningMessageDisplayed() - Found warning message: '"
              + actualMessage
              + "'");
      return actualMessage.contains(expectedMessage);
    } catch (Exception e) {
      System.err.println(
          "ERROR: BookingPage.isWarningMessageDisplayed() - Warning message not displayed or text mismatch: "
              + e.getMessage());
      return false;
    }
  }
}
