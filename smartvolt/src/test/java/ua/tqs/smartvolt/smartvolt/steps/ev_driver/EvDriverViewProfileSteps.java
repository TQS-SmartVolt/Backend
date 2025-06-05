package ua.tqs.smartvolt.smartvolt.steps.ev_driver;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.cucumber.java.en.Then;
import org.springframework.boot.test.context.SpringBootTest;
import ua.tqs.smartvolt.smartvolt.pages.ev_driver.ServiceStatisticsPage;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvDriverViewProfileSteps {
  private final TestContext context;
  private final ServiceStatisticsPage serviceStatisticsPage;

  public EvDriverViewProfileSteps(TestContext context) {
    this.context = context;
    this.serviceStatisticsPage = this.context.getServiceStatisticsPage();
  }

  @Then("I should see name {string}, email {string}, totalEnergy {double} and totalMoney {double}")
  public void iShouldSeeNameEmailTotalEnergyAndTotalMoney(
      String name, String email, double totalEnergy, double totalMoney) {
    String actualName = serviceStatisticsPage.getEvDriverName();
    String actualEmail = serviceStatisticsPage.getEvDriverEmail();
    double actualTotalEnergy = serviceStatisticsPage.getEvDriverTotalEnergy();
    double actualTotalSpent = serviceStatisticsPage.getEvDriverTotalSpent();

    assertEquals(name, actualName, "EV Driver name does not match");
    assertEquals(email, actualEmail, "EV Driver email does not match");
    assertEquals(totalEnergy, actualTotalEnergy, 0.01, "EV Driver total energy does not match");
    assertEquals(totalMoney, actualTotalSpent, 0.01, "EV Driver total money spent does not match");
  }
}
