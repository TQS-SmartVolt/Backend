package ua.tqs.smartvolt.smartvolt;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class SimpleTest {

  private static final Logger logger = LogManager.getLogger(SimpleTest.class);

  @Test
  @Tag("UnitTest")
  void testAddition() {
    logger.info("Running testAddition");
    assertEquals(2, 1 + 1);
  }
}
