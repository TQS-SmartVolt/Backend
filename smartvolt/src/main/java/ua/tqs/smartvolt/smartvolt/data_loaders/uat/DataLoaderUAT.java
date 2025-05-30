package ua.tqs.smartvolt.smartvolt.data_loaders.uat;

import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;
import ua.tqs.smartvolt.smartvolt.repositories.UserRepository;

@Component
@Profile("uat")
public class DataLoaderUAT implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final StationOperatorRepository stationOperatorRepository;
  private final ChargingStationRepository chargingStationRepository;
  private final ChargingSlotRepository chargingSlotRepository;
  private final EvDriverRepository evDriverRepository;
  private final BookingRepository bookingRepository;
  private final UserRepository userRepository;

  public static StationOperator stationOperator;

  public DataLoaderUAT(
      StationOperatorRepository sor,
      ChargingStationRepository csr,
      ChargingSlotRepository cslr,
      EvDriverRepository evr,
      BookingRepository bR,
      UserRepository ur,
      PasswordEncoder passwordEncoder) {
    this.stationOperatorRepository = sor;
    this.chargingStationRepository = csr;
    this.chargingSlotRepository = cslr;
    this.evDriverRepository = evr;
    this.bookingRepository = bR;
    this.userRepository = ur;
    this.passwordEncoder = passwordEncoder;
  }

  private void dropDatabase() {
    // Order matters due to foreign key constraints
    bookingRepository.deleteAll(); // Delete bookings first
    chargingSlotRepository.deleteAll(); // Then slots
    chargingStationRepository.deleteAll();
    stationOperatorRepository.deleteAll();
    evDriverRepository.deleteAll(); // Also delete EvDrivers
    userRepository.deleteAll();
    System.out.println("Database cleared.");
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    System.out.println("DataLoader is running...");

    System.out.println("Test password");
    System.out.println("Encoded password: " + passwordEncoder.encode("StrongPassword!"));

    // Clear the database
    dropDatabase();

    // Create Station Operator
    stationOperator =
        new StationOperator(
            "John Doe", "johndoe@example.com", passwordEncoder.encode("StrongPassword!"));

    stationOperatorRepository.saveAndFlush(stationOperator);
    System.out.printf(
        "Station Operator created: %s with ID %s%n",
        stationOperator.getName(), stationOperator.getUserId());

    // Create EV Driver
    EvDriver testEvDriver =
        new EvDriver("Jane Smith", "jane@example.com", passwordEncoder.encode("password123"));
    evDriverRepository.saveAndFlush(testEvDriver);
    System.out.printf(
        "EV Driver created: %s with ID %s%n", testEvDriver.getName(), testEvDriver.getUserId());

    // Create Charging Stations
    ChargingStation testChargingStationA =
        new ChargingStation(
            "Station A",
            37.7749,
            -122.4194,
            "123 Main St, San Francisco, CA",
            true,
            stationOperator);
    ChargingStation testChargingStationB =
        new ChargingStation(
            "Station B", 34.0522, -118.2437, "456 Elm St, Los Angeles, CA", true, stationOperator);
    ChargingStation testChargingStationC =
        new ChargingStation(
            "Station C", 40.7128, -74.0060, "789 Oak St, New York, NY", false, stationOperator);

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

    // Create Charging Slots for the stations with different speeds
    chargingSlotRepository.saveAll(
        List.of(
            // Station A: 1 Slow, 1 Medium
            new ChargingSlot(true, 0.20, 10, "Slow", testChargingStationA),
            new ChargingSlot(true, 0.30, 20, "Medium", testChargingStationA),

            // Station B: 1 Fast
            new ChargingSlot(true, 0.50, 30, "Fast", testChargingStationB),

            // Station C: 1 Slow (initially inactive)
            new ChargingSlot(true, 0.20, 10, "Slow", testChargingStationC)));
    System.out.println("Charging slots created.");

    System.out.println("DataLoader finished.");
  }
}
