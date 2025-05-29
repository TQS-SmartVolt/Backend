package ua.tqs.smartvolt.smartvolt.data_loaders.dev;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

@Component
@Profile("dev")
public class DataLoaderDev implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final StationOperatorRepository stationOperatorRepository;
  private final ChargingStationRepository chargingStationRepository;

  public DataLoaderDev(
      StationOperatorRepository sor,
      ChargingStationRepository csr,
      PasswordEncoder passwordEncoder) {
    this.stationOperatorRepository = sor;
    this.chargingStationRepository = csr;
    this.passwordEncoder = passwordEncoder;
  }

  private void dropDatabase() {
    // Increment this!a
    stationOperatorRepository.deleteAll();
    chargingStationRepository.deleteAll();
    System.out.println("Database cleared.");
  }

  @Override
  public void run(String... args) throws Exception {
    System.out.println("DataLoader is running...");

    // Clear the database
    dropDatabase();

    // Create Station Operator
    StationOperator testStationOperator =
        new StationOperator(
            "John Doe", "johndoe@example.com", passwordEncoder.encode("password123"));
    stationOperatorRepository.saveAndFlush(testStationOperator);
    System.out.printf(
        "Station Operator created: %s with ID %s%n",
        testStationOperator.getName(), testStationOperator.getUserId());

    // Create Charging Stations
    ChargingStation testChargingStationA =
        new ChargingStation(
            "Station A",
            37.7749,
            -122.4194,
            "123 Main St, San Francisco, CA",
            true,
            testStationOperator);
    ChargingStation testChargingStationB =
        new ChargingStation(
            "Station B",
            34.0522,
            -118.2437,
            "456 Elm St, Los Angeles, CA",
            true,
            testStationOperator);
    ChargingStation testChargingStationC =
        new ChargingStation(
            "Station C", 40.7128, -74.0060, "789 Oak St, New York, NY", false, testStationOperator);

    chargingStationRepository.saveAndFlush(testChargingStationA);
    chargingStationRepository.saveAndFlush(testChargingStationB);
    chargingStationRepository.saveAndFlush(testChargingStationC);
    System.out.printf(
        "Charging Station created: %s with ID %s%n",
        testChargingStationA.getName(), testChargingStationA.getStationId());
    System.out.printf(
        "Charging Station created: %s with ID %s%n",
        testChargingStationB.getName(), testChargingStationB.getStationId());
    System.out.printf(
        "Charging Station created: %s with ID %s%n",
        testChargingStationC.getName(), testChargingStationC.getStationId());

    System.out.println("DataLoader finished.");
  }
}
