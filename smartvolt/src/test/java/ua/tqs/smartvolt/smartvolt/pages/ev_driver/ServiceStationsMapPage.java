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

  @FindBy(css = "[data-testid='station-map-container']")
  private WebElement mapContainer;

  @FindBy(css = "[data-testid='filter-expand-collapse-button']")
  private WebElement filterExpandCollapseButton;

  @FindBy(css = "[data-testid='filter-checkbox-slow']")
  private WebElement slowSpeedFilterCheckbox;

  @FindBy(css = "[data-testid='filter-checkbox-fast']")
  private WebElement fastSpeedFilterCheckbox;

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
    try {
      wait.until(ExpectedConditions.visibilityOf(mapContainer));
      return mapContainer.isDisplayed();
    } catch (Exception e) {
      return false;
    }
  }

  public void clickFilterButton() {
    // This button expands/collapses the filter section
    wait.until(ExpectedConditions.elementToBeClickable(filterExpandCollapseButton));
    filterExpandCollapseButton.click();
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
    try {
      // Wait for at least one marker or a message indicating no markers
      // If the application shows a "no stations" message, add its data-testid here.
      // For now, assume if no markers appear, count is 0.
      wait.until(
          ExpectedConditions.presenceOfAllElementsLocatedBy(
              By.cssSelector("[data-testid^='station-marker-']")));
      List<WebElement> markers =
          driver.findElements(By.cssSelector("[data-testid^='station-marker-']"));
      return markers.size();
    } catch (TimeoutException e) {
      // No markers found within the timeout
      return 0;
    }
  }

  public void clickStationMarkerByIndex(int index) {
    List<WebElement> stationMarkers =
        driver.findElements(By.cssSelector("[data-testid^='station-marker-']"));
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

  public String getStationDetailsPopupTitle() {
    wait.until(ExpectedConditions.visibilityOf(stationDetailsPopupTitle));
    return stationDetailsPopupTitle.getText();
  }

  public String getStationDetailsPopupAddress() {
    wait.until(ExpectedConditions.visibilityOf(stationDetailsPopupAddress));
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
