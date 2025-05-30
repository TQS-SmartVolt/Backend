package ua.tqs.smartvolt.smartvolt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;

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
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationsResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

@ExtendWith(MockitoExtension.class)
public class ChargingStationServiceTest {

  @Mock private ChargingStationRepository chargingStationRepository;
  @Mock private StationOperatorRepository stationOperatorRepository;
  @Mock private ChargingSlotRepository chargingSlotRepository;

  private ChargingStationService chargingStationService;

  private StationOperator stationOperator;
  private List<ChargingStation> chargingStations;
  private Long OPERATOR_ID;

  private ChargingStation stationA;
  private ChargingStation stationB;
  private ChargingSlot slotA_Slow;
  private ChargingSlot slotA_Medium;
  private ChargingSlot slotB_Fast;

  @BeforeEach
  void setUp() {
    chargingStationService =
        new ChargingStationService(
            chargingStationRepository, stationOperatorRepository, chargingSlotRepository);

    OPERATOR_ID = 1L;
    stationOperator = new StationOperator();
    stationOperator.setUserId(OPERATOR_ID);

    chargingStations =
        List.of(
            new ChargingStation("Station 1", 12.34, 56.78, "Address 1", true, stationOperator),
            new ChargingStation("Station 2", 23.45, 67.89, "Address 2", true, stationOperator));
    
    // Setup for getChargingStationsByChargingSpeed tests
    stationA = new ChargingStation("Station A", 40.0, -8.0, "Address A", true, stationOperator);
    stationB = new ChargingStation("Station B", 41.0, -9.0, "Address B", true, stationOperator);

    stationA.setStationId(1L);
    stationB.setStationId(2L);

    slotA_Slow = new ChargingSlot();
    slotA_Slow.setChargingSpeed("Slow");
    slotA_Slow.setStation(stationA);

    slotA_Medium = new ChargingSlot();
    slotA_Medium.setChargingSpeed("Medium");
    slotA_Medium.setStation(stationA);

    slotB_Fast = new ChargingSlot();
    slotB_Fast.setChargingSpeed("Fast");
    slotB_Fast.setStation(stationB);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void getAllChargingStations_WhenOperatorExists_ReturnsListOfChargingStations()
      throws ResourceNotFoundException {
    // Arrange
    Long operatorId = OPERATOR_ID;
    when(stationOperatorRepository.findById(operatorId)).thenReturn(Optional.of(stationOperator));
    when(chargingStationRepository.findByOperator(stationOperator)).thenReturn(chargingStations);

    // Act
    List<ChargingStation> result = chargingStationService.getAllChargingStations(operatorId);

    // Assert
    assertThat(result)
        .isNotNull()
        .isInstanceOf(List.class)
        .hasSize(2)
        .extracting("name")
        .containsExactlyInAnyOrder("Station 1", "Station 2");
    verify(chargingStationRepository, times(1)).findByOperator(stationOperator);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void getAllChargingStations_WhenOperatorDoesNotExist_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidOperatorId = 999L;
    when(stationOperatorRepository.findById(invalidOperatorId)).thenReturn(Optional.empty());

    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> chargingStationService.getAllChargingStations(invalidOperatorId));

    String expectedMessage = "Operator not found with id: " + invalidOperatorId;
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);
    verify(chargingStationRepository, times(0)).findByOperator(stationOperator);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void createChargingStation_WhenOperatorExists_CreatesChargingStation()
      throws ResourceNotFoundException {
    // Arrange
    Long operatorId = OPERATOR_ID;
    when(stationOperatorRepository.findById(operatorId)).thenReturn(Optional.of(stationOperator));
    when(chargingStationRepository.save(chargingStations.get(0)))
        .thenReturn(chargingStations.get(0));
    ChargingStationRequest chargingStation =
        new ChargingStationRequest("Station 1", "Thrid Street", 12.34, 56.78);

    // Act
    ChargingStation result =
        chargingStationService.createChargingStation(chargingStation, stationOperator.getUserId());

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Station 1");

    verify(chargingStationRepository, times(1)).save(chargingStations.get(0));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void createChargingStation_WhenOperatorDoesNotExist_ThrowsResourceNotFoundException()
      throws Exception {
    // Arrange
    Long invalidOperatorId = 999L;
    when(stationOperatorRepository.findById(invalidOperatorId)).thenReturn(Optional.empty());

    // Act & Assert
    ChargingStationRequest request =
        new ChargingStationRequest("Station 1", "Second Stree", 12.34, 56.78);
    assertThatThrownBy(
            () -> chargingStationService.createChargingStation(request, invalidOperatorId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Operator not found with id: " + invalidOperatorId);
    verify(chargingStationRepository, times(0)).save(chargingStations.get(0));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-35")
  void updateChargingStationStatus_WhenStationExists_UpdatesAvailability()
      throws ResourceNotFoundException {
    // Arrange
    Long stationId = 1L;
    boolean newAvailability = false;

    ChargingStation existingStation = chargingStations.get(0);
    existingStation.setStationId(stationId);

    when(chargingStationRepository.findById(stationId)).thenReturn(Optional.of(existingStation));
    when(chargingStationRepository.save(existingStation)).thenReturn(existingStation);

    // Act
    ChargingStation updatedStation =
        chargingStationService.updateChargingStationStatus(stationId, newAvailability);

    // Assert
    assertThat(updatedStation).isNotNull();
    assertThat(updatedStation.isAvailability()).isEqualTo(newAvailability);
    verify(chargingStationRepository, times(1)).findById(stationId);
    verify(chargingStationRepository, times(1)).save(existingStation);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-35")
  void updateChargingStationStatus_WhenStationDoesNotExist_ThrowsResourceNotFoundException() {
    // Arrange
    Long invalidStationId = 999L;
    when(chargingStationRepository.findById(invalidStationId)).thenReturn(Optional.empty());

    // Act & Assert
    assertThatThrownBy(
            () -> chargingStationService.updateChargingStationStatus(invalidStationId, true))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Charging station not found with id: " + invalidStationId);
    verify(chargingStationRepository, times(1)).findById(invalidStationId);
    verify(chargingStationRepository, times(0)).save(new ChargingStation());
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-19") // Assign a new requirement ID
  void getChargingStationsByChargingSpeed_WhenStationsFound_ReturnsChargingStationsResponse()
      throws ResourceNotFoundException {
    // Arrange
    String[] speeds = {"Slow", "Fast"};

    // Mock findStationsByChargingSpeed for each speed
    when(chargingSlotRepository.findStationsByChargingSpeed("Slow"))
        .thenReturn(Arrays.asList(stationA)); // StationA has Slow
    when(chargingSlotRepository.findStationsByChargingSpeed("Fast"))
        .thenReturn(Arrays.asList(stationB)); // StationB has Fast

    // Mock findByStation for each found station to get their distinct speeds
    when(chargingSlotRepository.findByStation(stationA))
        .thenReturn(Arrays.asList(slotA_Slow, slotA_Medium));
    when(chargingSlotRepository.findByStation(stationB))
        .thenReturn(Arrays.asList(slotB_Fast));

    // Act
    ChargingStationsResponse response =
        chargingStationService.getChargingStationsByChargingSpeed(speeds);

    // Assert
    assertThat(response).isNotNull();
    assertThat(response.getStations()).hasSize(2);

    // Verify StationA details
    assertThat(response.getStations().get(0).getStationId()).isEqualTo(stationA.getStationId());
    assertThat(response.getStations().get(0).getName()).isEqualTo(stationA.getName());
    assertThat(response.getStations().get(0).getStationSlotChargingSpeeds()).containsExactlyInAnyOrder("Slow", "Medium");

    // Verify StationB details
    assertThat(response.getStations().get(1).getStationId()).isEqualTo(stationB.getStationId());
    assertThat(response.getStations().get(1).getName()).isEqualTo(stationB.getName());
    assertThat(response.getStations().get(1).getStationSlotChargingSpeeds()).containsExactlyInAnyOrder("Fast");

    // Verify repository calls
    verify(chargingSlotRepository, times(1)).findStationsByChargingSpeed("Slow");
    verify(chargingSlotRepository, times(1)).findStationsByChargingSpeed("Fast");
    verify(chargingSlotRepository, times(1)).findByStation(stationA);
    verify(chargingSlotRepository, times(1)).findByStation(stationB);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_WhenNoStationsFound_ThrowsResourceNotFoundException() {
    // Arrange
    String[] speeds = {"Slow", "Fast"};
    when(chargingSlotRepository.findStationsByChargingSpeed(anyString()))
        .thenReturn(Collections.emptyList()); // Mock both calls to return empty list

    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> chargingStationService.getChargingStationsByChargingSpeed(speeds));

    String expectedMessage = "No charging stations found for the given speeds: " + Arrays.toString(speeds);
    assertThat(exception.getMessage()).isEqualTo(expectedMessage);

    // Verify findStationsByChargingSpeed was called for each speed, but findByStation was never called
    verify(chargingSlotRepository, times(1)).findStationsByChargingSpeed("Slow");
    verify(chargingSlotRepository, times(1)).findStationsByChargingSpeed("Fast");
    verify(chargingSlotRepository, never()).findByStation(any(ChargingStation.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_WhenInputSpeedsAreNull_ThrowsResourceNotFoundException() {
    // Arrange
    String[] speeds = null;

    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> chargingStationService.getChargingStationsByChargingSpeed(speeds));

    assertThat(exception.getMessage()).isEqualTo("No charging speeds provided");

    // Verify no repository methods were called as the check is done before
    verify(chargingSlotRepository, never()).findStationsByChargingSpeed(anyString());
    verify(chargingSlotRepository, never()).findByStation(any(ChargingStation.class));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-19")
  void getChargingStationsByChargingSpeed_WhenInputSpeedsAreEmpty_ThrowsResourceNotFoundException() {
    // Arrange
    String[] speeds = {};

    // Act & Assert
    ResourceNotFoundException exception =
        assertThrows(
            ResourceNotFoundException.class,
            () -> chargingStationService.getChargingStationsByChargingSpeed(speeds));

    assertThat(exception.getMessage()).isEqualTo("No charging speeds provided");

    // Verify no repository methods were called as the check is done before
    verify(chargingSlotRepository, never()).findStationsByChargingSpeed(anyString());
    verify(chargingSlotRepository, never()).findByStation(any(ChargingStation.class));
  }

}
