package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ServiceStationsMapPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverStationSteps {

  private final TestContext context;
  private ServiceStationsMapPage serviceStationsMapPage;
  private String initialMapTransform; // To store map's transform style for comparison

  public EvDriverStationSteps(TestContext context) {
    this.context = context;
    this.serviceStationsMapPage = this.context.getServiceStationsMapPage();
  }

  @Then("I should see the map displayed")
  public void iShouldSeeTheMapDisplayed() {
    assertTrue(serviceStationsMapPage.isMapDisplayed(), "The map should be displayed.");
    initialMapTransform = serviceStationsMapPage.getMapCurrentTransformStyle();
  }

  @And("I expand the filter section")
  public void iExpandTheFilterSection() {
    serviceStationsMapPage.clickFilterButton();
  }

  @And("I click the {string} button")
  public void iClickTheButton(String buttonText) {
    if ("Show Markers".equals(buttonText)) {
      serviceStationsMapPage.clickToggleViewModeButton();
    } else {
      // Handle other buttons if you introduce more generic click steps
      throw new IllegalArgumentException("Unknown button: " + buttonText);
    }
  }

  @Then("I should see exactly {int} charging station markers on the map")
  public void iShouldSeeExactlyChargingStationMarkersOnTheMap(int expectedCount) {
    int actualMarkers = serviceStationsMapPage.getNumberOfStationMarkers();
    assertEquals(
        expectedCount,
        actualMarkers,
        "Expected to see exactly "
            + expectedCount
            + " charging station marker(s) on the map, but found "
            + actualMarkers
            + ".");
  }

  @When("I click on the charging station marker at index {int}")
  public void iClickOnTheChargingStationMarkerAtIndex(int index) {
    serviceStationsMapPage.clickStationMarkerByIndex(index);
  }

  @Then("I should see a station details popup with title {string} and address {string}")
  public void iShouldSeeAStationDetailsPopupWithTitleAndAddress(
      String expectedTitle, String expectedAddress) {
    assertTrue(
        serviceStationsMapPage.isStationDetailsPopupDisplayed(),
        "Station details popup should be displayed.");
    assertEquals(
        expectedTitle,
        serviceStationsMapPage.getStationDetailsPopupTitle(expectedTitle),
        "Popup title does not match."); // Pass expectedTitle
    assertEquals(
        expectedAddress,
        serviceStationsMapPage.getStationDetailsPopupAddress(expectedAddress),
        "Popup address does not match."); // Pass expectedAddress
  }

  // Step definition for unselecting all filters
  @And("I click on Select All Filter to remove all the filters")
  public void iClickOnSelectAllFilterToRemoveAllTheFilters() {
    serviceStationsMapPage.unselectAllFilters();
  }

  // Step definition for selecting Slow Filter
  @And("I click on Slow Filter")
  public void iClickOnSlowFilter() {
    serviceStationsMapPage.selectChargingSpeedFilter("Slow");
  }

  // Step definition for clicking the View Details button
  @And("I click on the View Details button")
  public void iClickOnTheViewDetailsButton() {
    serviceStationsMapPage.clickViewDetailsButton();
  }

  @Then("I should be in the page {string}")
  public void iShouldBeInThePage(String expectedPageUrl) {
    String fullExpectedUrl = context.getWebsite().getWebsiteUrl() + expectedPageUrl;

    // Normalize the expected URL by removing default ports if they are present
    // Example: http://localhost:80/booking becomes http://localhost/booking
    fullExpectedUrl = fullExpectedUrl.replace(":80", "");

    String currentUrl = context.getDriver().getCurrentUrl();
    System.out.println("DEBUG: Current URL: " + currentUrl);
    System.out.println("DEBUG: Expected URL (normalized): " + fullExpectedUrl);

    assertEquals(fullExpectedUrl, currentUrl, "The current URL does not match the expected page.");
  }

  // Updated Step Definitions for Map Zoom and Pan
  @And("I zoom in on the map")
  public void iZoomInOnTheMap() {
    serviceStationsMapPage.zoomIn();
    // Wait for the map's transform style to change after zooming
    serviceStationsMapPage.waitForMapTransformToChange(initialMapTransform);
    initialMapTransform =
        serviceStationsMapPage.getMapCurrentTransformStyle(); // Update for next action
  }

  @Then("the map view should have changed its zoom level")
  public void theMapViewShouldHaveChangedItsZoomLevel() {
    // The actual assertion/wait for change happened in the @And step.
    // This step is a confirmation that the change occurred.
  }

  @And("I zoom out on the map")
  public void iZoomOutOnTheMap() {
    serviceStationsMapPage.zoomOut();
    // Wait for the map's transform style to change after zooming
    serviceStationsMapPage.waitForMapTransformToChange(initialMapTransform);
    initialMapTransform =
        serviceStationsMapPage.getMapCurrentTransformStyle(); // Update for next action
  }

  @And("I pan the map by {int} pixels horizontally and {int} pixels vertically")
  public void iPanTheMapByPixelsHorizontallyAndPixelsVertically(int xOffset, int yOffset) {
    serviceStationsMapPage.panMap(xOffset, yOffset);
    // Wait for the map's transform style to change after panning
    serviceStationsMapPage.waitForMapTransformToChange(initialMapTransform);
    initialMapTransform =
        serviceStationsMapPage.getMapCurrentTransformStyle(); // Update for next action
  }

  @Then("the map position should have changed")
  public void theMapPositionShouldHaveChanged() {
    // The actual assertion/wait for change happened in the @And step.
  }
}
