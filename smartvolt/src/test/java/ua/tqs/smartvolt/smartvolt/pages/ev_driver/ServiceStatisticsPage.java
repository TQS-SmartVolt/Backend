package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class ServiceStatisticsPage extends Website {

  @FindBy(css = "[data-testid='energy-consumption-chart'] [data-testid='chart-description-text']")
  private WebElement energyChartDescription;

  @FindBy(css = "[data-testid='energy-consumption-chart'] [data-testid='chart-visual-container']")
  private WebElement energyChartContainer;

  @FindBy(css = "[data-testid='spending-chart'] [data-testid='chart-description-text']")
  private WebElement spendingChartDescription;

  @FindBy(css = "[data-testid='spending-chart'] [data-testid='chart-visual-container']")
  private WebElement spendingChartContainer;

  public ServiceStatisticsPage(WebDriver driver) {
    super(driver);
  }

  /**
   * Checks if the statistics page is displayed by verifying its URL.
   *
   * @return true if the URL contains "/service/statistics", false otherwise.
   */
  public boolean isPageDisplayed() {
    System.out.println("DEBUG: ServiceStatisticsPage.isPageDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.urlContains("/service/account"));
      System.out.println(
          "DEBUG: ServiceStatisticsPage.isPageDisplayed() - URL is /service/account.");
      return true;
    } catch (Exception e) {
      System.err.println(
          "ERROR: ServiceStatisticsPage.isPageDisplayed() - Not on statistics page: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a chart's description is displayed and contains the expected text. It selects the
   * correct WebElement based on the chartTitle.
   *
   * @param chartTitle The title of the chart (e.g., "Energy Consumption (kWh)" or "Spending (€)").
   * @param expectedDescriptionText The full text expected in the chart's description.
   * @return true if the description is found, displayed, and contains the text, false otherwise.
   */
  public boolean isChartDescriptionDisplayed(String chartTitle, String expectedDescriptionText) {
    System.out.println(
        "DEBUG: ServiceStatisticsPage.isChartDescriptionDisplayed() - Checking description '"
            + expectedDescriptionText
            + "' for chart: "
            + chartTitle);
    WebElement descriptionElement;

    if ("Energy Consumption (kWh)".equals(chartTitle)) {
      descriptionElement = energyChartDescription;
    } else if ("Spending (€)".equals(chartTitle)) {
      descriptionElement = spendingChartDescription;
    } else {
      System.err.println(
          "ERROR: ServiceStatisticsPage.isChartDescriptionDisplayed() - Unknown chart title provided: "
              + chartTitle);
      return false;
    }

    try {
      return wait.until(ExpectedConditions.visibilityOf(descriptionElement)).isDisplayed()
          && descriptionElement.getText().contains(expectedDescriptionText);
    } catch (Exception e) {
      System.err.println(
          "ERROR: ServiceStatisticsPage.isChartDescriptionDisplayed() - Description for chart '"
              + chartTitle
              + "' not found or not visible: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if a chart's visual container is displayed. It selects the correct WebElement based on
   * the chartTitle.
   *
   * @param chartTitle The title of the chart (e.g., "Energy Consumption (kWh)" or "Spending (€)").
   * @return true if the chart container is found and displayed, false otherwise.
   */
  public boolean isChartContainerDisplayed(String chartTitle) {
    System.out.println(
        "DEBUG: ServiceStatisticsPage.isChartContainerDisplayed() - Checking container for chart: "
            + chartTitle);
    WebElement containerElement;

    if ("Energy Consumption (kWh)".equals(chartTitle)) {
      containerElement = energyChartContainer;
    } else if ("Spending (€)".equals(chartTitle)) {
      containerElement = spendingChartContainer;
    } else {
      System.err.println(
          "ERROR: ServiceStatisticsPage.isChartContainerDisplayed() - Unknown chart title provided: "
              + chartTitle);
      return false;
    }

    try {
      return wait.until(ExpectedConditions.visibilityOf(containerElement)).isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: ServiceStatisticsPage.isChartContainerDisplayed() - Chart container for '"
              + chartTitle
              + "' not found or not visible: "
              + e.getMessage());
      return false;
    }
  }
}
