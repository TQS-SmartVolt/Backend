package ua.tqs.smartvolt.smartvolt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.dto.OperatorEnergyResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.exceptions.SlotAlreadyBookedException;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSession;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSessionRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;
  @Mock private EvDriverRepository evDriverRepository;
  @Mock private ChargingSlotRepository chargingSlotRepository;
  @Mock private ChargingSessionRepository chargingSessionRepository;
  @Mock private ChargingSessionService chargingSessionService;

  private BookingService bookingService;

  // Common test data
  private EvDriver testDriver;
  private ChargingSlot testSlot;
  private BookingRequest validBookingRequest;
  private Booking expectedBooking;
  private Booking preExistingBooking;
  private ChargingStation testStation;

  @BeforeEach
  void setUp() {
    bookingService =
        new BookingService(
            bookingRepository, evDriverRepository, chargingSlotRepository, chargingSessionService);

    testDriver = new EvDriver();
    testDriver.setUserId(101L);
    testDriver.setEmail("driver@example.com");
    testDriver.setName("Test Driver");

    StationOperator testOperator = new StationOperator();
    testOperator.setUserId(1L);

    testStation =
        new ChargingStation("Test Station", 40.0, -8.0, "Test Address", true, testOperator);
    testStation.setStationId(200L);

    testSlot = new ChargingSlot();
    testSlot.setSlotId(201L);
    testSlot.setPower(10.0);
    testSlot.setPricePerKWh(0.15);
    testSlot.setChargingSpeed("Fast");
    testSlot.setStation(testStation);

    LocalDateTime now =
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    validBookingRequest = new BookingRequest(testSlot.getSlotId(), now);

    expectedBooking = new Booking();
    expectedBooking.setBookingId(1L);
    expectedBooking.setSlot(testSlot);
    expectedBooking.setDriver(testDriver);
    expectedBooking.setStartTime(validBookingRequest.getStartTime());
    expectedBooking.setStatus("not_used");
    expectedBooking.setCost(1.5);

    preExistingBooking = new Booking();
    preExistingBooking.setBookingId(99L);
    preExistingBooking.setSlot(testSlot);
    preExistingBooking.setStartTime(validBookingRequest.getStartTime());
    preExistingBooking.setStatus("not_used");

    // Lenient stubbing for common repository calls by default
    lenient().when(evDriverRepository.findById(anyLong())).thenReturn(Optional.of(testDriver));
    lenient().when(chargingSlotRepository.findById(anyLong())).thenReturn(Optional.of(testSlot));
    lenient()
        .when(
            bookingRepository.findBySlotAndStartTime(
                any(ChargingSlot.class), any(LocalDateTime.class)))
        .thenReturn(Optional.empty());
    lenient().when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);
    lenient()
        .when(chargingSlotRepository.getPowerBySlotId(anyLong()))
        .thenReturn(Optional.of(testSlot.getPower()));
    lenient()
        .when(chargingSlotRepository.getPricePerKWhBySlotId(anyLong()))
        .thenReturn(Optional.of(testSlot.getPricePerKWh()));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_ValidRequest_ReturnsCreatedBooking()
      throws ResourceNotFoundException, SlotAlreadyBookedException {
    // Arrange: All mocks set up in @BeforeEach are valid for this scenario

    // Act
    Booking createdBooking =
        bookingService.createBooking(validBookingRequest, testDriver.getUserId());

    // Assert
    assertThat(createdBooking).isNotNull();
    assertThat(createdBooking.getBookingId()).isEqualTo(expectedBooking.getBookingId());
    assertThat(createdBooking.getSlot()).isEqualTo(expectedBooking.getSlot());
    assertThat(createdBooking.getDriver()).isEqualTo(expectedBooking.getDriver());
    assertThat(createdBooking.getStartTime()).isEqualTo(expectedBooking.getStartTime());
    assertThat(createdBooking.getStatus()).isEqualTo("not_used");
    assertThat(createdBooking.getCost()).isEqualTo(expectedBooking.getCost());

    // Verify repository interactions
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(chargingSlotRepository, times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    verify(bookingRepository, times(1)).save(any(Booking.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_InvalidSlotId_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidSlotId = 999L;
    BookingRequest requestWithInvalidSlot =
        new BookingRequest(invalidSlotId, validBookingRequest.getStartTime());

    // Mock that slot is not found
    when(chargingSlotRepository.findById(invalidSlotId)).thenReturn(Optional.empty());
    // Also mock that driver is found first, as per service order.
    when(evDriverRepository.findById(testDriver.getUserId())).thenReturn(Optional.of(testDriver));

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(requestWithInvalidSlot, testDriver.getUserId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Slot not found with id: " + invalidSlotId);

    // Verify interactions
    verify(evDriverRepository, times(1))
        .findById(testDriver.getUserId()); // Driver is fetched first
    verify(chargingSlotRepository, times(1)).findById(invalidSlotId);
    verify(bookingRepository, never()).findBySlotAndStartTime(any(), any());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_InvalidDriverId_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidDriverId = 998L;
    when(evDriverRepository.findById(invalidDriverId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> bookingService.createBooking(validBookingRequest, invalidDriverId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Driver not found with id: " + invalidDriverId);

    // Verify interactions
    verify(evDriverRepository, times(1)).findById(invalidDriverId);
    verify(chargingSlotRepository, never()).findById(anyLong());
    verify(bookingRepository, never()).findBySlotAndStartTime(any(), any());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_SlotAlreadyBooked_ThrowsSlotAlreadyBookedException() {
    // Arrange
    when(bookingRepository.findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime()))
        .thenReturn(Optional.of(preExistingBooking));

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(SlotAlreadyBookedException.class)
        .hasMessageContaining(
            "Slot "
                + testSlot.getSlotId()
                + " is already booked at "
                + validBookingRequest
                    .getStartTime()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));

    // Verify repository interactions
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_InvalidStartTimeInterval_ThrowsIllegalArgumentException() {
    // Arrange
    // Use a time not on a 30-min interval, including seconds/nanos to be strict
    LocalDateTime invalidTime =
        LocalDateTime.now().plusDays(1).withHour(10).withMinute(15).withSecond(10).withNano(100);
    BookingRequest request = new BookingRequest(testSlot.getSlotId(), invalidTime);

    when(evDriverRepository.findById(testDriver.getUserId())).thenReturn(Optional.of(testDriver));
    when(chargingSlotRepository.findById(testSlot.getSlotId())).thenReturn(Optional.of(testSlot));

    // Act & Assert
    assertThatThrownBy(() -> bookingService.createBooking(request, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Booking start time must be on a 30-minute interval (e.g., HH:00 or HH:30).");

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, never()).findBySlotAndStartTime(any(), any());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_PowerNotFoundForSlot_ThrowsResourceNotFoundException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.empty());
    lenient()
        .when(chargingSlotRepository.getPricePerKWhBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(testSlot.getPricePerKWh()));

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Power not found for slotId: " + testSlot.getSlotId());

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_PriceNotFoundForSlot_ThrowsResourceNotFoundException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(testSlot.getPower()));
    when(chargingSlotRepository.getPricePerKWhBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Price not found for slotId: " + testSlot.getSlotId());

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(chargingSlotRepository, times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_NegativePower_ThrowsIllegalArgumentException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(-5.0));

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid power or price");

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(chargingSlotRepository, times(1))
        .getPricePerKWhBySlotId(
            testSlot.getSlotId()); // Assuming both are fetched before validation
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242")
  void createBooking_NegativePricePerKWh_ThrowsIllegalArgumentException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(testSlot.getPower()));
    when(chargingSlotRepository.getPricePerKWhBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(-0.05));

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid power or price");

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .findBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(chargingSlotRepository, times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-242") // Assign a new requirement ID
  void createBooking_StartTimeInPast_ThrowsIllegalArgumentException() {
    // Arrange
    // Create a start time that is definitively in the past
    LocalDateTime pastTime = LocalDateTime.now().minusHours(1).withSecond(0).withNano(0);
    if (pastTime.getMinute() % 30 != 0) {
      // Ensure it's on a 30-minute interval, even if in the past
      pastTime = pastTime.minusMinutes(pastTime.getMinute() % 30);
    }

    BookingRequest requestWithPastTime = new BookingRequest(testSlot.getSlotId(), pastTime);

    // Mocks for driver and slot should still return valid objects as these checks come first
    when(evDriverRepository.findById(testDriver.getUserId())).thenReturn(Optional.of(testDriver));
    when(chargingSlotRepository.findById(testSlot.getSlotId())).thenReturn(Optional.of(testSlot));

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(requestWithPastTime, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Cannot create a booking in the past.");

    // Verify that no further repository interactions happen (like checking existing bookings or
    // saving)
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, never()).findBySlotAndStartTime(any(), any());
    verify(bookingRepository, never()).save(any());
    verify(chargingSlotRepository, never()).getPowerBySlotId(anyLong());
    verify(chargingSlotRepository, never()).getPricePerKWhBySlotId(anyLong());
  }

  // --- Tests for finalizeBookingPayment (User Story 3.1) ---

  @Test
  @Tag("UnitTest")
  @Requirement("SV-26")
  void finalizeBookingPayment_ValidBookingNotUsed_StatusChangesToPaid() throws Exception {
    // Arrange
    Long bookingId = 123L;
    Booking bookingToFinalize = new Booking();
    bookingToFinalize.setBookingId(bookingId);
    bookingToFinalize.setStatus("not_used");
    // Ensure createdAt is recent enough to not be expired
    bookingToFinalize.setCreatedAt(LocalDateTime.now());

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingToFinalize));
    when(bookingRepository.save(any(Booking.class))).thenReturn(bookingToFinalize);

    // Act
    bookingService.finalizeBookingPayment(bookingId);

    // Assert
    assertThat(bookingToFinalize.getStatus()).isEqualTo("paid");
    verify(bookingRepository, times(1)).findById(bookingId);
    verify(bookingRepository, times(1)).save(bookingToFinalize);
    verify(bookingRepository, never()).delete(any(Booking.class)); // Ensure not deleted
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-26")
  void finalizeBookingPayment_BookingNotFound_ThrowsException() {
    // Arrange
    Long nonExistentBookingId = 999L;
    when(bookingRepository.findById(nonExistentBookingId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> bookingService.finalizeBookingPayment(nonExistentBookingId))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Booking not found");

    // Verify
    verify(bookingRepository, times(1)).findById(nonExistentBookingId);
    verify(bookingRepository, never()).save(any(Booking.class));
    verify(bookingRepository, never()).delete(any(Booking.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-26")
  void finalizeBookingPayment_BookingExpired_ThrowsExceptionAndDeletesBooking() {
    // Arrange
    Long bookingId = 456L;
    Booking expiredBooking = new Booking();
    expiredBooking.setBookingId(bookingId);
    expiredBooking.setStatus("not_used");
    // Set createdAt to be more than 5 minutes ago
    expiredBooking.setCreatedAt(LocalDateTime.now().minusMinutes(6));

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(expiredBooking));

    // Act & Assert
    assertThatThrownBy(() -> bookingService.finalizeBookingPayment(bookingId))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Booking expired");

    // Verify
    verify(bookingRepository, times(1)).findById(bookingId);
    verify(bookingRepository, times(1)).delete(expiredBooking); // Booking should be deleted
    verify(bookingRepository, never()).save(any(Booking.class)); // Should not save
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-26")
  void finalizeBookingPayment_BookingAlreadyPaid_ThrowsException() {
    // Arrange
    Long bookingId = 789L;
    Booking paidBooking = new Booking();
    paidBooking.setBookingId(bookingId);
    paidBooking.setStatus("paid"); // Already paid
    paidBooking.setCreatedAt(LocalDateTime.now().minusMinutes(1)); // Not expired

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(paidBooking));

    // Act & Assert
    assertThatThrownBy(() -> bookingService.finalizeBookingPayment(bookingId))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Booking already paid");

    // Verify
    verify(bookingRepository, times(1)).findById(bookingId);
    verify(bookingRepository, never()).save(any(Booking.class)); // Should not save
    verify(bookingRepository, never()).delete(any(Booking.class)); // Should not delete
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-27")
  void getBookingsToUnlock_InvalidDriver_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidDriverId = 999L;
    when(evDriverRepository.findById(invalidDriverId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> bookingService.getBookingsToUnlock(invalidDriverId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Driver not found with id: " + invalidDriverId);

    verify(evDriverRepository, times(1)).findById(invalidDriverId);
    verify(bookingRepository, never()).findByDriver(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-27")
  void deleteNotUsedBookings_DeletesExpiredNotUsedBookings() {
    // Arrange
    Booking expiredBooking = new Booking();
    expiredBooking.setBookingId(1L);
    expiredBooking.setStatus("not_used");
    expiredBooking.setCreatedAt(LocalDateTime.now().minusMinutes(10));

    Booking freshBooking = new Booking();
    freshBooking.setBookingId(2L);
    freshBooking.setStatus("not_used");
    freshBooking.setCreatedAt(LocalDateTime.now());

    List<Booking> bookings = List.of(expiredBooking, freshBooking);

    // Act
    bookingService.deleteNotUsedBookings(bookings);

    // Assert
    verify(bookingRepository, times(1)).delete(expiredBooking);
    verify(bookingRepository, never()).delete(freshBooking);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-27")
  void unlockChargingSlot_DriverDoesNotMatch_ThrowsException() {
    // Arrange
    Long bookingId = 1L;
    Long driverId = 2L;

    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    EvDriver bookingDriver = new EvDriver();
    bookingDriver.setUserId(3L);
    booking.setDriver(bookingDriver);

    EvDriver evDriver = new EvDriver();
    evDriver.setUserId(driverId);

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
    when(evDriverRepository.findById(driverId)).thenReturn(Optional.of(evDriver));

    // Act & Assert
    assertThatThrownBy(() -> bookingService.unlockChargingSlot(bookingId, driverId))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Driver does not match booking driver");
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-27")
  void unlockChargingSlot_BookingNotPaidOrAlreadyUsed_ThrowsException() {
    // Arrange
    Long bookingId = 1L;
    Long driverId = 2L;

    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    booking.setStatus("not_used"); // Not "paid"
    EvDriver evDriver = new EvDriver();
    evDriver.setUserId(driverId);
    booking.setDriver(evDriver);

    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
    when(evDriverRepository.findById(driverId)).thenReturn(Optional.of(evDriver));

    // Act & Assert
    assertThatThrownBy(() -> bookingService.unlockChargingSlot(bookingId, driverId))
        .isInstanceOf(Exception.class)
        .hasMessageContaining("Booking is not paid or already used");
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-36")
  void getEnergyConsumption_ReturnsOperatorEnergyResponse() {
    // Arrange
    List<Booking> bookings = createBookingsAndSessions();
    when(bookingRepository.findAll()).thenReturn(bookings);

    // Act
    OperatorEnergyResponse response = bookingService.getEnergyConsumption();

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getTotalEnergy()).isEqualTo(20.0);
    assertThat(response.getAverageEnergyPerMonth()).isEqualTo(1.82);

    verify(bookingRepository, times(1)).findAll();
  }

  // ======================== Auxiliar Methods ========================
  List<Booking> createBookingsAndSessions() {
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime startOfCurrentMonth =
        now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
    LocalDateTime startOfPreviousMonth = startOfCurrentMonth.minusMonths(1);
    LocalDateTime startOfPreviousOfPreviousMonth = startOfPreviousMonth.minusMonths(1);

    // Create bookings
    Booking booking1 =
        new Booking(
            testDriver, testSlot, startOfPreviousMonth.plusDays(5).plusHours(10), "used", 20.0);
    ChargingSession session1 = new ChargingSession(5, booking1);
    booking1.setChargingSession(session1);
    Booking booking2 =
        new Booking(
            testDriver, testSlot, startOfPreviousMonth.plusDays(10).plusHours(12), "used", 25.0);
    ChargingSession session2 = new ChargingSession(5, booking2);
    booking2.setChargingSession(session2);
    Booking booking3 =
        new Booking(
            testDriver,
            testSlot,
            startOfPreviousOfPreviousMonth.plusDays(3).plusHours(8),
            "used",
            30.0);
    ChargingSession session3 = new ChargingSession(5, booking3);
    booking3.setChargingSession(session3);
    Booking booking4 =
        new Booking(
            testDriver,
            testSlot,
            startOfPreviousOfPreviousMonth.plusDays(15).plusHours(14),
            "used",
            35.0);
    ChargingSession session4 = new ChargingSession(5, booking4);
    booking4.setChargingSession(session4);
    Booking booking5 =
        new Booking(
            testDriver,
            testSlot,
            startOfPreviousOfPreviousMonth.plusDays(20).plusHours(16),
            "not_used",
            40.0);
    ChargingSession session5 = new ChargingSession(5, booking5);
    booking5.setChargingSession(session5);

    return List.of(booking1, booking2, booking3, booking4, booking5);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-28")
  void getBookingDetails_ValidId_ReturnsBooking() throws ResourceNotFoundException {
    // Arrange
    Long bookingId = 1L;
    Booking booking = new Booking();
    booking.setBookingId(bookingId);
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
    // Act
    Booking result = bookingService.getBookingDetails(bookingId);
    // Assert
    assertNotNull(result);
    assertEquals(bookingId, result.getBookingId());
    verify(bookingRepository, times(1)).findById(bookingId);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-28")
  void getBookingDetails_InvalidId_ThrowsException() {
    // Arrange
    Long bookingId = 2L;
    when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());
    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class, () -> bookingService.getBookingDetails(bookingId));
    assertTrue(exception.getMessage().contains("Booking not found with id: " + bookingId));
    verify(bookingRepository, times(1)).findById(bookingId);
  }
}
