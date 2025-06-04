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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.ChargingHistoryResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@ExtendWith(MockitoExtension.class)
public class EvDriverServiceTest {

  @Mock private EvDriverRepository evDriverRepository;
  @Mock private BookingRepository bookingRepository;

  private EvDriverService evDriverService;

  // Common test data
  private EvDriver testDriver;
  private ChargingStation stationSlow;
  private ChargingStation stationMedium;
  private ChargingStation stationFast;
  private ChargingSlot slotSlow;
  private ChargingSlot slotMedium;
  private ChargingSlot slotFast;
  private Booking booking1;
  private Booking booking2;
  private Booking booking3;

  // Common test data setup
  double factor = 0.5; // Assuming a constant factor for energy delivered calculation
  double pricePerKWhSlow = 0.15;
  double pricePerKWhMedium = 0.25;
  double pricePerKWhFast = 0.40;
  double powerSlow = 10.0;
  double powerMedium = 20.0;
  double powerFast = 30.0;
  double energyDeliveredSlow = powerSlow * factor;
  double energyDeliveredMedium = powerMedium * factor;
  double energyDeliveredFast = powerFast * factor;
  double costSlow = (energyDeliveredSlow * pricePerKWhSlow);
  double costMedium = (energyDeliveredMedium * pricePerKWhMedium);
  double costFast = (energyDeliveredFast * pricePerKWhFast);

