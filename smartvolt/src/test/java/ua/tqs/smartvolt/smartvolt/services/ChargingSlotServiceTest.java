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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotsResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.InvalidRequestException;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository; // Import BookingRepository
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;

@ExtendWith(MockitoExtension.class)
public class ChargingSlotServiceTest {

  @Mock private ChargingStationRepository chargingStationRepository;
  @Mock private ChargingSlotRepository chargingSlotRepository;
  @Mock private BookingRepository bookingRepository;

  private ChargingSlotService chargingSlotService;

  private ChargingStation stationSlow;
  private ChargingStation stationMedium;
  private ChargingSlot slot1_Slow;
  private ChargingSlot slot2_Slow;

  @BeforeEach
  void setUp() {
    // Initialize the service with all its dependencies
    chargingSlotService =
        new ChargingSlotService(
            chargingSlotRepository, chargingStationRepository, bookingRepository);

    // --- Set up the general lenient stubbing here ---
    // This ensures that for any call to existsBySlotAndStartTime that is not
    // specifically mocked later, it will default to returning 'false'
    // without triggering Strictness errors.
    lenient()
        .when(
            bookingRepository.existsBySlotAndStartTime(
                any(ChargingSlot.class), any(LocalDateTime.class)))
        .thenReturn(false);
    // ------------------------------------------------------------------

    // Setup common mock objects for a 'Slow' station with two slots
    StationOperator operator = new StationOperator();
    operator.setUserId(1L);

    stationSlow = new ChargingStation("Station Slow", 40.0, -8.0, "Address Slow", true, operator);
    stationSlow.setStationId(102L);

    stationMedium =
        new ChargingStation("Station Medium", 38.0, -9.0, "Address Medium", true, operator);
    stationMedium.setStationId(103L);

    slot1_Slow = new ChargingSlot();
    slot1_Slow.setSlotId(1L); // Unique ID for the first physical slot
    slot1_Slow.setPricePerKWh(0.15F);
    slot1_Slow.setPower(10);
    slot1_Slow.setChargingSpeed("Slow");
    slot1_Slow.setStation(stationSlow);

    slot2_Slow = new ChargingSlot();
    slot2_Slow.setSlotId(2L); // Unique ID for the second physical slot
    slot2_Slow.setPricePerKWh(0.15F); // Matching price from V002
    slot2_Slow.setPower(10);
    slot2_Slow.setChargingSpeed("Slow");
    slot2_Slow.setStation(stationSlow);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-24") // User Story 2.1
  void getAvailableSlots_ValidRequest_ReturnsCorrectSlots() throws ResourceNotFoundException {
    // Arrange
    Long stationId = stationSlow.getStationId();
    String chargingSpeed = "Slow";
    LocalDate date = LocalDate.now();

    // Mock repository calls
    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(stationSlow));
    when(chargingSlotRepository.findByStationAndChargingSpeed(stationSlow, chargingSpeed))
        .thenReturn(Arrays.asList(slot1_Slow, slot2_Slow));
    // Mock no bookings for any slot at any time
    when(bookingRepository.existsBySlotAndStartTime(
            any(ChargingSlot.class), any(LocalDateTime.class)))
        .thenReturn(false);

    // Act
    ChargingSlotsResponse response =
        chargingSlotService.getAvailableSlots(stationId, chargingSpeed, date);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getAvailableSlotMapping()).isNotNull();
    assertThat(response.getAvailableSlotMapping())
        .hasSize(96); // 2 physical slots * 48 half-hour slots per day
    assertThat(response.getPricePerKWh()).isEqualTo(0.15F); // Price for Slow slots

    // Verify unique slot IDs
    Set<Long> uniqueSlotIds =
        response.getAvailableSlotMapping().stream()
            .map(ChargingSlotsResponse.SlotAvailability::getSlotId)
            .collect(Collectors.toSet());
    assertThat(uniqueSlotIds)
        .hasSize(2) // Should contain exactly two distinct physical slot IDs
        .containsExactlyInAnyOrder(slot1_Slow.getSlotId(), slot2_Slow.getSlotId());

