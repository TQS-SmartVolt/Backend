package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ServiceStatisticsPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverViewStatisticsSteps {

  private final TestContext context;
  private final ServiceStatisticsPage serviceStatisticsPage;

  public EvDriverViewStatisticsSteps(TestContext context) {
    this.context = context;
    this.serviceStatisticsPage = this.context.getServiceStatisticsPage();
  }

  @Then("I should be on the statistics page")
  public void iShouldBeOnTheStatisticsPage() {
    System.out.println(
        "DEBUG: EvDriverViewStatisticsSteps.iShouldBeOnTheStatisticsPage() - Verifying page display.");
    assertTrue(serviceStatisticsPage.isPageDisplayed(), "Should be on the statistics page.");
  }

  @And("I should see the {string} chart description")
  public void iShouldSeeTheChartDescription(String chartTitle) {
    System.out.println(
        "DEBUG: EvDriverViewStatisticsSteps.iShouldSeeTheChartDescription() - Verifying description for chart: "
            + chartTitle);
    String chartRootTestId;
    String expectedDescriptionText;

    if ("Energy Consumption (kWh)".equals(chartTitle)) {
      chartRootTestId = "Energy Consumption (kWh)";
      expectedDescriptionText = "Track your energy usage month by month";
    } else if ("Spending (€)".equals(chartTitle)) {
      chartRootTestId = "Spending (€)";
      expectedDescriptionText = "Track your spending usage month by month";
    } else {
      throw new IllegalArgumentException("Unknown chart title: " + chartTitle);
    }

    assertTrue(
        serviceStatisticsPage.isChartDescriptionDisplayed(chartRootTestId, expectedDescriptionText),
        "Chart description '"
            + expectedDescriptionText
            + "' for chart '"
            + chartTitle
            + "' should be displayed.");
  }

  @And("I should see the {string} chart container")
  public void iShouldSeeTheChartContainer(String chartTitle) {
    System.out.println(
        "DEBUG: EvDriverViewStatisticsSteps.iShouldSeeTheChartContainer() - Verifying container for chart: "
            + chartTitle);
    String chartRootTestId;

    if ("Energy Consumption (kWh)".equals(chartTitle)) {
      chartRootTestId = "Energy Consumption (kWh)";
    } else if ("Spending (€)".equals(chartTitle)) {
      chartRootTestId = "Spending (€)";
    } else {
      throw new IllegalArgumentException("Unknown chart title: " + chartTitle);
    }

    assertTrue(
        serviceStatisticsPage.isChartContainerDisplayed(chartRootTestId),
        "Chart container for '" + chartTitle + "' should be displayed.");
  }
}
