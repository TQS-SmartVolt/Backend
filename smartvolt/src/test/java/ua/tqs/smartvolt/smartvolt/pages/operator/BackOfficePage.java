package ua.tqs.smartvolt.smartvolt.pages.operator;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
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

  @FindBy(css = "[data-testid='station-card-add-slot']")
  private WebElement stationCardAddSlotButton;

  @FindBy(css = "[data-testid='add-slot-price-input']")
  private WebElement addSlotPriceInput;

  @FindBy(css = "[data-testid='add-slot-speed-select']")
  private WebElement addSlotSpeedSelect;

  @FindBy(css = "[data-testid='confirm-add-slot-button']")
  private WebElement confirmAddSlotButton;

  @FindBy(css = "[data-testid='operator-total-sessions']")
  private WebElement operatorTotalSessions;

  @FindBy(css = "[data-testid='operator-total-energy']")
  private WebElement operatorTotalEnergy;

  @FindBy(css = "[data-testid='operator-average-sessions']")
  private WebElement operatorAverageSessions;

  @FindBy(css = "[data-testid='operator-average-energy']")
  private WebElement operatorAverageEnergy;

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
    try {
      WebElement activeLabel =
          wait.until(
              ExpectedConditions.presenceOfNestedElementLocatedBy(
                  stationCard, By.cssSelector("[data-testid='station-card-active']")));
      return activeLabel.getText();
    } catch (TimeoutException e) {
      WebElement inactiveLabel =
          wait.until(
              ExpectedConditions.presenceOfNestedElementLocatedBy(
                  stationCard, By.cssSelector("[data-testid='station-card-inactive']")));
      return inactiveLabel.getText();
    }
  }

  public int getStationCardNumSlots(WebElement stationCard) {
    wait.until(ExpectedConditions.visibilityOf(stationCard));
    WebElement slotsElement = stationCard.findElement(By.cssSelector("[data-testid$='slots']"));
    String slotsText = slotsElement.getText().split(" ")[0]; // Extract the number before "Slots"
    return Integer.parseInt(slotsText);
  }

  public int getOperatorTotalSessions() {
    wait.until(ExpectedConditions.visibilityOf(operatorTotalSessions));
    String text = operatorTotalSessions.getText();
    return Integer.parseInt(text.replaceAll("[^0-9]", "")); // Extract number from text
  }

  public double getOperatorTotalEnergy() {
    wait.until(ExpectedConditions.visibilityOf(operatorTotalEnergy));
    String text = operatorTotalEnergy.getText();
    return Double.parseDouble(text.replaceAll("[^0-9.]", "")); // Extract number from text
  }

  public double getOperatorAverageSessions() {
    wait.until(ExpectedConditions.visibilityOf(operatorAverageSessions));
    String text = operatorAverageSessions.getText();
    return Double.parseDouble(text.replaceAll("[^0-9.]", "")); // Extract number from text
  }

  public double getOperatorAverageEnergy() {
    wait.until(ExpectedConditions.visibilityOf(operatorAverageEnergy));
    String text = operatorAverageEnergy.getText();
    return Double.parseDouble(text.replaceAll("[^0-9.]", "")); // Extract number from text
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

  public void clickAddSlotButton(WebElement stationCard) {
    WebElement addSlotButton =
        stationCard.findElement(By.cssSelector("[data-testid='station-card-add-slot']"));
    wait.until(ExpectedConditions.elementToBeClickable(addSlotButton));
    addSlotButton.click();
  }

  public void fillDeactivationReason(String reason) {
    wait.until(ExpectedConditions.elementToBeClickable(deactivationReasonSelect));
    deactivationReasonSelect.sendKeys(reason);
  }

  public void fillPriceForNewSlot(double price) {
    wait.until(ExpectedConditions.elementToBeClickable(addSlotPriceInput));
    addSlotPriceInput.sendKeys(String.valueOf(price));
  }

  public void fillChargingSpeedForNewSlot(String speed) {
    wait.until(ExpectedConditions.elementToBeClickable(addSlotSpeedSelect));
    addSlotSpeedSelect.sendKeys(speed);
  }

  public void confirmDeactivation() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmDeactivationButton));
    confirmDeactivationButton.click();
  }

  public void confirmActivation() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmActivationButton));
    confirmActivationButton.click();
  }

  public void confirmAddSlot() {
    wait.until(ExpectedConditions.elementToBeClickable(confirmAddSlotButton));
    confirmAddSlotButton.click();
  }
}
