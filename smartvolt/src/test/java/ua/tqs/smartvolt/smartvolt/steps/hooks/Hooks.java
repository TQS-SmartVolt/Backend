package ua.tqs.smartvolt.smartvolt.steps.hooks;

import io.cucumber.java.After;
import io.cucumber.java.Before;
import ua.tqs.smartvolt.smartvolt.steps.common.TestContext;

public class Hooks {
  private final TestContext context;

  public Hooks(TestContext context) {
    this.context = context;
  }

  @Before
  public void setup() {
    context.initialize();
  }

  @After
  public void tearDown() {
    context.quit();
  }
}
