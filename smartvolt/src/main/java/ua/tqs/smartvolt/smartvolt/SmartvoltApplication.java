package ua.tqs.smartvolt.smartvolt;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SmartvoltApplication {

  private static final Logger logger = LogManager.getLogger(SmartvoltApplication.class);

  public static void main(String[] args) {
    SpringApplication.run(SmartvoltApplication.class, args);
    // Wait 1 min and test 100x logs
    try {
      Thread.sleep(60000);
    } catch (InterruptedException e) {
      logger.error("Error while waiting", e);
    }
    for (int i = 0; i < 10; i++) {
      logger.info("TestLogNumber" + i);
    }
  }
}
