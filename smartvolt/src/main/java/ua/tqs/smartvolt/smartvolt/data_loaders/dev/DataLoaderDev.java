package ua.tqs.smartvolt.smartvolt.data_loaders.dev;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
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
@Profile("dev")
public class DataLoaderDev implements CommandLineRunner {

  private final PasswordEncoder passwordEncoder;
  private final StationOperatorRepository stationOperatorRepository;
  private final ChargingStationRepository chargingStationRepository;
  private final ChargingSlotRepository chargingSlotRepository;
  private final UserRepository userRepository;
  private final EvDriverRepository evDriverRepository;
  private final BookingRepository bookingRepository;
  private final PaymentRepository paymentRepository;
  private final ChargingSessionRepository chargingSessionRepository;

  public DataLoaderDev(
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

    System.out.printf(
        "EV Driver created: %s with ID %s%n",
        anotherEVDriver.getName(), anotherEVDriver.getUserId());

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

    // Create bookings, payments, and sessions for each month (June 2024 to June 2025)

    // June 2024 (2 bookings)
    Booking booking1 =
        new Booking(testEVDriver, slot1, LocalDateTime.of(2024, 6, 5, 10, 0, 0), "used", 10.00);
    Booking booking2 =
        new Booking(anotherEVDriver, slot5, LocalDateTime.of(2024, 6, 15, 14, 0, 0), "used", 15.00);

    // July 2024 (1 booking)
    Booking booking3 =
        new Booking(testEVDriver, slot3, LocalDateTime.of(2024, 7, 10, 11, 0, 0), "used", 12.00);

    // August 2024 (3 bookings)
    Booking booking4 =
        new Booking(anotherEVDriver, slot2, LocalDateTime.of(2024, 8, 3, 9, 0, 0), "used", 8.00);
    Booking booking5 =
        new Booking(testEVDriver, slot4, LocalDateTime.of(2024, 8, 12, 13, 0, 0), "used", 14.00);
    Booking booking6 =
        new Booking(testEVDriver, slot6, LocalDateTime.of(2024, 8, 20, 15, 0, 0), "used", 20.00);

    // September 2024 (1 booking)
    Booking booking7 =
        new Booking(anotherEVDriver, slot3, LocalDateTime.of(2024, 9, 8, 10, 0, 0), "used", 12.00);

    // October 2024 (2 bookings)
    Booking booking8 =
        new Booking(testEVDriver, slot5, LocalDateTime.of(2024, 10, 4, 12, 0, 0), "used", 18.00);
    Booking booking9 =
        new Booking(
            anotherEVDriver, slot1, LocalDateTime.of(2024, 10, 18, 11, 0, 0), "not_used", 10.00);

    // November 2024 (1 booking)
    Booking booking10 =
        new Booking(testEVDriver, slot2, LocalDateTime.of(2024, 11, 6, 14, 0, 0), "used", 9.00);

    // December 2024 (2 bookings)
    Booking booking11 =
        new Booking(anotherEVDriver, slot4, LocalDateTime.of(2024, 12, 2, 10, 0, 0), "used", 14.00);
    Booking booking12 =
        new Booking(testEVDriver, slot6, LocalDateTime.of(2024, 12, 15, 16, 0, 0), "used", 20.00);

    // January 2025 (1 booking)
    Booking booking13 =
        new Booking(testEVDriver, slot3, LocalDateTime.of(2025, 1, 7, 11, 0, 0), "used", 12.00);

    // February 2025 (2 bookings)
    Booking booking14 =
        new Booking(anotherEVDriver, slot5, LocalDateTime.of(2025, 2, 3, 13, 0, 0), "used", 18.00);
    Booking booking15 =
        new Booking(testEVDriver, slot1, LocalDateTime.of(2025, 2, 20, 10, 0, 0), "used", 10.00);

    // March 2025 (1 booking)
    Booking booking16 =
        new Booking(testEVDriver, slot2, LocalDateTime.of(2025, 3, 5, 12, 0, 0), "used", 9.00);

    // April 2025 (3 bookings)
    Booking booking17 =
        new Booking(anotherEVDriver, slot3, LocalDateTime.of(2025, 4, 1, 11, 0, 0), "used", 12.00);
    Booking booking18 =
        new Booking(testEVDriver, slot4, LocalDateTime.of(2025, 4, 10, 14, 0, 0), "used", 14.00);
    Booking booking19 =
        new Booking(anotherEVDriver, slot6, LocalDateTime.of(2025, 4, 25, 15, 0, 0), "used", 20.00);

    // May 2025 (2 bookings)
    Booking booking20 =
        new Booking(testEVDriver, slot5, LocalDateTime.of(2025, 5, 8, 13, 0, 0), "used", 18.00);
    Booking booking21 =
        new Booking(
            anotherEVDriver, slot1, LocalDateTime.of(2025, 5, 15, 10, 0, 0), "not_used", 10.00);

    LocalDateTime now1 = LocalDateTime.now();
    int minute1 = now1.getMinute();
    int roundedMinute1 = (minute1 < 30) ? 0 : 30;
    LocalDateTime sessionStart1 = now1.withMinute(roundedMinute1).withSecond(0).withNano(0);
    Booking booking22 = new Booking(testEVDriver, slot2, sessionStart1, "used", 9.00);

    LocalDateTime now2 = LocalDateTime.now();
    int minute2 = now2.getMinute();
    int roundedMinute2 = (minute2 < 30) ? 0 : 30;
    LocalDateTime sessionStart2 = now2.withMinute(roundedMinute2).withSecond(0).withNano(0);
    Booking booking23 = new Booking(testEVDriver, slot2, sessionStart2, "paid", 9.00);

    bookingRepository.saveAll(
        Arrays.asList(
            booking1, booking2, booking3, booking4, booking5, booking6, booking7, booking8,
            booking9, booking10, booking11, booking12, booking13, booking14, booking15, booking16,
            booking17, booking18, booking19, booking20, booking21, booking22, booking23));
    System.out.println("Bookings created.");

    // Create payments for each booking
    Payment payment1 = new Payment(testEVDriver, booking1);
    Payment payment2 = new Payment(anotherEVDriver, booking2);
    Payment payment3 = new Payment(testEVDriver, booking3);
    Payment payment4 = new Payment(anotherEVDriver, booking4);
    Payment payment5 = new Payment(testEVDriver, booking5);
    Payment payment6 = new Payment(testEVDriver, booking6);
    Payment payment7 = new Payment(anotherEVDriver, booking7);
    Payment payment8 = new Payment(testEVDriver, booking8);
    Payment payment9 = new Payment(anotherEVDriver, booking9);
    Payment payment10 = new Payment(testEVDriver, booking10);
    Payment payment11 = new Payment(anotherEVDriver, booking11);
    Payment payment12 = new Payment(testEVDriver, booking12);
    Payment payment13 = new Payment(testEVDriver, booking13);
    Payment payment14 = new Payment(anotherEVDriver, booking14);
    Payment payment15 = new Payment(testEVDriver, booking15);
    Payment payment16 = new Payment(testEVDriver, booking16);
    Payment payment17 = new Payment(anotherEVDriver, booking17);
    Payment payment18 = new Payment(testEVDriver, booking18);
    Payment payment19 = new Payment(anotherEVDriver, booking19);
    Payment payment20 = new Payment(testEVDriver, booking20);
    Payment payment21 = new Payment(anotherEVDriver, booking21);
    Payment payment22 = new Payment(testEVDriver, booking22);
    Payment payment23 = new Payment(testEVDriver, booking23);

    paymentRepository.saveAll(
        Arrays.asList(
            payment1, payment2, payment3, payment4, payment5, payment6, payment7, payment8,
            payment9, payment10, payment11, payment12, payment13, payment14, payment15, payment16,
            payment17, payment18, payment19, payment20, payment21, payment22, payment23));
    System.out.println("Payments created.");

    // Create charging sessions with energyDelivered = slot power * 0.5
    ChargingSession session1 =
        new ChargingSession(slot1.getPower() * 0.5, booking1); // 10 * 0.5 = 5.0
    ChargingSession session2 =
        new ChargingSession(slot5.getPower() * 0.5, booking2); // 30 * 0.5 = 15.0
    ChargingSession session3 =
        new ChargingSession(slot3.getPower() * 0.5, booking3); // 20 * 0.5 = 10.0
    ChargingSession session4 =
        new ChargingSession(slot2.getPower() * 0.5, booking4); // 10 * 0.5 = 5.0
    ChargingSession session5 =
        new ChargingSession(slot4.getPower() * 0.5, booking5); // 20 * 0.5 = 10.0
    ChargingSession session6 =
        new ChargingSession(slot6.getPower() * 0.5, booking6); // 30 * 0.5 = 15.0
    ChargingSession session7 =
        new ChargingSession(slot3.getPower() * 0.5, booking7); // 20 * 0.5 = 10.0
    ChargingSession session8 =
        new ChargingSession(slot5.getPower() * 0.5, booking8); // 30 * 0.5 = 15.0
    ChargingSession session9 =
        new ChargingSession(slot1.getPower() * 0.5, booking9); // 10 * 0.5 = 5.0
    ChargingSession session10 =
        new ChargingSession(slot2.getPower() * 0.5, booking10); // 10 * 0.5 = 5.0
    ChargingSession session11 =
        new ChargingSession(slot4.getPower() * 0.5, booking11); // 20 * 0.5 = 10.0
    ChargingSession session12 =
        new ChargingSession(slot6.getPower() * 0.5, booking12); // 30 * 0.5 = 15.0
    ChargingSession session13 =
        new ChargingSession(slot3.getPower() * 0.5, booking13); // 20 * 0.5 = 10.0
    ChargingSession session14 =
        new ChargingSession(slot5.getPower() * 0.5, booking14); // 30 * 0.5 = 15.0
    ChargingSession session15 =
        new ChargingSession(slot1.getPower() * 0.5, booking15); // 10 * 0.5 = 5.0
    ChargingSession session16 =
        new ChargingSession(slot2.getPower() * 0.5, booking16); // 10 * 0.5 = 5.0
    ChargingSession session17 =
        new ChargingSession(slot3.getPower() * 0.5, booking17); // 20 * 0.5 = 10.0
    ChargingSession session18 =
        new ChargingSession(slot4.getPower() * 0.5, booking18); // 20 * 0.5 = 10.0
    ChargingSession session19 =
        new ChargingSession(slot6.getPower() * 0.5, booking19); // 30 * 0.5 = 15.0
    ChargingSession session20 =
        new ChargingSession(slot5.getPower() * 0.5, booking20); // 30 * 0.5 = 15.0
    ChargingSession session21 =
        new ChargingSession(slot1.getPower() * 0.5, booking21); // 10 * 0.5 = 5.0
    ChargingSession session22 =
        new ChargingSession(slot2.getPower() * 0.5, booking22); // 10 * 0.5 = 5.0

    chargingSessionRepository.saveAll(
        Arrays.asList(
            session1, session2, session3, session4, session5, session6, session7, session8,
            session9, session10, session11, session12, session13, session14, session15, session16,
            session17, session18, session19, session20, session21, session22));
    System.out.println("Charging sessions created.");

    System.out.println("DataLoader finished.");
  }
}
