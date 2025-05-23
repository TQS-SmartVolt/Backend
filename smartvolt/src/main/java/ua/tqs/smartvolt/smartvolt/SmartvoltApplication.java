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
    // Test 100x logs
    for (int i = 0; i < 100; i++) {
      logger.info("Test log number: " + i);
    }
  }
}
