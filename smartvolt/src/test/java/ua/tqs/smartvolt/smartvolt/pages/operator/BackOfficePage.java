package ua.tqs.smartvolt.smartvolt.pages.operator;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class BackOfficePage extends Website {

  @FindBy(css = "[data-testid='add-station']")
  private WebElement addStationButton;

  @FindBy(css = "[data-testid='action-modal']")
  private WebElement actionModal;

  @FindBy(css = "[data-testid='add-station-name']")
  private WebElement stationNameInput;

  @FindBy(css = "[data-testid='add-station-address']")
  private WebElement stationAddressInput;

  @FindBy(css = "[data-testid='add-station-latitude']")
  private WebElement stationLatitudeInput;

  @FindBy(css = "[data-testid='add-station-longitude']")
  private WebElement stationLongitudeInput;

  @FindBy(css = "[data-testid='confirm-add-station']")
  private WebElement confirmAddStationButton;

  @FindBy(css = "[data-testid='station-card-deactivate']")
  private WebElement stationCardDeactivateButton;

  @FindBy(css = "[data-testid='station-card-activate']")
  private WebElement stationCardActivateButton;

  @FindBy(css = "[data-testid='confirm-deactivation-button']")
  private WebElement confirmDeactivationButton;

  @FindBy(css = "[data-testid='deactivation-reason-select']")
  private WebElement deactivationReasonSelect;

  @FindBy(css = "[data-testid='confirm-activation-button']")
  private WebElement confirmActivationButton;

  public BackOfficePage(WebDriver driver) {
    super(driver);
  }

  // Access methods for WebElements

  public List<WebElement> getStationCards() {
    wait.until(
        ExpectedConditions.visibilityOfElementLocated(
            By.cssSelector("[data-testid^='station-card-index-']")));
    return driver.findElements(By.cssSelector("[data-testid^='station-card-index-']"));
  }

  public WebElement getStationByIndex(int index) {
    List<WebElement> stationCards = getStationCards();
    return stationCards.get(index - 1);
  }

  public WebElement getStationCardByName(String name) {
    List<WebElement> stationCards = getStationCards();
    for (WebElement card : stationCards) {
      String title = getStationCardTitle(card);
      if (title.equals(name)) {
        return card;
      }
    }
    throw new IllegalArgumentException("No station card found with name: " + name);
  }

  public String getStationCardTitle(WebElement stationCard) {
    wait.until(ExpectedConditions.visibilityOf(stationCard));
    WebElement titleElement =
        stationCard.findElement(By.cssSelector("[data-testid='station-card-title']"));
    return titleElement.getText();
  }

  public String getStationCardAddress(WebElement stationCard) {
    wait.until(ExpectedConditions.visibilityOf(stationCard));
    WebElement addressElement =
        stationCard.findElement(By.cssSelector("[data-testid='station-card-address']"));
    return addressElement.getText();
  }

  public String getStationCardAvailability(WebElement stationCard) {
    wait.until(ExpectedConditions.visibilityOf(stationCard));
    WebElement availabilityElement =
        stationCard.findElement(By.cssSelector("[data-testid='station-card-active']"));
    return availabilityElement.getText();
  }

  public int getStationCardNumSlots(WebElement stationCard) {
    wait.until(ExpectedConditions.visibilityOf(stationCard));
    WebElement slotsElement = stationCard.findElement(By.cssSelector("[data-testid$='slots']"));
    String slotsText = slotsElement.getText().split(" ")[0]; // Extract the number before "Slots"
    return Integer.parseInt(slotsText);
  }

  public boolean isActionModalVisible() {
    try {
      wait.until(ExpectedConditions.visibilityOf(actionModal));
      return actionModal.isDisplayed();
    } catch (Exception e) {
      return false; // modal is not found
    }
  }

  public boolean isActionModalNotVisible() {
    try {
      wait.until(ExpectedConditions.invisibilityOf(actionModal));
      return !actionModal.isDisplayed();
    } catch (Exception e) {
      return true; // modal is not found, hence not visible
    }
  }

  public void clickAddStation() {
    wait.until(ExpectedConditions.elementToBeClickable(addStationButton));
    addStationButton.click();
  }

  public void fillStationDetails(String name, String address, double latitude, double longitude) {
    stationNameInput.sendKeys(name);
    stationAddressInput.sendKeys(address);
    stationLatitudeInput.sendKeys(String.valueOf(latitude));
    stationLongitudeInput.sendKeys(String.valueOf(longitude));
  }

  public void confirmAddStation() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmAddStationButton));
    confirmAddStationButton.click();
  }

  public void clickDeactivateStation(WebElement stationCard) {
    WebElement deactivateButton =
        stationCard.findElement(By.cssSelector("[data-testid='station-card-deactivate']"));
    wait.until(ExpectedConditions.elementToBeClickable(deactivateButton));
    deactivateButton.click();
  }

  public void clickActivateStation(WebElement stationCard) {
    WebElement activateButton =
        stationCard.findElement(By.cssSelector("[data-testid='station-card-activate']"));
    wait.until(ExpectedConditions.elementToBeClickable(activateButton));
    activateButton.click();
  }

  public void fillDeactivationReason(String reason) {
    wait.until(ExpectedConditions.elementToBeClickable(deactivationReasonSelect));
    deactivationReasonSelect.sendKeys(reason);
  }

  public void confirmDeactivation() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmDeactivationButton));
    confirmDeactivationButton.click();
  }

  public void confirmActivation() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmActivationButton));
    confirmActivationButton.click();
  }
}
