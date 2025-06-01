package ua.tqs.smartvolt.smartvolt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDateTime;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks; // Use InjectMocks for the service under test
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dtos.BookingRequest; // Assuming this DTO exists
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.exceptions.SlotAlreadyBookedException;
// Removed InvalidBookingTimeException as we decided to use IllegalArgumentException
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator; // Needed for ChargingStation
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;
  @Mock private EvDriverRepository evDriverRepository;
  @Mock private ChargingSlotRepository chargingSlotRepository;

  @InjectMocks // This will inject the mocks into BookingService automatically
  private BookingService bookingService;

  // Common test data
  private EvDriver testDriver;
  private ChargingSlot testSlot;
  private BookingRequest validBookingRequest;
  private Booking expectedBooking;
  private ChargingStation testStation;

  @BeforeEach
  void setUp() {
    // Manually inject mocks if @InjectMocks isn't used or for complex scenarios
    // bookingService = new BookingService(bookingRepository, evDriverRepository,
    // chargingSlotRepository);

    // Set up common mock entities
    testDriver = new EvDriver();
    testDriver.setUserId(101L);
    testDriver.setEmail("driver@example.com");
    testDriver.setName("Test Driver");
    // Other driver properties as needed for your application

    StationOperator operator = new StationOperator(); // Assuming operator is needed for station
    operator.setUserId(1L);

    testStation = new ChargingStation("Test Station", 40.0, -8.0, "Test Address", true, operator);
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
    expectedBooking.setBookingId(1L); // Assuming ID is generated
    expectedBooking.setSlot(testSlot);
    expectedBooking.setDriver(testDriver);
    expectedBooking.setStartTime(validBookingRequest.getStartTime());
    expectedBooking.setStatus("Not Used");
    expectedBooking.setCost(1.5); // 10.0 power * 0.15 price = 1.5

    // Lenient stubbing for common repository calls to avoid "unnecessary stubbing" errors
    // Default: slot and driver exist, and slot is not booked
    lenient().when(evDriverRepository.findById(anyLong())).thenReturn(Optional.of(testDriver));
    lenient().when(chargingSlotRepository.findById(anyLong())).thenReturn(Optional.of(testSlot));
    lenient()
        .when(
            bookingRepository.existsBySlotAndStartTime(
                any(ChargingSlot.class), any(LocalDateTime.class)))
        .thenReturn(false);
    lenient().when(bookingRepository.save(any(Booking.class))).thenReturn(expectedBooking);

    // Mock specific methods used for cost calculation
    lenient()
        .when(chargingSlotRepository.getPowerBySlotId(anyLong()))
        .thenReturn(Optional.of(testSlot.getPower()));
    lenient()
        .when(chargingSlotRepository.getPricePerKWhBySlotId(anyLong()))
        .thenReturn(Optional.of(testSlot.getPricePerKWh()));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-25") // Corresponds to the main booking creation requirement
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
    assertThat(createdBooking.getStatus()).isEqualTo("Not Used"); // Default status for new booking
    assertThat(createdBooking.getCost()).isEqualTo(expectedBooking.getCost());

    // Verify repository interactions
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(chargingSlotRepository, times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    verify(bookingRepository, times(1)).save(any(Booking.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-25")
  void createBooking_InvalidSlotId_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidSlotId = 999L;
    BookingRequest requestWithInvalidSlot =
        new BookingRequest(invalidSlotId, validBookingRequest.getStartTime());

    when(chargingSlotRepository.findById(invalidSlotId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(requestWithInvalidSlot, testDriver.getUserId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Charging slot not found with id: " + invalidSlotId);

    // Verify that other methods were not called after the exception
    verify(evDriverRepository, never()).findById(anyLong());
    verify(bookingRepository, never()).existsBySlotAndStartTime(any(), any());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-25")
  void createBooking_InvalidDriverId_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidDriverId = 998L;

    when(evDriverRepository.findById(invalidDriverId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> bookingService.createBooking(validBookingRequest, invalidDriverId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("EV Driver not found with id: " + invalidDriverId);

    // Verify that other methods were not called after the exception
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId()); // Slot is found first
    verify(bookingRepository, never()).existsBySlotAndStartTime(any(), any());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-26") // Slot already booked
  void createBooking_SlotAlreadyBooked_ThrowsSlotAlreadyBookedException() {
    // Arrange
    // Mock existsBySlotAndStartTime to return true, indicating a booking conflict
    when(bookingRepository.existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime()))
        .thenReturn(true);

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
        .existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(bookingRepository, never()).save(any()); // Booking should not be saved
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-27") // 30-minute interval validation
  void createBooking_InvalidStartTimeInterval_ThrowsIllegalArgumentException() {
    // Arrange
    LocalDateTime invalidTime =
        LocalDateTime.now()
            .plusDays(1)
            .withHour(10)
            .withMinute(15)
            .withSecond(10)
            .withNano(100); // 10:15:10.100
    BookingRequest request = new BookingRequest(testSlot.getSlotId(), invalidTime);

    // Act & Assert
    assertThatThrownBy(() -> bookingService.createBooking(request, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining(
            "Booking start time must be on a 30-minute interval (e.g., HH:00 or HH:30).");

    // Verify that no further repository calls were made after validation
    verify(evDriverRepository, never()).findById(anyLong()); // Driver not fetched yet
    verify(chargingSlotRepository, never()).findById(anyLong()); // Slot not fetched yet
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-XX") // Assign a suitable new Requirement ID
  void createBooking_PowerNotFoundForSlot_ThrowsResourceNotFoundException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.empty());
    when(chargingSlotRepository.getPricePerKWhBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(testSlot.getPricePerKWh())); // Price is found

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Power not found for slotId: " + testSlot.getSlotId());

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1))
        .getPowerBySlotId(testSlot.getSlotId()); // Should be called
    verify(chargingSlotRepository, never())
        .getPricePerKWhBySlotId(anyLong()); // This should not be called if power not found
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-XX") // Assign a suitable new Requirement ID
  void createBooking_PriceNotFoundForSlot_ThrowsResourceNotFoundException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(testSlot.getPower())); // Power is found
    when(chargingSlotRepository.getPricePerKWhBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.empty()); // Price is not found

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Price not found for slotId: " + testSlot.getSlotId());

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1))
        .getPowerBySlotId(testSlot.getSlotId()); // Should be called
    verify(chargingSlotRepository, times(1))
        .getPricePerKWhBySlotId(testSlot.getSlotId()); // Should be called
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-XX") // Assign a suitable new Requirement ID
  void createBooking_NegativePower_ThrowsIllegalArgumentException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(-5.0)); // Negative power

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid power or price");

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    // getPricePerKWhBySlotId might or might not be called depending on short-circuiting in the
    // service
    // if power is negative, it might throw before checking price.
    // For robustness, we check it wasn't called if the logic throws immediately.
    // If it short-circuits: verify(chargingSlotRepository,
    // never()).getPricePerKWhBySlotId(anyLong());
    // If it doesn't short-circuit before the check: verify(chargingSlotRepository,
    // times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    // Let's assume it proceeds to calculate cost, so both are called for this test.
    verify(chargingSlotRepository, times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    verify(bookingRepository, never()).save(any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-XX") // Assign a suitable new Requirement ID
  void createBooking_NegativePricePerKWh_ThrowsIllegalArgumentException() {
    // Arrange
    when(chargingSlotRepository.getPowerBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(testSlot.getPower())); // Positive power
    when(chargingSlotRepository.getPricePerKWhBySlotId(testSlot.getSlotId()))
        .thenReturn(Optional.of(-0.05)); // Negative price

    // Act & Assert
    assertThatThrownBy(
            () -> bookingService.createBooking(validBookingRequest, testDriver.getUserId()))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Invalid power or price");

    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(chargingSlotRepository, times(1)).findById(testSlot.getSlotId());
    verify(bookingRepository, times(1))
        .existsBySlotAndStartTime(testSlot, validBookingRequest.getStartTime());
    verify(chargingSlotRepository, times(1)).getPowerBySlotId(testSlot.getSlotId());
    verify(chargingSlotRepository, times(1)).getPricePerKWhBySlotId(testSlot.getSlotId());
    verify(bookingRepository, never()).save(any());
  }

  // Note: The `if (cost < 0)` check was determined to be redundant if power and price are >= 0.
  // If your service code still contains `if (cost < 0)`, you might create a test for it
  // but it would require mocking power or price to be negative (which is already covered).
  // Thus, no separate test for `cost < 0` is added here as it's logically impossible with valid
  // inputs.
}
