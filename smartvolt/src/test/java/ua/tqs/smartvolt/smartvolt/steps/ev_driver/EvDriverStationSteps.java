package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.Then;
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
  public void iShouldSeeAtLeastChargingStationMarkerOnTheMap(int expectedMinMarkers) {
    assertTrue(
        serviceStationsMapPage.getNumberOfStationMarkers() >= expectedMinMarkers,
        "Should see at least " + expectedMinMarkers + " charging station marker(s) on the map.");
  }
}
