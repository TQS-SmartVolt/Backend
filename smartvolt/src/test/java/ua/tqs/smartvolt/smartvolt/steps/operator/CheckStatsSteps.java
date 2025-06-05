package ua.tqs.smartvolt.smartvolt.steps.operator;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Then;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.operator.BackOfficePage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CheckStatsSteps {

  private final TestContext context;
  private BackOfficePage backOfficePage;

  public CheckStatsSteps(TestContext context) {
    this.context = context;
    this.backOfficePage = this.context.getBackOfficePage();
  }

  @Then(
      "the operator should see {int} totalSessions, {double} avgSession, {double} energyDelivered and {double} avgEnergy")
  public void theOperatorShouldSeeStats(
      int totalSessions, double avgSession, double energyDelivered, double avgEnergy) {
    int actualTotalSessions = backOfficePage.getOperatorTotalSessions();
    double actualAvgSession = backOfficePage.getOperatorAverageSessions();
    double actualEnergyDelivered = backOfficePage.getOperatorTotalEnergy();
    double actualAvgEnergy = backOfficePage.getOperatorAverageEnergy();

    assertEquals(totalSessions, actualTotalSessions, "Total sessions do not match");
    assertEquals(avgSession, actualAvgSession, 0.01, "Average session duration does not match");
    assertEquals(energyDelivered, actualEnergyDelivered, 0.01, "Energy delivered does not match");
    assertEquals(avgEnergy, actualAvgEnergy, 0.01, "Average energy does not match");
  }
}
