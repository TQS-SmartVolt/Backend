package ua.tqs.smartvolt.smartvolt.pages.ev_driver;

import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;
import ua.tqs.smartvolt.smartvolt.pages.Website;

public class ChargingHistoryPage extends Website {

  public ChargingHistoryPage(WebDriver driver) {
    super(driver);
  }

  @FindBy(css = "[data-testid='simple-table']")
  private WebElement chargingHistoryTable;

  @FindBy(css = "[data-testid='no-history-message']")
  private WebElement noHistoryMessage;

  @FindBy(css = "[data-testid='loading-message']")
  private WebElement loadingMessage;

  @FindBy(css = "[data-testid='error-message']")
  private WebElement errorMessage;

  /**
   * Checks if the charging history page is displayed by verifying its URL.
   *
   * @return true if the URL contains "/service/history", false otherwise.
   */
  public boolean isPageDisplayed() {
    System.out.println("DEBUG: ChargingHistoryPage.isPageDisplayed() - Entering method.");
    try {
      wait.until(ExpectedConditions.urlContains("/service/history"));
      System.out.println("DEBUG: ChargingHistoryPage.isPageDisplayed() - URL is /service/history.");
      return true;
    } catch (Exception e) {
      System.err.println(
          "ERROR: ChargingHistoryPage.isPageDisplayed() - Not on charging history page: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if the charging history table is visible.
   *
   * @return true if the table is displayed, false otherwise.
   */
  public boolean isChargingHistoryTableDisplayed() {
    System.out.println(
        "DEBUG: ChargingHistoryPage.isChargingHistoryTableDisplayed() - Checking table visibility.");
    try {
      return wait.until(ExpectedConditions.visibilityOf(chargingHistoryTable)).isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: ChargingHistoryPage.isChargingHistoryTableDisplayed() - Table not found or not visible: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Checks if the "No charging history found" message is visible.
   *
   * @return true if the message is displayed, false otherwise.
   */
  public boolean isNoHistoryMessageDisplayed() {
    System.out.println(
        "DEBUG: ChargingHistoryPage.isNoHistoryMessageDisplayed() - Checking no history message visibility.");
    try {
      return wait.until(ExpectedConditions.visibilityOf(noHistoryMessage)).isDisplayed();
    } catch (Exception e) {
      System.err.println(
          "ERROR: ChargingHistoryPage.isNoHistoryMessageDisplayed() - No history message not found or not visible: "
              + e.getMessage());
      return false;
    }
  }

  /**
   * Gets the number of rows in the charging history table (excluding header).
   *
   * @return The number of data rows.
   */
  public int getNumberOfHistoryEntries() {
    System.out.println(
        "DEBUG: ChargingHistoryPage.getNumberOfHistoryEntries() - Getting number of rows.");
    try {
      // Wait for the table to be visible first
      wait.until(ExpectedConditions.visibilityOf(chargingHistoryTable));
      // Find all table rows within the table body (assuming SimpleTable uses tbody)
      List<WebElement> rows = chargingHistoryTable.findElements(By.cssSelector("tbody tr"));
      System.out.println(
          "DEBUG: ChargingHistoryPage.getNumberOfHistoryEntries() - Found "
              + rows.size()
              + " rows.");
      return rows.size();
    } catch (Exception e) {
      System.err.println(
          "ERROR: ChargingHistoryPage.getNumberOfHistoryEntries() - Failed to get rows: "
              + e.getMessage());
      return 0;
    }
  }

  /**
   * Gets the text content of a specific cell in the charging history table.
   *
   * @param rowIndex The 0-based index of the row (excluding header).
   * @param colIndex The 0-based index of the column.
   * @return The text content of the cell.
   */
  public String getCellText(int rowIndex, int colIndex) {
    System.out.println(
        String.format(
            "DEBUG: ChargingHistoryPage.getCellText() - Getting text from row %d, col %d.",
            rowIndex, colIndex));
    try {
      wait.until(ExpectedConditions.visibilityOf(chargingHistoryTable));
      WebElement row =
          chargingHistoryTable.findElement(
              By.cssSelector("tbody tr:nth-child(" + (rowIndex + 1) + ")"));
      WebElement cell = row.findElement(By.cssSelector("td:nth-child(" + (colIndex + 1) + ")"));
      String text = cell.getText();
      System.out.println(
          String.format("DEBUG: ChargingHistoryPage.getCellText() - Text: %s", text));
      return text;
    } catch (Exception e) {
      System.err.println(
          String.format(
              "ERROR: ChargingHistoryPage.getCellText() - Failed to get cell text at row %d, col %d: %s",
              rowIndex, colIndex, e.getMessage()));
      return null;
    }
  }
}
