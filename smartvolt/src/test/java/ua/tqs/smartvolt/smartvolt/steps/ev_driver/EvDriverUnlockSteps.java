package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertTrue;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.UnlockPage; // Still needed for booking actions
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.PaymentPage; // New PaymentPage
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverUnlockSteps {
  private final TestContext context;
  private final UnlockPage unlockPage;

  public EvDriverUnlockSteps(TestContext context) {
    this.context = context;
    this.unlockPage = this.context.getUnlockPage();
  }

  @Then("I should be on the unlock slot page")
  public void iShouldBeOnTheUnlockSlotPage() {
    System.out.println(
        "DEBUG: EvDriverUnlockSteps.iShouldBeOnTheUnlockSlotPage() - Verifying unlock page.");
    assertTrue(unlockPage.isUnlockPageDisplayed(), "Should be on the unlock slot page.");
  } 

  // @And("I should see an entry for {string} in the slot list")
  // public void iShouldSeeAnEntryForInTheSlotList(String time) {
  //   System.out.println(
  //       "DEBUG: EvDriverUnlockSteps.iShouldSeeAnEntryForInTheSlotList() - Verifying slot entry for time: "
  //           + time);
  //   assertTrue(
  //       unlockPage.isSlotEntryDisplayed(time),
  //       "Slot entry for time " + time + " should be displayed.");
  // }


}