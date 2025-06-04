package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ChargingHistoryPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverViewChargingHistorySteps {

  private final TestContext context;
  private final ChargingHistoryPage chargingHistoryPage;

  // Define the date formatter to match the frontend's output
  // This must precisely match: bookingDate.toLocaleDateString("en-US", { month: "short", day:
  // "2-digit", year: "numeric", })
  // and bookingDate.toLocaleTimeString("en-US", { hour: "2-digit", minute: "2-digit", hour12:
  // false, })
  private static final DateTimeFormatter UI_DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("MMM dd, yyyy, HH:mm");

  public EvDriverViewChargingHistorySteps(TestContext context) {
    this.context = context;
    this.chargingHistoryPage = this.context.getChargingHistoryPage();
  }

  @Then("I should be on the charging history page")
  public void iShouldBeOnTheChargingHistoryPage() {
    System.out.println(
        "DEBUG: EvDriverViewChargingHistorySteps.iShouldBeOnTheChargingHistoryPage() - Verifying page display.");
    assertTrue(chargingHistoryPage.isPageDisplayed(), "Should be on the charging history page.");
  }

  @And("I should see the charging history table displayed")
  public void iShouldSeeTheChargingHistoryTableDisplayed() {
    System.out.println(
        "DEBUG: EvDriverViewChargingHistorySteps.iShouldSeeTheChargingHistoryTableDisplayed() - Verifying table visibility.");
    assertTrue(
        chargingHistoryPage.isChargingHistoryTableDisplayed(),
        "Charging history table should be displayed.");
  }

  @And("I should see {int} charging sessions in the history")
  public void iShouldSeeChargingSessionsInTheHistory(int expectedCount) {
    System.out.println(
        "DEBUG: EvDriverViewChargingHistorySteps.iShouldSeeChargingSessionsInTheHistory() - Verifying row count.");
    assertEquals(
        expectedCount,
        chargingHistoryPage.getNumberOfHistoryEntries(),
        "Number of charging history entries should match.");
  }

  @And("the {int} charging session entry should display:")
  public void theNthChargingSessionEntryShouldDisplay(
      int rowIndex, io.cucumber.datatable.DataTable dataTable) {
    System.out.println(
        String.format(
            "DEBUG: EvDriverViewChargingHistorySteps.theNthChargingSessionEntryShouldDisplay() - Verifying row %d details.",
            rowIndex));
    List<List<String>> rows = dataTable.asLists(String.class);
    // Assuming the dataTable has one row with headers: Date, Station, Charging Speed, Charging
    // Power (kW), Energy Delivered (kWh), Price per kWh, Cost
    List<String> expectedValues = rows.get(1); // Get the first data row

    // Adjust rowIndex to be 0-based for internal page object methods
    int actualRowIndex = rowIndex - 1;

    // Extract the date and time strings from the feature file
    // String datePartFromFeature = expectedValues.get(0).split(",")[0].trim(); // Removed unused
    // variable
    String timePartFromFeature = expectedValues.get(0).split(",")[1].trim(); // e.g., "09:00"

    // Calculate the actual LocalDateTime for "tomorrow" based on when the test runs
    LocalDateTime tomorrow = LocalDateTime.now().plusDays(1);

    // Combine the calculated date with the time from the feature file
    LocalDateTime expectedDateTime =
        tomorrow
            .withHour(Integer.parseInt(timePartFromFeature.split(":")[0]))
            .withMinute(Integer.parseInt(timePartFromFeature.split(":")[1]))
            .withSecond(0) // Ensure seconds are 0 for consistent comparison
            .withNano(0); // Ensure nanos are 0 for consistent comparison

    // Format the expected LocalDateTime to match the frontend's display format
    String expectedFormattedDateTime = expectedDateTime.format(UI_DATE_TIME_FORMATTER);

    // Column order: Date, Station, Charging Speed, Charging Power (kW), Energy Delivered (kWh),
    // Price per kWh, Cost
    assertEquals(
        expectedFormattedDateTime,
        chargingHistoryPage.getCellText(actualRowIndex, 0),
        "Date and time mismatch");
    assertEquals(
        expectedValues.get(1),
        chargingHistoryPage.getCellText(actualRowIndex, 1),
        "Charging station name mismatch");
    assertEquals(
        expectedValues.get(2),
        chargingHistoryPage.getCellText(actualRowIndex, 2),
        "Charging speed mismatch");
    assertEquals(
        expectedValues.get(3),
        chargingHistoryPage.getCellText(actualRowIndex, 3),
        "Charging power mismatch");
    assertEquals(
        expectedValues.get(4),
        chargingHistoryPage.getCellText(actualRowIndex, 4),
        "Energy delivered mismatch");
    assertEquals(
        expectedValues.get(5),
        chargingHistoryPage.getCellText(actualRowIndex, 5),
        "Price per kWh mismatch");
    assertEquals(
        expectedValues.get(6),
        chargingHistoryPage.getCellText(actualRowIndex, 6),
        "Total cost mismatch");
  }

  @Then("I should see a message indicating no charging history found")
  public void iShouldSeeAMessageIndicatingNoChargingHistoryFound() {
    System.out.println(
        "DEBUG: EvDriverViewChargingHistorySteps.iShouldSeeAMessageIndicatingNoChargingHistoryFound() - Verifying no history message.");
    assertTrue(
        chargingHistoryPage.isNoHistoryMessageDisplayed(),
        "No charging history message should be displayed.");
  }
}
