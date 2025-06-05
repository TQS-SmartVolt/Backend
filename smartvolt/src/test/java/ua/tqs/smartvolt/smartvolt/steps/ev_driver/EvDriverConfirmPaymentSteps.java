package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.BookingPage; // Still needed for booking actions
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.PaymentPage; // New PaymentPage
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverConfirmPaymentSteps {
  private final TestContext context;
  private final BookingPage bookingPage;
  private final PaymentPage paymentPage;

  public EvDriverConfirmPaymentSteps(TestContext context) {
    this.context = context;
    this.bookingPage = this.context.getBookingPage();
    this.paymentPage = this.context.getPaymentPage();
  }

  @When("I click the {string} button on the confirmation dialog")
  public void iClickTheButtonOnTheConfirmationDialog(String buttonText) {
    System.out.println(
        "DEBUG: EvDriverConfirmPaymentSteps.iClickTheButtonOnTheConfirmationDialog() - Clicking button: "
            + buttonText);
    if ("Confirm".equals(buttonText)) {
      bookingPage
          .clickConfirmConfirmationBookingButton(); // This method still clicks the element with
      // data-testid='confirm-payment-button'
    } else if ("Cancel Booking".equals(buttonText)) {
      bookingPage.clickCancelBookingButton();
    } else {
      throw new RuntimeException(
          "Button with text '" + buttonText + "' not recognized for confirmation dialog.");
    }
  }

  @Then("I should be on the payment details page")
  public void iShouldBeOnThePaymentDetailsPage() {
    System.out.println(
        "DEBUG: EvDriverConfirmPaymentSteps.iShouldBeOnThePaymentDetailsPage() - Verifying payment details page.");
    assertTrue(
        paymentPage.isPaymentDetailsPageDisplayed(), "Should be on the payment details page.");
  }

  @And("I should see the payment details displayed")
  public void iShouldSeeThePaymentDetailsDisplayed() {
    System.out.println(
        "DEBUG: EvDriverConfirmPaymentSteps.iShouldSeeThePaymentDetailsDisplayed() - Verifying payment details visibility.");
    assertTrue(
        paymentPage.arePaymentDetailsDisplayed(), "All payment details should be displayed.");
  }

  @And("I insert the card details with number {string}, expiration date {string}, and CVV {string}")
  public void iInsertTheCardDetailsWithNumberExpirationDateAndCVV(
      String cardNumber, String expirationDate, String cvv) {
    System.out.println(
        "DEBUG: EvDriverConfirmPaymentSteps.iInsertTheCardDetailsWithNumberExpirationDateAndCVV() - Inserting card details.");
    paymentPage.fillPaymentForm(cardNumber, expirationDate, cvv);
  }

  @And("I click the confirm payment button")
  public void iClickTheConfirmPaymentButton() {
    System.out.println(
        "DEBUG: EvDriverConfirmPaymentSteps.iClickTheConfirmPaymentButton() - Clicking confirm payment button.");
    paymentPage.confirmPayment();
  }

  @Then("I should see the payment confirmation dialog")
  public void iShouldSeeThePaymentConfirmationDialog() {
    System.out.println(
        "DEBUG: EvDriverConfirmPaymentSteps.iShouldSeeThePaymentConfirmationDialog() - Verifying payment confirmation dialog.");
    assertTrue(
        paymentPage.isPaymentConfirmationDialogDisplayed(),
        "Payment confirmation dialog should be displayed.");
  }
}
