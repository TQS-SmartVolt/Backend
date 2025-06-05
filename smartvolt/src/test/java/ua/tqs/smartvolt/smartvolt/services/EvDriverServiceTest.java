package ua.tqs.smartvolt.smartvolt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.ChargingHistoryResponse;
import ua.tqs.smartvolt.smartvolt.dto.ConsumptionResponse;
import ua.tqs.smartvolt.smartvolt.dto.SpendingResponse;
import ua.tqs.smartvolt.smartvolt.dto.UserInfoResponse;
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

  // --- User Story 5.2 - View Personal Charging Statistics (Consumption) ---

  @Test
  @Tag("UnitTest")
  @Requirement("SV-33") // User Story 5.2 - View Personal Charging Statistics
  void
      getEvDriverConsumption_ValidDriverWithBookingsForCurrentMonth_ReturnsCorrectMonthlyConsumption()
          throws ResourceNotFoundException {
    // Arrange
    // Create new bookings specifically for this test, set to today's date
    LocalDateTime today = LocalDateTime.now();

    Booking booking4 =
        new Booking(
            testDriver,
            slotSlow,
            today.withHour(9).withMinute(0).withSecond(0).withNano(0),
            "Not Used",
            costSlow);
    booking4.setBookingId(4L);
    booking4.setCreatedAt(today.minusMinutes(1)); // Ensure not expired

    Booking booking5 =
        new Booking(
            testDriver,
            slotMedium,
            today.withHour(15).withMinute(30).withSecond(0).withNano(0),
            "Not Used",
            costMedium);
    booking5.setBookingId(5L);
    booking5.setCreatedAt(today.minusMinutes(1)); // Ensure not expired

    List<Booking> driverBookings = Arrays.asList(booking4, booking5);
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(driverBookings));

    // Act
    ConsumptionResponse consumptionResponse =
        evDriverService.getEvDriverConsumption(testDriver.getUserId());

    // Assert
    assertThat(consumptionResponse).isNotNull();
    List<Double> monthlyConsumption = consumptionResponse.getConsumptionPerMonth();
    assertThat(monthlyConsumption).isNotNull().hasSize(12);

    // Calculate expected total consumption for the current month based on booking4 and booking5
    int currentMonth = LocalDateTime.now().getMonthValue();
    double expectedCurrentMonthConsumption =
        (booking4.getSlot().getPower() * factor) + (booking5.getSlot().getPower() * factor);

    // Verify consumption for the current month
    assertThat(monthlyConsumption.get(currentMonth - 1)).isEqualTo(expectedCurrentMonthConsumption);

    // Verify other months are 0.0 (since our test bookings are only in one month)
    for (int i = 0; i < 12; i++) {
      if (i != (currentMonth - 1)) {
        assertThat(monthlyConsumption.get(i)).isEqualTo(0.0);
      }
    }

    // Verify repository interactions
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(bookingRepository, times(1)).findByDriver(testDriver);
    // Verify cleanup method is called
    verify(bookingRepository, times(1))
        .deleteExpiredBookingsByDriverAndStatus(
            eq(testDriver), any(LocalDateTime.class), eq("Not Used"));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-33")
  void getEvDriverConsumption_DriverNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    Long nonExistentUserId = 999L;
    when(evDriverRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> evDriverService.getEvDriverConsumption(nonExistentUserId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("EvDriver not found with id: " + nonExistentUserId);

    // Verify
    verify(evDriverRepository, times(1)).findById(nonExistentUserId);
    verify(bookingRepository, never()).findByDriver(any(EvDriver.class));
    verify(bookingRepository, never()).deleteExpiredBookingsByDriverAndStatus(any(), any(), any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-33")
  void getEvDriverConsumption_ValidDriverNoBookings_ReturnsEmptyConsumptionResponse()
      throws ResourceNotFoundException {
    // Arrange
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(Collections.emptyList()));

    // Act
    ConsumptionResponse consumptionResponse =
        evDriverService.getEvDriverConsumption(testDriver.getUserId());

    // Assert
    assertThat(consumptionResponse).isNotNull();
    List<Double> monthlyConsumption = consumptionResponse.getConsumptionPerMonth();
    assertThat(monthlyConsumption).isNotNull().hasSize(12);
    assertThat(monthlyConsumption).containsOnly(0.0); // All 12 months should be 0.0

    // Verify
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(bookingRepository, times(1)).findByDriver(testDriver);
    verify(bookingRepository, times(1))
        .deleteExpiredBookingsByDriverAndStatus(
            eq(testDriver), any(LocalDateTime.class), eq("Not Used"));
  }

  // --- User Story 5.2 - View Personal Charging Statistics (Spending) ---

  @Test
  @Tag("UnitTest")
  @Requirement("SV-33") // User Story 5.2 - View Personal Charging Statistics
  void getEvDriverSpending_ValidDriverWithBookingsForCurrentMonth_ReturnsCorrectMonthlySpending()
      throws ResourceNotFoundException {
    // Arrange
    // Create new bookings specifically for this test, set to today's date
    LocalDateTime today = LocalDateTime.now();

    Booking booking6 =
        new Booking(
            testDriver,
            slotSlow,
            today.withHour(10).withMinute(0).withSecond(0).withNano(0),
            "Not Used",
            costSlow);
    booking6.setBookingId(6L);
    booking6.setCreatedAt(today.minusMinutes(1)); // Ensure not expired

    Booking booking7 =
        new Booking(
            testDriver,
            slotFast,
            today.withHour(16).withMinute(30).withSecond(0).withNano(0),
            "Not Used",
            costFast);
    booking7.setBookingId(7L);
    booking7.setCreatedAt(today.minusMinutes(1)); // Ensure not expired

    List<Booking> driverBookings = Arrays.asList(booking6, booking7);
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(driverBookings));

    // Act
    SpendingResponse spendingResponse = evDriverService.getEvDriverSpending(testDriver.getUserId());

    // Assert
    assertThat(spendingResponse).isNotNull();
    List<Double> monthlySpending = spendingResponse.getSpendingPerMonth();
    assertThat(monthlySpending).isNotNull().hasSize(12);

    // Calculate expected total spending for the current month based on booking6 and booking7
    int currentMonth = LocalDateTime.now().getMonthValue();
    double expectedCurrentMonthSpending = booking6.getCost() + booking7.getCost();

    // Verify spending for the current month
    assertThat(monthlySpending.get(currentMonth - 1)).isEqualTo(expectedCurrentMonthSpending);

    // Verify other months are 0.0 (since our test bookings are only in one month)
    for (int i = 0; i < 12; i++) {
      if (i != (currentMonth - 1)) {
        assertThat(monthlySpending.get(i)).isEqualTo(0.0);
      }
    }

    // Verify repository interactions
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(bookingRepository, times(1)).findByDriver(testDriver);
    // Verify cleanup method is called
    verify(bookingRepository, times(1))
        .deleteExpiredBookingsByDriverAndStatus(
            eq(testDriver), any(LocalDateTime.class), eq("Not Used"));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-33")
  void getEvDriverSpending_DriverNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    Long nonExistentUserId = 999L;
    when(evDriverRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> evDriverService.getEvDriverSpending(nonExistentUserId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("EvDriver not found with id: " + nonExistentUserId);

    // Verify
    verify(evDriverRepository, times(1)).findById(nonExistentUserId);
    verify(bookingRepository, never()).findByDriver(any(EvDriver.class));
    verify(bookingRepository, never()).deleteExpiredBookingsByDriverAndStatus(any(), any(), any());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-33")
  void getEvDriverSpending_ValidDriverNoBookings_ReturnsEmptySpendingResponse()
      throws ResourceNotFoundException {
    // Arrange
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(Collections.emptyList()));

    // Act
    SpendingResponse spendingResponse = evDriverService.getEvDriverSpending(testDriver.getUserId());

    // Assert
    assertThat(spendingResponse).isNotNull();
    List<Double> monthlySpending = spendingResponse.getSpendingPerMonth();
    assertThat(monthlySpending).isNotNull().hasSize(12);
    assertThat(monthlySpending).containsOnly(0.0); // All 12 months should be 0.0

    // Verify
    verify(evDriverRepository, times(1)).findById(testDriver.getUserId());
    verify(bookingRepository, times(1)).findByDriver(testDriver);
    verify(bookingRepository, times(1))
        .deleteExpiredBookingsByDriverAndStatus(
            eq(testDriver), any(LocalDateTime.class), eq("Not Used"));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-31")
  void getEvDriverInfo_ValidDriver_ReturnsUserInfoResponse() throws ResourceNotFoundException {
    // Arrange
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(Collections.emptyList()));

    // Act
    UserInfoResponse userInfo = evDriverService.getEvDriverInfo(testDriver.getUserId());

    // Assert
    assertThat(userInfo).isNotNull();
    assertThat(userInfo.getName()).isEqualTo(testDriver.getName());
    assertThat(userInfo.getEmail()).isEqualTo(testDriver.getEmail());
    assertThat(userInfo.getTotalEnergyConsumed()).isEqualTo(0.0);
    assertThat(userInfo.getTotalMoneySpent()).isEqualTo(0.0);

    // Verify repository interactions
    verify(evDriverRepository, times(3)).findById(testDriver.getUserId());
    verify(bookingRepository, times(2)).findByDriver(testDriver);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-31")
  void getEvDriverInfo_DriverNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    Long nonExistentUserId = 999L;
    when(evDriverRepository.findById(nonExistentUserId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(() -> evDriverService.getEvDriverInfo(nonExistentUserId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("EvDriver not found with id: " + nonExistentUserId);

    // Verify
    verify(evDriverRepository, times(1)).findById(nonExistentUserId);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-31")
  void getEvDriverInfo_ValidDriverWithBookings_ReturnsUserInfoResponseWithTotals()
      throws ResourceNotFoundException {
    // Arrange
    List<Booking> driverBookings = Arrays.asList(booking1, booking2, booking3);
    when(bookingRepository.findByDriver(testDriver)).thenReturn(Optional.of(driverBookings));

    // Act
    UserInfoResponse userInfo = evDriverService.getEvDriverInfo(testDriver.getUserId());

    // Assert
    assertThat(userInfo).isNotNull();
    assertThat(userInfo.getName()).isEqualTo(testDriver.getName());
    assertThat(userInfo.getEmail()).isEqualTo(testDriver.getEmail());

    double totalEnergyConsumed =
        (booking1.getSlot().getPower() * factor)
            + (booking2.getSlot().getPower() * factor)
            + (booking3.getSlot().getPower() * factor);
    assertThat(userInfo.getTotalEnergyConsumed()).isEqualTo(totalEnergyConsumed);

    double totalMoneySpent = booking1.getCost() + booking2.getCost() + booking3.getCost();
    assertThat(userInfo.getTotalMoneySpent()).isEqualTo(totalMoneySpent);

    // Verify repository interactions
    verify(evDriverRepository, times(3)).findById(testDriver.getUserId());
    verify(bookingRepository, times(2)).findByDriver(testDriver);
  }
}
