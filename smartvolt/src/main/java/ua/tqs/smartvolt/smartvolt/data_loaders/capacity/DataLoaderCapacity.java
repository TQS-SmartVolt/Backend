package ua.tqs.smartvolt.smartvolt.data_loaders.capacity;

import jakarta.transaction.Transactional;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSessionRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;
import ua.tqs.smartvolt.smartvolt.repositories.PaymentRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;
import ua.tqs.smartvolt.smartvolt.repositories.UserRepository;

@Component
@Profile("capacity")
public class DataLoaderCapacity implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final StationOperatorRepository stationOperatorRepository;
  private final ChargingStationRepository chargingStationRepository;
  private final ChargingSlotRepository chargingSlotRepository;
  private final UserRepository userRepository;
  private final EvDriverRepository evDriverRepository;
  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final ChargingSessionRepository chargingSessionRepository;

  public DataLoaderCapacity(
      StationOperatorRepository sor,
      ChargingStationRepository csr,
      ChargingSlotRepository cslr,
      UserRepository ur,
      EvDriverRepository evr,
      BookingRepository bR,
      PasswordEncoder passwordEncoder,
      PaymentRepository paymentRepository,
      ChargingSessionRepository chargingSessionRepository) {
    this.stationOperatorRepository = sor;
    this.chargingStationRepository = csr;
    this.chargingSlotRepository = cslr;
    this.userRepository = ur;
    this.evDriverRepository = evr;
    this.bookingRepository = bR;
    this.passwordEncoder = passwordEncoder;
    this.paymentRepository = paymentRepository;
    this.chargingSessionRepository = chargingSessionRepository;
  }

  private void dropDatabase() {
    // First, delete all payments and charging sessions
    paymentRepository.deleteAll();
    chargingSessionRepository.deleteAll();
    // Then, delete all bookings referencing ChargingSlots
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

    // Create a StationOperator
    StationOperator stationOperator =
        new StationOperator(
            "John Doe", "johndoe@example.com", passwordEncoder.encode("StrongPassword!"));
    stationOperatorRepository.saveAndFlush(stationOperator);
    System.out.printf(
        "Station Operator created: %s with ID %s%n",
        stationOperator.getName(), stationOperator.getUserId());

    // Create 10 EVDrivers all with the same password
    int numberOfDrivers = 1000;
    createDriversWithSamePassword(numberOfDrivers);

    // Create 3 ChargingStations
    int numberChargingStations = 3;
    createChargingStations(stationOperator, numberChargingStations);

    // Create 6 ChargingSlots (2 "Slow", 2 "Medium", 2 "Fast") for each ChargingStation
    int numberChargingSlots = 6;
    createChargingSlotsForEachStation(numberChargingSlots);
    System.out.println("DataLoader finished.");
  }

  private void createDriversWithSamePassword(int numberOfDrivers) {
    String name;
    String email;
    String password = passwordEncoder.encode("password123!");
    EvDriver evDriver;
    for (int i = 1; i <= numberOfDrivers; i++) {
      name = "evdriver" + i;
      email = "evdriver" + i + "@example.com";
      evDriver = new EvDriver(name, email, password);
      evDriverRepository.saveAndFlush(evDriver);
      System.out.printf(
          "EV Driver created: %s with ID %s%n", evDriver.getName(), evDriver.getUserId());
    }
  }

  private void createChargingStations(StationOperator stationOperator, int numberChargingStations) {
    String stationName;
    double latitude;
    double longitude;
    String address;
    boolean availability = true;
    ChargingStation chargingStation;
    for (int i = 1; i <= numberChargingStations; i++) {
      stationName = "Station " + i;
      latitude = 40.0 + i * 0.01;
      longitude = -8.0 - i * 0.01;
      address = "Address " + i;
      chargingStation =
          new ChargingStation(
              stationName, latitude, longitude, address, availability, stationOperator);
      chargingStationRepository.saveAndFlush(chargingStation);
      System.out.printf(
          "Charging Station created: %s with ID %s%n",
          chargingStation.getName(), chargingStation.getStationId());
    }
  }

  private void createChargingSlotsForEachStation(int numberChargingSlots) {
    boolean isLocked = true;
    double pricePerKWh;
    double power;
    String chargingSpeed;
    ChargingSlot chargingSlot;
    for (ChargingStation station : chargingStationRepository.findAll()) {
      for (int i = 1; i <= numberChargingSlots; i++) {
        if (i <= 2) {
          chargingSpeed = "Slow";
          power = 10.0;
          pricePerKWh = 0.20;
        } else if (i <= 4) {
          chargingSpeed = "Medium";
          power = 30.0;
          pricePerKWh = 0.30;
        } else {
          chargingSpeed = "Fast";
          power = 50.0;
          pricePerKWh = 0.50;
        }
        chargingSlot = new ChargingSlot(isLocked, pricePerKWh, power, chargingSpeed, station);
        chargingSlotRepository.saveAndFlush(chargingSlot);
        System.out.printf(
            "Charging Slot created: %s with ID %s%n",
            chargingSlot.getChargingSpeed(), chargingSlot.getSlotId());
      }
    }
  }
}
