package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

@Component
public class DataInitializer implements CommandLineRunner {
  private final StationOperatorRepository stationOperatorRepository;

  public DataInitializer(StationOperatorRepository stationOperatorRepository) {
    this.stationOperatorRepository = stationOperatorRepository;
  }

  @Override
  public void run(String... args) throws Exception {
    // Initialize the database with a default operator if it doesn't exist
    if (stationOperatorRepository.count() == 0) {
      StationOperator operator1 = new StationOperator();
      operator1.setName("Operator 1");
      operator1.setEmail("operator1@example.com");
      operator1.setPassword("password1");
      stationOperatorRepository.save(operator1);
    }
  }
}
