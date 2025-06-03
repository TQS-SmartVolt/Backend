package ua.tqs.smartvolt.smartvolt.data_loaders.uat;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.tqs.smartvolt.smartvolt.models.Booking;
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

    // // Create a EVDriver
    // EvDriver testEVDriver =
    //     new EvDriver("Jane Smith", "test@example.com", passwordEncoder.encode("password123!"));
    // evDriverRepository.saveAndFlush(testEVDriver);

    // System.out.printf(
    //     "EV Driver created: %s with ID %s%n", testEVDriver.getName(), testEVDriver.getUserId());

    // // Create Charging Stations
    // ChargingStation testChargingStation1 =
    //     new ChargingStation("Station 1", 40.6343605, -8.647361, "Rua 1", true, stationOperator);
    // ChargingStation testChargingStation2 =
    //     new ChargingStation("Station 2", 40.613605, -8.647361, "Rua 2", true, stationOperator);
    // ChargingStation testChargingStation3 =
    //     new ChargingStation("Station 3", 40.623605, -8.647361, "Rua 3", false, stationOperator);

    // chargingStationRepository.saveAll(
    //     List.of(testChargingStation1, testChargingStation2, testChargingStation3));

    // System.out.printf(
    //     "Charging Station created: %s with ID %s%n",
    //     testChargingStation1.getName(), testChargingStation1.getStationId());
    // System.out.printf(
    //     "Charging Station created: %s with ID %s%n",
    //     testChargingStation2.getName(), testChargingStation2.getStationId());
    // System.out.printf(
    //     "Charging Station created: %s with ID %s%n",
    //     testChargingStation3.getName(), testChargingStation3.getStationId());

    // chargingSlotRepository.saveAll(
    //     List.of(
    //         new ChargingSlot(true, 0.20, 10, "Slow", testChargingStation1),
    //         new ChargingSlot(true, 0.20, 10, "Slow", testChargingStation1),
    //         new ChargingSlot(true, 0.30, 20, "Medium", testChargingStation1),
    //         new ChargingSlot(true, 0.30, 20, "Medium", testChargingStation2),
    //         new ChargingSlot(true, 0.50, 30, "Fast", testChargingStation3),
    //         new ChargingSlot(true, 0.50, 30, "Fast", testChargingStation3)));

    // System.out.println("Charging slots created.");

    // -----------------------------------

    ChargingStation newTestChargingStation1 =
        new ChargingStation(
            "New Station 1", 40.3343605, -8.247361, "New Road 1", true, stationOperator);
    ChargingStation newTestChargingStation2 =
        new ChargingStation(
            "New Station 2", 40.313605, -8.147361, "New Road 2", true, stationOperator);
    ChargingStation newTestChargingStation3 =
        new ChargingStation(
            "New Station 3", 40.323605, -8.627361, "New Road 3", false, stationOperator);

    chargingStationRepository.saveAll(
        List.of(newTestChargingStation1, newTestChargingStation2, newTestChargingStation3));

    // Create a new EV Driver
    EvDriver newTestEVDriver =
        new EvDriver("Joao Pinto", "newtest@example.com", passwordEncoder.encode("passwordXPTO!"));
    evDriverRepository.saveAndFlush(newTestEVDriver);

    // Create a new EV Driver for the "no history" scenario
    EvDriver noHistoryDriver =
        new EvDriver(
            "No History Driver", "nohistory@example.com", passwordEncoder.encode("passwordXPTO!"));
    evDriverRepository.saveAndFlush(noHistoryDriver);
    System.out.printf(
        "No History EV Driver created: %s with ID %s%n",
        noHistoryDriver.getName(), noHistoryDriver.getUserId());

    ChargingSlot uatSlowSlot = new ChargingSlot(true, 0.15, 10.0, "Slow", newTestChargingStation1);
    ChargingSlot uatMediumSlot =
        new ChargingSlot(true, 0.25, 20.0, "Medium", newTestChargingStation2);
    ChargingSlot uatFastSlot = new ChargingSlot(true, 0.40, 30.0, "Fast", newTestChargingStation3);

    chargingSlotRepository.saveAll(List.of(uatSlowSlot, uatMediumSlot, uatFastSlot));
    System.out.println("Dedicated UAT charging slots for bookings created.");

    // Create Booking data for newTestEVDriver (Joao Pinto, newtest@example.com)
    LocalDateTime now = LocalDateTime.now();

    // Booking 1: Slow Charging Session - tomorrow
    LocalDateTime booking1Time =
        now.plusDays(1).withHour(9).withMinute(0).withSecond(0).withNano(0);
    double power1 = uatSlowSlot.getPower();
    double price1 = uatSlowSlot.getPricePerKWh();
    double cost1 = (power1 * 0.5) * price1; // Correct cost calculation
    Booking booking1 = new Booking(newTestEVDriver, uatSlowSlot, booking1Time, "Not Used", cost1);
    bookingRepository.save(booking1);
    System.out.printf(
        "UAT Booking 1 created for driver %s, slot %s, cost %.2f%n",
        newTestEVDriver.getName(), uatSlowSlot.getSlotId(), cost1);

    // Booking 2: Medium Charging Session - tomorrow
    LocalDateTime booking2Time =
        now.plusDays(1).withHour(14).withMinute(30).withSecond(0).withNano(0);
    double power2 = uatMediumSlot.getPower();
    double price2 = uatMediumSlot.getPricePerKWh();
    double cost2 = (power2 * 0.5) * price2; // Correct cost calculation
    Booking booking2 = new Booking(newTestEVDriver, uatMediumSlot, booking2Time, "Not Used", cost2);
    bookingRepository.save(booking2);
    System.out.printf(
        "UAT Booking 2 created for driver %s, slot %s, cost %.2f%n",
        newTestEVDriver.getName(), uatMediumSlot.getSlotId(), cost2);

    // Booking 3: Fast Charging Session - tomorrow
    LocalDateTime booking3Time =
        now.plusDays(1).withHour(18).withMinute(0).withSecond(0).withNano(0);
    double power3 = uatFastSlot.getPower();
    double price3 = uatFastSlot.getPricePerKWh();
    double cost3 = (power3 * 0.5) * price3; // Correct cost calculation
    Booking booking3 = new Booking(newTestEVDriver, uatFastSlot, booking3Time, "Not Used", cost3);
    bookingRepository.save(booking3);
    System.out.printf(
        "UAT Booking 3 created for driver %s, slot %s, cost %.2f%n",
        newTestEVDriver.getName(), uatFastSlot.getSlotId(), cost3);

    // -----------------------------------

    System.out.println("DataLoader finished.");
  }
}