  @BeforeEach
  void setUp() {
    evDriverService = new EvDriverService(evDriverRepository, bookingRepository);

    testDriver = new EvDriver("Test Driver", "driver@example.com", "password123");
    testDriver.setUserId(101L);

    StationOperator operator =
        new StationOperator("Test Operator", "operator@example.com", "opass");
    operator.setUserId(1L);

    stationSlow = new ChargingStation("Station Slow", 40.0, -8.0, "Address A", true, operator);
    stationSlow.setStationId(200L);
    stationMedium = new ChargingStation("Station Medium", 41.0, -9.0, "Address B", true, operator);
    stationMedium.setStationId(201L);
    stationFast = new ChargingStation("Station Fast", 42.0, -10.0, "Address C", true, operator);
    stationFast.setStationId(202L);

    slotSlow = new ChargingSlot(true, pricePerKWhSlow, powerSlow, "Slow", stationSlow);
    slotSlow.setSlotId(300L);
    slotMedium = new ChargingSlot(true, pricePerKWhMedium, powerMedium, "Medium", stationMedium);
    slotMedium.setSlotId(301L);
    slotFast = new ChargingSlot(true, pricePerKWhFast, powerFast, "Fast", stationFast);
    slotFast.setSlotId(302L);

    // Booking 1: Slow charging
    LocalDateTime startTime1 =
        LocalDateTime.now().minusDays(1).withHour(10).withMinute(0).withSecond(0).withNano(0);
    booking1 = new Booking(testDriver, slotSlow, startTime1, "not_used", costSlow);
    booking1.setBookingId(1L);

    // Booking 2: Medium charging
    LocalDateTime startTime2 =
        LocalDateTime.now().minusDays(2).withHour(14).withMinute(30).withSecond(0).withNano(0);
    booking2 = new Booking(testDriver, slotMedium, startTime2, "not_used", costMedium);
    booking2.setBookingId(2L);

    // Booking 3: Fast charging
    LocalDateTime startTime3 =
        LocalDateTime.now().minusDays(3).withHour(11).withMinute(0).withSecond(0).withNano(0);
    booking3 = new Booking(testDriver, slotFast, startTime3, "not_used", costFast);
    booking3.setBookingId(3L);

    // Common stubbing for evDriverRepository.findById
    lenient().when(evDriverRepository.findById(anyLong())).thenReturn(Optional.of(testDriver));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-32") // User Story 5.1 - View Charging History
  void getEvDriverBookings_ValidDriverWithBookings_ReturnsChargingHistory()
      throws ResourceNotFoundException {
    // Arrange
    List<Booking> driverBookings = Arrays.asList(booking1, booking2, booking3);
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(driverBookings));

    // Act
    List<ChargingHistoryResponse> history =
        evDriverService.getEvDriverBookings(testDriver.getUserId());

    // Assert
    assertThat(history).isNotNull().hasSize(3);

    // Assertions for booking1 (Slow)
    ChargingHistoryResponse response1 = history.get(0); // Assuming order is preserved from mock
    assertThat(response1.getStartTime()).isEqualTo(booking1.getStartTime());
    assertThat(response1.getChargingStationName()).isEqualTo(stationSlow.getName());
    assertThat(response1.getChargingSpeed()).isEqualTo(slotSlow.getChargingSpeed());
    assertThat(response1.getPower()).isEqualTo(slotSlow.getPower());
    assertThat(response1.getEnergyDelivered()).isEqualTo(slotSlow.getPower() * 0.5);
    assertThat(response1.getPricePerKWh()).isEqualTo(slotSlow.getPricePerKWh());
    assertThat(response1.getCost()).isEqualTo(booking1.getCost());

    // Assertions for booking2 (Medium)
    ChargingHistoryResponse response2 = history.get(1);
    assertThat(response2.getStartTime()).isEqualTo(booking2.getStartTime());
    assertThat(response2.getChargingStationName()).isEqualTo(stationMedium.getName());
    assertThat(response2.getChargingSpeed()).isEqualTo(slotMedium.getChargingSpeed());
    assertThat(response2.getPower()).isEqualTo(slotMedium.getPower());
    assertThat(response2.getEnergyDelivered()).isEqualTo(slotMedium.getPower() * 0.5);
    assertThat(response2.getPricePerKWh()).isEqualTo(slotMedium.getPricePerKWh());
    assertThat(response2.getCost()).isEqualTo(booking2.getCost());

    // Assertions for booking3 (Fast)
    ChargingHistoryResponse response3 = history.get(2);
    assertThat(response3.getStartTime()).isEqualTo(booking3.getStartTime());
    assertThat(response3.getChargingStationName()).isEqualTo(stationFast.getName());
    assertThat(response3.getChargingSpeed()).isEqualTo(slotFast.getChargingSpeed());
    assertThat(response3.getPower()).isEqualTo(slotFast.getPower());
    assertThat(response3.getEnergyDelivered()).isEqualTo(slotFast.getPower() * 0.5);
    assertThat(response3.getPricePerKWh()).isEqualTo(slotFast.getPricePerKWh());
    assertThat(response3.getCost()).isEqualTo(booking3.getCost());

    // Verify repository interactions
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(bookingRepository, times(1)).findByDriver(testDriver);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-32")
  void getEvDriverBookings_DriverNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    Long nonExistentUserId = 999L;
    when(evDriverRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> evDriverService.getEvDriverBookings(nonExistentUserId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("EvDriver not found with id: " + nonExistentUserId);

    // Verify
    verify(evDriverRepository, times(1)).findById(nonExistentUserId);
    verify(bookingRepository, never())
        .findByDriver(any(EvDriver.class)); // Should not attempt to find bookings
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-32")
  void getEvDriverBookings_ValidDriverNoBookings_ReturnsEmptyList()
      throws ResourceNotFoundException {
    // Arrange
    // evDriverRepository.findById already returns testDriver from @BeforeEach
    when(bookingRepository.findByDriver(testDriver))
        .thenReturn(Optional.of(Arrays.asList())); // Return empty list of bookings

    // Act
    List<ChargingHistoryResponse> history =
        evDriverService.getEvDriverBookings(testDriver.getUserId());

    // Assert
    assertThat(history).isNotNull().isEmpty();

    // Verify
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(bookingRepository, times(1)).findByDriver(testDriver);
  }

  // Test for getEvDriverByEmail
  @Test
  @Tag("UnitTest")
  @Requirement("SV-32")
  void getEvDriverByEmail_ExistingEmail_ReturnsEvDriver() throws ResourceNotFoundException {
    // Arrange
    String email = "driver@example.com";
    when(evDriverRepository.findByEmail(email)).thenReturn(Optional.of(testDriver));

    // Act
    Optional<EvDriver> foundDriver = evDriverService.getEvDriverByEmail(email);

    // Assert
    assertThat(foundDriver).isPresent().contains(testDriver);
    verify(evDriverRepository, times(1)).findByEmail(email);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-32")
  void getEvDriverByEmail_NonExistingEmail_ReturnsEmptyOptional() throws ResourceNotFoundException {
    // Arrange
    String email = "nonexistent@example.com";
    when(evDriverRepository.findByEmail(email)).thenReturn(Optional.empty());

    // Act
    Optional<EvDriver> foundDriver = evDriverService.getEvDriverByEmail(email);

    // Assert
    assertThat(foundDriver).isEmpty();
    verify(evDriverRepository, times(1)).findByEmail(email);
  }

  // Test for getEvDriverById
  @Test
  @Tag("UnitTest")
  @Requirement("SV-32")
  void getEvDriverById_ExistingId_ReturnsEvDriver() throws ResourceNotFoundException {
    // Arrange
    Long userId = testDriver.getUserId();
    // Mocked in BeforeEach, but explicitly for clarity
    when(evDriverRepository.findById(userId)).thenReturn(Optional.of(testDriver));

    // Act
    Optional<EvDriver> foundDriver = evDriverService.getEvDriverById(userId);

    // Assert
    assertThat(foundDriver).isPresent().contains(testDriver);
    verify(evDriverRepository, times(1)).findById(userId);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-32")
  void getEvDriverById_NonExistingId_ReturnsEmptyOptional() throws ResourceNotFoundException {
    // Arrange
    Long nonExistentId = 999L;
    when(evDriverRepository.findById(nonExistentId)).thenReturn(Optional.empty());

    // Act
    Optional<EvDriver> foundDriver = evDriverService.getEvDriverById(nonExistentId);

    // Assert
    assertThat(foundDriver).isEmpty();
    verify(evDriverRepository, times(1)).findById(nonExistentId);
  }
}
