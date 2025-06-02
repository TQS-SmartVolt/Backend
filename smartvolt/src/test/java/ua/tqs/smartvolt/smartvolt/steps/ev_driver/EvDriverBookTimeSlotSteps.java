package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.BookingPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverBookTimeSlotSteps {

  private final TestContext context;
  private final BookingPage bookingPage;

  public EvDriverBookTimeSlotSteps(TestContext context) {
    this.context = context;
    this.bookingPage = this.context.getBookingPage();
  }

  @When("I select the date tomorrow")
  public void iSelectTheDate() {
    System.out.println(
        "DEBUG: EvDriverBookTimeSlotSteps.iSelectTheDate() - Selecting date tomorrow.");
    bookingPage.selectDate();
  }

  @And(
      "I click the {string} button on the booking page") // Reusing a generic step for button clicks
  public void iClickTheButtonOnBookingPage(String buttonText) {
    System.out.println(
        "DEBUG: EvDriverBookTimeSlotSteps.iClickTheButton() - Clicking button: " + buttonText);

    if ("Confirm Booking".equals(buttonText)) {
      bookingPage.clickConfirmBookingButton();
    } else {
      throw new RuntimeException(
          "Button with text '" + buttonText + "' not recognized for clicking.");
    }
  }

  @Then("I should see a warning message {string}")
  public void iShouldSeeAWarningMessage(String expectedMessage) {
    System.out.println(
        "DEBUG: EvDriverBookTimeSlotSteps.iShouldSeeAWarningMessage() - Verifying warning message: '"
            + expectedMessage
            + "'");
    assertTrue(
        bookingPage.isWarningMessageDisplayed(expectedMessage),
        "Warning message '" + expectedMessage + "' should be displayed.");
  }
}
