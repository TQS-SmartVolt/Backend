package ua.tqs.smartvolt.smartvolt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "ua.tqs.smartvolt.smartvolt")
public class SmartvoltApplication {

  public static void main(String[] args) {
    SpringApplication.run(SmartvoltApplication.class, args);
  }
}
