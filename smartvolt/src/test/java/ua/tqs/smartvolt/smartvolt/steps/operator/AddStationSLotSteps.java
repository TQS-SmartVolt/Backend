package ua.tqs.smartvolt.smartvolt.steps.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.util.List;
import org.openqa.selenium.WebElement;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.operator.BackOfficePage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class AddStationSLotSteps {

  private final TestContext context;
  private BackOfficePage backOfficePage;
  private List<WebElement> stationCards;

  public AddStationSLotSteps(TestContext context) {
    this.context = context;
    this.backOfficePage = this.context.getBackOfficePage();
  }

  @When("the operator clicks on Add for station {int}")
  public void theOperatorClicksOnAddForStation(int stationIndex) {
    stationCards = backOfficePage.getStationCards();
    backOfficePage.clickAddSlotButton(stationCards.get(stationIndex));
  }

  @When("the operator fills in the form with price {double} and charging speed {string}")
  public void theOperatorFillsInTheFormWithPriceAndChargingSpeed(double price, String speed) {
    backOfficePage.fillPriceForNewSlot(price);
    backOfficePage.fillChargingSpeedForNewSlot(speed);
  }

  @When("the operator confirms the addition of the slot")
  public void theOperatorConfirmsTheAdditionOfTheSlot() {
    backOfficePage.confirmAddSlot();
  }

  @Then("station {int} should have {int} slots")
  public void stationShouldHaveSlots(int stationIndex, int expectedSlotCount) {
    stationCards = backOfficePage.getStationCards();
    WebElement stationCard = stationCards.get(stationIndex);
    int actualSlotCount = backOfficePage.getStationCardNumSlots(stationCard);
    assertEquals(
        expectedSlotCount,
        actualSlotCount,
        "The number of slots for station "
            + stationIndex
            + " does not match the expected value: "
            + expectedSlotCount);
  }
}