    // Verify repository interactions
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingSlotRepository, times(1))
        .findByStationAndChargingSpeed(stationSlow, chargingSpeed);
    // bookingRepository.existsBySlotAndStartTime should be called for each potential slot-time
    // combination
    // For 2 physical slots and 48 time slots, it's 2 * 48 = 96 calls
    verify(bookingRepository, times(96))
        .existsBySlotAndStartTime(any(ChargingSlot.class), any(LocalDateTime.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-24")
  void getAvailableSlots_InvalidStationId_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidStationId = 9999L;
    String chargingSpeed = "Slow";
    LocalDate date = LocalDate.now();

    // Mock findById to return empty, simulating station not found
    when(chargingStationRepository.findById(invalidStationId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(
            () -> chargingSlotService.getAvailableSlots(invalidStationId, chargingSpeed, date))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Charging station not found with id: " + invalidStationId);

    // Verify interactions: only findById should be called
    verify(chargingStationRepository, times(1)).findById(invalidStationId);
    verify(chargingSlotRepository, never())
        .findByStationAndChargingSpeed(any(ChargingStation.class), any(String.class));
    verify(bookingRepository, never())
        .existsBySlotAndStartTime(any(ChargingSlot.class), any(LocalDateTime.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-24")
  void getAvailableSlots_NoSlotsForSelectedSpeed_ReturnsEmptyListAndZeroPrice()
      throws ResourceNotFoundException {
    // Arrange
    Long stationId = stationMedium.getStationId(); // Use stationMedium
    String chargingSpeed = "Slow"; // Request 'Slow' speed which stationMedium doesn't have
    LocalDate date = LocalDate.now();

    // Mock findById to return the station
    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(stationMedium));
    // Mock findByStationAndChargingSpeed to return an empty list
    when(chargingSlotRepository.findByStationAndChargingSpeed(stationMedium, chargingSpeed))
        .thenReturn(Collections.emptyList());

    // Act
    ChargingSlotsResponse response =
        chargingSlotService.getAvailableSlots(stationId, chargingSpeed, date);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getAvailableSlotMapping()).isEmpty(); // Should be empty
    assertThat(response.getPricePerKWh()).isEqualTo(0.0F); // Should be 0.0F

    // Verify interactions
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingSlotRepository, times(1))
        .findByStationAndChargingSpeed(stationMedium, chargingSpeed);
    verify(bookingRepository, never())
        .existsBySlotAndStartTime(
            any(ChargingSlot.class),
            any(LocalDateTime.class)); // Crucial: no booking check if no slots found
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-24")
  void getAvailableSlots_PastDate_ReturnsEmptyListAndZeroPrice() throws ResourceNotFoundException {
    // Arrange
    Long stationId = stationSlow.getStationId(); // Use any valid station ID
    String chargingSpeed = "Slow"; // Use any valid charging speed
    LocalDate pastDate = LocalDate.now().minusDays(1); // Date in the past

    // No need to mock chargingStationRepository or chargingSlotRepository
    // because the service should return early due to the past date check.

    // Act
    ChargingSlotsResponse response =
        chargingSlotService.getAvailableSlots(stationId, chargingSpeed, pastDate);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getAvailableSlotMapping()).isEmpty(); // Should be empty
    assertThat(response.getPricePerKWh()).isEqualTo(0.0F); // Should be 0.0F

    // Verify that no further repository calls were made after detecting the past date
    verify(chargingStationRepository, never()).findById(anyLong()); // No need to find station
    verify(chargingSlotRepository, never())
        .findByStationAndChargingSpeed(
            any(ChargingStation.class), any(String.class)); // No need to find slots
    verify(bookingRepository, never())
        .existsBySlotAndStartTime(
            any(ChargingSlot.class), any(LocalDateTime.class)); // No need to check bookings
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-24")
  void getAvailableSlots_SomeSlotsBooked_ReturnsOnlyAvailableSlots()
      throws ResourceNotFoundException {
    // Arrange
    Long stationId = stationSlow.getStationId();
    String chargingSpeed = "Slow";
    LocalDate date = LocalDate.now();
    LocalDateTime bookedTime = date.atTime(10, 0); // A specific time to simulate booking

    // Mock specific repository calls
    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(stationSlow));
    when(chargingSlotRepository.findByStationAndChargingSpeed(stationSlow, chargingSpeed))
        .thenReturn(Arrays.asList(slot1_Slow, slot2_Slow));

    // NOW, specifically mock the booked slot. This will override the lenient default set in setUp.
    // This order is crucial for Mockito to resolve specific vs. general mocks.
    when(bookingRepository.existsBySlotAndStartTime(eq(slot1_Slow), eq(bookedTime)))
        .thenReturn(true);

    // Act
    ChargingSlotsResponse response =
        chargingSlotService.getAvailableSlots(stationId, chargingSpeed, date);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getAvailableSlotMapping()).isNotNull();
    // Total slots should be (2 physical slots * 48 half-hours) - 1 booked slot = 95
    assertThat(response.getAvailableSlotMapping()).hasSize(95);
    assertThat(response.getPricePerKWh()).isEqualTo(0.15F);

    // Verify that the specific booked slot-time combination is *not* in the response
    boolean bookedSlotFound =
        response.getAvailableSlotMapping().stream()
            .anyMatch(
                sa ->
                    sa.getSlotId().equals(slot1_Slow.getSlotId())
                        && sa.getStartTime().equals(bookedTime));
    assertThat(bookedSlotFound).isFalse();

    // Verify that the other slot at the same time *is* in the response (as it wasn't booked)
    boolean unbookedSlotAtSameTimeFound =
        response.getAvailableSlotMapping().stream()
            .anyMatch(
                sa ->
                    sa.getSlotId().equals(slot2_Slow.getSlotId())
                        && sa.getStartTime().equals(bookedTime));
    assertThat(unbookedSlotAtSameTimeFound).isTrue();

    // Verify repository interactions
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingSlotRepository, times(1))
        .findByStationAndChargingSpeed(stationSlow, chargingSpeed);
    // Still 96 checks performed in the service's loop
    verify(bookingRepository, times(96))
        .existsBySlotAndStartTime(any(ChargingSlot.class), any(LocalDateTime.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-24")
  void getAvailableSlots_FutureDate_ReturnsAvailableSlots() throws ResourceNotFoundException {
    // Arrange
    Long stationId = stationSlow.getStationId();
    String chargingSpeed = "Slow";
    LocalDate futureDate = LocalDate.now().plusDays(7); // A date in the future

    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(stationSlow));
    when(chargingSlotRepository.findByStationAndChargingSpeed(stationSlow, chargingSpeed))
        .thenReturn(Arrays.asList(slot1_Slow, slot2_Slow));
    // The lenient().when(bookingRepository.existsBySlotAndStartTime(any(),
    // any())).thenReturn(false);
    // in @BeforeEach will ensure all slots are available by default.

    // Act
    ChargingSlotsResponse response =
        chargingSlotService.getAvailableSlots(stationId, chargingSpeed, futureDate);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getAvailableSlotMapping()).isNotNull();
    assertThat(response.getAvailableSlotMapping())
        .hasSize(96); // 2 physical slots * 48 half-hour slots
    assertThat(response.getPricePerKWh()).isEqualTo(0.15F);

    Set<Long> uniqueSlotIds =
        response.getAvailableSlotMapping().stream()
            .map(ChargingSlotsResponse.SlotAvailability::getSlotId)
            .collect(Collectors.toSet());
    assertThat(uniqueSlotIds)
        .hasSize(2)
        .containsExactlyInAnyOrder(slot1_Slow.getSlotId(), slot2_Slow.getSlotId());

    // Verify interactions
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingSlotRepository, times(1))
        .findByStationAndChargingSpeed(stationSlow, chargingSpeed);
    // Booking repository still checked for all potential slots
    verify(bookingRepository, times(96))
        .existsBySlotAndStartTime(any(ChargingSlot.class), any(LocalDateTime.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-68")
  void createChargingSlot_WhenValidRequest_ReturnsCreatedSlot()
      throws ResourceNotFoundException, InvalidRequestException {
    // Arrange
    Long stationId = stationSlow.getStationId();
    ChargingSlotRequest request = new ChargingSlotRequest();
    request.setChargingSpeed("Slow");
    request.setPricePerKWh(0.20F);

    // Mock the station repository to return the station
    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(stationSlow));

    // Mock the save method to return the slot being saved
    when(chargingSlotRepository.save(any(ChargingSlot.class)))
        .thenAnswer(invocation -> invocation.getArgument(0)); // Return the saved slot

    // Act
    ChargingSlot createdSlot = chargingSlotService.addChargingSlotToStation(stationId, request);

    // Assert
    assertThat(createdSlot).isNotNull();
    assertThat(createdSlot.getChargingSpeed()).isEqualTo("Slow");
    assertThat(createdSlot.getPricePerKWh()).isEqualTo(0.20F);
    assertThat(createdSlot.getPower()).isEqualTo(10);
    assertThat(createdSlot.getStation()).isEqualTo(stationSlow);

    // Verify interactions
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingSlotRepository, times(1)).save(any(ChargingSlot.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-68")
  void createChargingSlot_WhenInvalidChargingSpeed_ThrowsInvalidRequestException() {
    // Arrange
    Long stationId = stationSlow.getStationId();
    ChargingSlotRequest request = new ChargingSlotRequest();
    request.setChargingSpeed("UltraFast"); // Invalid speed
    request.setPricePerKWh(0.20F);

    // Mock the station repository to return the station
    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(stationSlow));

    // Act & Assert
    assertThatThrownBy(() -> chargingSlotService.addChargingSlotToStation(stationId, request))
        .isInstanceOf(InvalidRequestException.class)
        .hasMessageContaining(
            "Invalid charging speed: UltraFast. Valid options are Slow, Medium, Fast.");

    // Verify interactions
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingSlotRepository, never()).save(any(ChargingSlot.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-68")
  void createChargingSlot_WhenStationNotFound_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidStationId = 9999L; // Non-existent station ID
    ChargingSlotRequest request = new ChargingSlotRequest();
    request.setChargingSpeed("Slow");
    request.setPricePerKWh(0.20F);

    // Mock the station repository to return empty
    when(chargingStationRepository.findById(invalidStationId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(
            () -> chargingSlotService.addChargingSlotToStation(invalidStationId, request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Charging station not found with id: " + invalidStationId);

    // Verify interactions
    verify(chargingStationRepository, times(1)).findById(invalidStationId);
    verify(chargingSlotRepository, never()).save(any(ChargingSlot.class));
  }
}
