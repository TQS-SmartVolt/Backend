package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class ServiceStationsMapPage extends Website {

  // Constructor
  public ServiceStationsMapPage(WebDriver driver) {
    super(driver);
  }

  // --- Web Elements for the Map and Filters ---

  @FindBy(css = "[data-testid='map-component-container']")
  private WebElement mapContainer;

  @FindBy(css = "[data-testid='filter-expand-collapse-button']")
  private WebElement filterExpandCollapseButton;

  @FindBy(css = "[data-testid='filter-checkbox-slow']")
  private WebElement slowSpeedFilterCheckbox;

  @FindBy(css = "[data-testid='filter-checkbox-fast']")
  private WebElement fastSpeedFilterCheckbox;

  // New: Web Element for the toggle view mode button
  @FindBy(css = "[data-testid='toggle-view-mode-button']")
  private WebElement toggleViewModeButton;

  // --- Web Elements for Station Markers/Details ---

  // Represents the currently open station popup
  @FindBy(css = "[data-testid^='station-popup-']")
  private WebElement stationDetailsPopup;

  @FindBy(css = "[data-testid^='station-popup-title-']")
  private WebElement stationDetailsPopupTitle;

  @FindBy(css = "[data-testid^='station-popup-address-']")
  private WebElement stationDetailsPopupAddress;

  @FindBy(css = "[data-testid^='station-popup-view-details-button-']")
  private WebElement stationDetailsPopupViewDetailsButton;

  // --- Page Actions ---

  public boolean isMapDisplayed() {
    System.out.println("DEBUG: ServiceStationsMapPage.isMapDisplayed() - Entering method.");
    try {
      System.out.println(
          "DEBUG: ServiceStationsMapPage.isMapDisplayed() - Waiting for visibility of mapContainer ([data-testid='map-component-container'])...");
      wait.until(ExpectedConditions.visibilityOf(mapContainer));
      System.out.println(
          "DEBUG: ServiceStationsMapPage.isMapDisplayed() - mapContainer is visible. Now waiting for a Leaflet map tile ([data-testid='map-component-container'] img.leaflet-tile)...");

      // This wait ensures the actual map content (tiles) has loaded
      wait.until(
          ExpectedConditions.visibilityOfElementLocated(
              By.cssSelector("[data-testid='map-component-container'] img.leaflet-tile")));
      System.out.println(
          "DEBUG: ServiceStationsMapPage.isMapDisplayed() - Map tiles are visible. Map is considered displayed.");

      return mapContainer.isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: ServiceStationsMapPage.isMapDisplayed() - Map display check failed: "
              + e.getMessage());
      return false;
    }
  }

  public void clickFilterButton() {
    System.out.println(
        "DEBUG: ServiceStationsMapPage.clickFilterButton() - Clicking filter expand/collapse button.");
    wait.until(ExpectedConditions.elementToBeClickable(filterExpandCollapseButton));
    filterExpandCollapseButton.click();
  }

  public void clickToggleViewModeButton() {
    System.out.println(
        "DEBUG: ServiceStationsMapPage.clickToggleViewModeButton() - Entering method.");
    wait.until(ExpectedConditions.elementToBeClickable(toggleViewModeButton));
    // Check if the button currently says "Show Markers" before clicking
    String buttonText = toggleViewModeButton.getText();
    System.out.println(
        "DEBUG: ServiceStationsMapPage.clickToggleViewModeButton() - Current button text: '"
            + buttonText
            + "'");
    if (buttonText.equals("Show Markers")) {
      toggleViewModeButton.click();
      System.out.println(
          "DEBUG: ServiceStationsMapPage.clickToggleViewModeButton() - Clicked 'Show Markers' button.");
      // Add a small wait for the map to re-render with markers
      try {
        Thread.sleep(1000); // Wait for 1 second, adjust as needed
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
    } else {
      System.out.println(
          "DEBUG: ServiceStationsMapPage.clickToggleViewModeButton() - Button is not 'Show Markers'. Assuming markers are already visible or view is already toggled.");
    }
  }

  public void selectChargingSpeedFilter(String speed) {
    // Assuming the filter modal/menu is open after clicking filterExpandCollapseButton
    WebElement checkboxToClick;
    if ("Slow".equalsIgnoreCase(speed)) {
      checkboxToClick = slowSpeedFilterCheckbox;
    } else if ("Fast".equalsIgnoreCase(speed)) {
      checkboxToClick = fastSpeedFilterCheckbox;
    } else {
      throw new IllegalArgumentException("Unsupported charging speed filter: " + speed);
    }

    wait.until(ExpectedConditions.elementToBeClickable(checkboxToClick));
    if (!checkboxToClick.isSelected()) {
      checkboxToClick.click();
    }
  }

  public int getNumberOfStationMarkers() {
    System.out.println(
        "DEBUG: ServiceStationsMapPage.getNumberOfStationMarkers() - Entering method.");
    try {
      System.out.println(
          "DEBUG: ServiceStationsMapPage.getNumberOfStationMarkers() - Waiting for presence of station markers (.leaflet-marker-icon).");
      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(
              By.cssSelector(".leaflet-marker-icon")));
      List<WebElement> markers = driver.findElements(By.cssSelector(".leaflet-marker-icon"));
      int numberOfMarkers = markers.size();
      System.out.println(
          "DEBUG: ServiceStationsMapPage.getNumberOfStationMarkers() - Found "
              + numberOfMarkers
              + " markers.");
      return numberOfMarkers;
    } catch (TimeoutException e) {
      System.out.println(
          "DEBUG: ServiceStationsMapPage.getNumberOfStationMarkers() - TimeoutException: No markers found within the timeout. Returning 0.");
      return 0;
    } catch (Exception e) {
      System.err.println(
          "ERROR: ServiceStationsMapPage.getNumberOfStationMarkers() - An unexpected error occurred: "
              + e.getMessage());
      return 0;
    }
  }

  public void clickStationMarkerByIndex(int index) {
    List<WebElement> stationMarkers = driver.findElements(By.cssSelector(".leaflet-marker-icon"));
    wait.until(ExpectedConditions.visibilityOfAllElements(stationMarkers));
    if (index >= 0 && index < stationMarkers.size()) {
      wait.until(ExpectedConditions.elementToBeClickable(stationMarkers.get(index)));
      stationMarkers.get(index).click();
    } else {
      throw new IndexOutOfBoundsException("Station marker index out of bounds: " + index);
    }
  }

  public boolean isStationDetailsPopupDisplayed() {
    try {
      wait.until(ExpectedConditions.visibilityOf(stationDetailsPopup));
      return stationDetailsPopup.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public String getStationDetailsPopupTitle(String expectedTitle) {
    wait.until(
        ExpectedConditions.textToBePresentInElement(stationDetailsPopupTitle, expectedTitle));
    return stationDetailsPopupTitle.getText();
  }

  public String getStationDetailsPopupAddress(String expectedAddress) {
    wait.until(
        ExpectedConditions.textToBePresentInElement(stationDetailsPopupAddress, expectedAddress));
    return stationDetailsPopupAddress.getText();
  }

  public boolean isStationDetailsPopupViewDetailsButtonDisplayed() {
    try {
      wait.until(ExpectedConditions.visibilityOf(stationDetailsPopupViewDetailsButton));
      return stationDetailsPopupViewDetailsButton.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }
}
