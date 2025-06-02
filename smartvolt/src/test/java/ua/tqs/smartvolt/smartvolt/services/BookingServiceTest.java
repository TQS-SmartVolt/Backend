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
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.exceptions.SlotAlreadyBookedException;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

  @Mock private BookingRepository bookingRepository;
  @Mock private EvDriverRepository evDriverRepository;
  @Mock private ChargingSlotRepository chargingSlotRepository;

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
        new BookingService(bookingRepository, evDriverRepository, chargingSlotRepository);

    testDriver = new EvDriver();
    testDriver.setUserId(101L);
    testDriver.setEmail("driver@example.com");
    testDriver.setName("Test Driver");

    StationOperator operator = new StationOperator();
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
    expectedBooking.setBookingId(1L);
    expectedBooking.setSlot(testSlot);
    expectedBooking.setDriver(testDriver);
    expectedBooking.setStartTime(validBookingRequest.getStartTime());
    expectedBooking.setStatus("Not Used");
    expectedBooking.setCost(1.5);

    preExistingBooking = new Booking();
    preExistingBooking.setBookingId(99L);
    preExistingBooking.setSlot(testSlot);
    preExistingBooking.setStartTime(validBookingRequest.getStartTime());
    preExistingBooking.setStatus("Not Used");

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
    assertThat(createdBooking.getStatus()).isEqualTo("Not Used");
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
}
