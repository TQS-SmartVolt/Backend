package ua.tqs.smartvolt.smartvolt.data_loaders.uat;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSession;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.Payment;
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
@Profile("uat")
public class DataLoaderUAT implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final StationOperatorRepository stationOperatorRepository;
  private final ChargingStationRepository chargingStationRepository;
  private final ChargingSlotRepository chargingSlotRepository;
  private final UserRepository userRepository;
  private final EvDriverRepository evDriverRepository;
  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final ChargingSessionRepository chargingSessionRepository;

  public DataLoaderUAT(
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

    // Create another EVDriver
    EvDriver anotherEVDriver =
        new EvDriver("Alice Johnson", "test2@example.com", passwordEncoder.encode("password456!"));
    evDriverRepository.saveAndFlush(anotherEVDriver);

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

    ChargingSlot slot1 = new ChargingSlot(true, 0.20, 10, "Slow", testChargingStation1);
    ChargingSlot slot2 = new ChargingSlot(true, 0.20, 10, "Slow", testChargingStation1);
    ChargingSlot slot3 = new ChargingSlot(true, 0.30, 20, "Medium", testChargingStation1);
    ChargingSlot slot4 = new ChargingSlot(true, 0.30, 20, "Medium", testChargingStation2);
    ChargingSlot slot5 = new ChargingSlot(true, 0.50, 30, "Fast", testChargingStation3);
    ChargingSlot slot6 = new ChargingSlot(true, 0.50, 30, "Fast", testChargingStation3);

    chargingSlotRepository.saveAll(List.of(slot1, slot2, slot3, slot4, slot5, slot6));

    System.out.println("Charging slots created.");

    // Create some bookings
    Booking testBooking1 =
        new Booking(
            testEVDriver,
            slot1,
            LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0),
            "not_used",
            5.00);
    Booking testBooking2 =
        new Booking(
            testEVDriver,
            slot3,
            LocalDateTime.now().plusDays(1).withHour(11).withMinute(0).withSecond(0).withNano(0),
            "used",
            15.00);
    Booking anotherBooking =
        new Booking(
            anotherEVDriver,
            slot2,
            LocalDateTime.now().plusDays(1).withHour(12).withMinute(0).withSecond(0).withNano(0),
            "used",
            10.00);

    bookingRepository.saveAll(List.of(testBooking1, testBooking2, anotherBooking));

    System.out.println("Bookings created.");

    // Create payments
    Payment testPayment1 = new Payment(testEVDriver, testBooking1);
    Payment testPayment2 = new Payment(testEVDriver, testBooking2);
    Payment anotherPayment = new Payment(anotherEVDriver, anotherBooking);

    paymentRepository.saveAll(List.of(testPayment1, testPayment2, anotherPayment));

    System.out.println("Payments created.");

    // Create charging sessions
    ChargingSession testChargingSession1 = new ChargingSession(10.0, testBooking1);
    ChargingSession testChargingSession2 = new ChargingSession(20.0, testBooking2);
    ChargingSession anotherChargingSession = new ChargingSession(15.0, anotherBooking);

    chargingSessionRepository.saveAll(
        List.of(testChargingSession1, testChargingSession2, anotherChargingSession));

    System.out.println("Charging sessions created.");

    System.out.println("DataLoader finished.");
  }
}
