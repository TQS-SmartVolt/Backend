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
  private final UserRepository userRepository;
  private final EvDriverRepository evDriverRepository;
  private final BookingRepository bookingRepository;

  public DataLoaderUAT(
      StationOperatorRepository sor,
      ChargingStationRepository csr,
      ChargingSlotRepository cslr,
      UserRepository ur,
      EvDriverRepository evr,
      BookingRepository bR,
      PasswordEncoder passwordEncoder) {
    this.stationOperatorRepository = sor;
    this.chargingStationRepository = csr;
    this.chargingSlotRepository = cslr;
    this.userRepository = ur;
    this.evDriverRepository = evr;
    this.bookingRepository = bR;
    this.passwordEncoder = passwordEncoder;
  }

  private void dropDatabase() {
    // First, delete all bookings referencing ChargingSlots
    bookingRepository.deleteAll();

    // Now, delete charging slots
    chargingSlotRepository.deleteAll();

    // Proceed with other deletions
    chargingStationRepository.deleteAll();
    stationOperatorRepository.deleteAll();
    evDriverRepository.deleteAll();
    userRepository.deleteAll();
  }

  @Override
  @Transactional
  public void run(String... args) throws Exception {
    System.out.println("DataLoader is running...");

    System.out.println("Test password");
    System.out.println("Encoded password: " + passwordEncoder.encode("StrongPassword!"));

    // Clear the database
    dropDatabase();

    StationOperator stationOperator =
        new StationOperator(
            "John Doe", "johndoe@example.com", passwordEncoder.encode("StrongPassword!"));
    stationOperatorRepository.saveAndFlush(stationOperator);
    System.out.printf(
        "Station Operator created: %s with ID %s%n",
        stationOperator.getName(), stationOperator.getUserId());

    // Create a EVDriver
    EvDriver testEVDriver =
        new EvDriver("Jane Smith", "test@example.com", passwordEncoder.encode("password123!"));
    evDriverRepository.saveAndFlush(testEVDriver);

    System.out.printf(
        "EV Driver created: %s with ID %s%n", testEVDriver.getName(), testEVDriver.getUserId());

    // Create Charging Stations
    ChargingStation testChargingStation1 =
        new ChargingStation("Station 1", 40.6343605, -8.647361, "Rua 1", true, stationOperator);
    ChargingStation testChargingStation2 =
        new ChargingStation("Station 2", 40.613605, -8.647361, "Rua 2", true, stationOperator);
    ChargingStation testChargingStation3 =
        new ChargingStation("Station 3", 40.623605, -8.647361, "Rua 3", false, stationOperator);

    chargingStationRepository.saveAll(
        List.of(testChargingStation1, testChargingStation2, testChargingStation3));

    System.out.printf(
        "Charging Station created: %s with ID %s%n",
        testChargingStation1.getName(), testChargingStation1.getStationId());
    System.out.printf(
        "Charging Station created: %s with ID %s%n",
        testChargingStation2.getName(), testChargingStation2.getStationId());
    System.out.printf(
        "Charging Station created: %s with ID %s%n",
        testChargingStation3.getName(), testChargingStation3.getStationId());

    chargingSlotRepository.saveAll(
        List.of(
            new ChargingSlot(true, 0.20, 10, "Slow", testChargingStation1),
            new ChargingSlot(true, 0.20, 10, "Slow", testChargingStation1),
            new ChargingSlot(true, 0.30, 20, "Medium", testChargingStation1),
            new ChargingSlot(true, 0.30, 20, "Medium", testChargingStation2),
            new ChargingSlot(true, 0.50, 30, "Fast", testChargingStation3),
            new ChargingSlot(true, 0.50, 30, "Fast", testChargingStation3)));

    System.out.println("Charging slots created.");

    System.out.println("DataLoader finished.");
  }
}
