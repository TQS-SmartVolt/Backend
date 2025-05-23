package ua.tqs.smartvolt.smartvolt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SmartvoltApplicationTests {

  private static final Logger logger = LogManager.getLogger(SmartvoltApplicationTests.class);

  @Test
  void contextLoads() {
    logger.info("Running contextLoads");
    // This test will pass if the application context loads successfully
    // No assertions are needed here, as the test will fail if the context fails to load
  }
}
