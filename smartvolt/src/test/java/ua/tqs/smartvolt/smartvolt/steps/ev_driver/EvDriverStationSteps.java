package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Given; // Keep this if using the new CommonSteps structure
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ServiceStationsMapPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverStationSteps {

  private final TestContext context;
  private ServiceStationsMapPage serviceStationsMapPage;

  public EvDriverStationSteps(TestContext context) {
    this.context = context;
    this.serviceStationsMapPage = this.context.getServiceStationsMapPage();
  }

  @Then("I should see the map displayed")
  public void iShouldSeeTheMapDisplayed() {
    assertTrue(serviceStationsMapPage.isMapDisplayed(), "The map should be displayed.");
  }

  @Then("I should see at least {int} charging station marker on the map")
  public void iShouldSeeAtLeastChargingStationMarkerOnTheMap(int expectedMinCount) {
    assertTrue(
        serviceStationsMapPage.getNumberOfStationMarkers() >= expectedMinCount,
        "Should see at least " + expectedMinCount + " station markers on the map.");
  }

  @When("I click on the {string} button")
  public void iClickOnTheButton(String buttonName) {
    if ("Filter".equalsIgnoreCase(buttonName)) {
      serviceStationsMapPage.clickFilterButton();
    } else {
      throw new IllegalArgumentException("Button not recognized: " + buttonName);
    }
  }

  @When("I select {string} charging speed filter")
  public void iSelectChargingSpeedFilter(String speed) {
    serviceStationsMapPage.selectChargingSpeedFilter(speed);
  }

  @Then("I should see {int} charging station marker on the map")
  public void iShouldSeeChargingStationMarkerOnTheMap(int expectedCount) {
    assertEquals(
        expectedCount,
        serviceStationsMapPage.getNumberOfStationMarkers(),
        "The number of station markers on the map does not match the expected count.");
  }

  @Given("there is at least {int} charging station marker on the map")
  public void thereIsAtLeastChargingStationMarkerOnTheMap(int expectedMinCount) {
    assertTrue(
        serviceStationsMapPage.getNumberOfStationMarkers() >= expectedMinCount,
        "Precondition failed: Should have at least "
            + expectedMinCount
            + " station markers on the map.");
  }

  @When("I click on the first charging station marker")
  public void iClickOnTheFirstChargingStationMarker() {
    serviceStationsMapPage.clickStationMarkerByIndex(0);
  }

  @Then("I should see the station details popup displayed")
  public void iShouldSeeTheStationDetailsPopupDisplayed() {
    assertTrue(
        serviceStationsMapPage.isStationDetailsPopupDisplayed(),
        "The station details popup should be displayed.");
  }

  @Then("the station details title should be {string}")
  public void theStationDetailsTitleShouldBe(String expectedTitle) {
    assertEquals(
        expectedTitle,
        serviceStationsMapPage.getStationDetailsPopupTitle(),
        "Station title in details popup does not match.");
  }

  @Then("the station details address should be {string}")
  public void theStationDetailsAddressShouldBe(String expectedAddress) {
    assertEquals(
        expectedAddress,
        serviceStationsMapPage.getStationDetailsPopupAddress(),
        "Station address in details popup does not match.");
  }

  @Then("the station details view details button should be displayed")
  public void theStationDetailsViewDetailsButtonShouldBeDisplayed() {
    assertTrue(
        serviceStationsMapPage.isStationDetailsPopupViewDetailsButtonDisplayed(),
        "The 'View Details' button in the popup should be displayed.");
  }
}
