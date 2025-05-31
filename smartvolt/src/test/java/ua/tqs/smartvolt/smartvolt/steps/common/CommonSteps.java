package ua.tqs.smartvolt.smartvolt.steps.common;

import io.cucumber.java.en.Given;

public class CommonSteps {

  private final TestContext context;

  public CommonSteps(TestContext context) {
    this.context = context;
  }

  @Given("the website is available at page {string}")
  public void givenTheWebsiteIsAvailableAtPage(String page) {
    context.getBackOfficePage().navigateTo(page);
  }

  @Given("the user is on page {string}")
  public void theUserIsOnPage(String page) {
    context.getWebsite().navigateTo(page); // This requires TestContext to have getWebsite()
  }

  @Given("the operator is logged in with email {string} and password {string}")
  public void theOperatorIsLoggedInWithEmailAndPassword(String email, String password) {
    context.getLoginPage().login(email, password, true);
  }

  @Given("the EV driver is logged in with email {string} and password {string}")
  public void theEvDriverIsLoggedInWithEmailAndPassword(String email, String password) {
    context.getLoginPage().login(email, password, false);
  }
}
