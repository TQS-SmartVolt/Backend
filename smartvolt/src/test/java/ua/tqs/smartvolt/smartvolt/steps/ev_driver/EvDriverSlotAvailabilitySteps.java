package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And; // You might need this if you add more 'And' steps later
import io.cucumber.java.en.Then;
// You might need this if you add more 'When' steps later
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.BookingPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverSlotAvailabilitySteps {

  private final TestContext context;
  private final BookingPage bookingPage; // Declare the BookingPage

  public EvDriverSlotAvailabilitySteps(TestContext context) {
    this.context = context;
    this.bookingPage = this.context.getBookingPage(); // Get BookingPage from context
  }

  @Then(
      "I should see the booking page elements for {string} and address {string}") // <-- ADDED "and
  // address
  // {string}"
  public void iShouldSeeTheBookingPageElementsFor(
      String stationName, String address) { // <-- ADDED String address
    assertTrue(
        bookingPage.areBookingPageElementsDisplayedForStation(
            stationName, address), // <-- PASS address
        "Booking page elements for '"
            + stationName
            + "' and address '"
            + address
            + "' should be displayed.");
  }

  @And("I should filter the charging speed by {string}") // This is an action performed by the user
  public void iShouldFilterTheChargingSpeedBy(String speed) {
    System.out.println(
        "DEBUG: EvDriverSlotAvailabilitySteps.iShouldFilterTheChargingSpeedBy() - Filtering speed: "
            + speed);
    bookingPage.selectChargingSpeed(speed);
  }

  @Then("I should see a warning message")
  public void iShouldSeeAWarningMessage() {
    System.out.println(
        "DEBUG: EvDriverSlotAvailabilitySteps.iShouldSeeAWarningMessage() - Verifying warning message.");
    assertTrue(bookingPage.isNoSlotsMessageDisplayed(), "No available time slots for this speed.");
  }

  @And("I should see the available time slots displayed")
  public void iShouldSeeTheAvailableTimeSlotsDisplayed() {
    System.out.println(
        "DEBUG: EvDriverSlotAvailabilitySteps.iShouldSeeTheAvailableTimeSlotsDisplayed() - Verifying time slots display.");
    assertTrue(
        bookingPage.areTimeSlotsDisplayedWithClearStartTimes(),
        "Available time slots should be displayed with clear start times.");
  }
}
