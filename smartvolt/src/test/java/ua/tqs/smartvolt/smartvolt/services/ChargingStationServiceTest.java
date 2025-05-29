package ua.tqs.smartvolt.smartvolt.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

@ExtendWith(MockitoExtension.class)
public class ChargingStationServiceTest {

  @Mock private ChargingStationRepository chargingStationRepository;
  @Mock private StationOperatorRepository stationOperatorRepository;

  private ChargingStationService chargingStationService;

  private StationOperator stationOperator;
  private List<ChargingStation> chargingStations;
  private Long OPERATOR_ID = stationOperator.getUserId();

  @BeforeEach
  void setUp() {
    chargingStationService =
        new ChargingStationService(chargingStationRepository, stationOperatorRepository);

    stationOperator = new StationOperator();
    stationOperator.setUserId(OPERATOR_ID);

    chargingStations =
        List.of(
            new ChargingStation("Station 1", 12.34, 56.78, "Address 1", true, stationOperator),
            new ChargingStation("Station 2", 23.45, 67.89, "Address 2", true, stationOperator));
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
  void createChargingStation_WhenOperatorExists_CreatesChargingStation() {
    // Arrange
    Long operatorId = OPERATOR_ID;
    when(stationOperatorRepository.findById(operatorId)).thenReturn(Optional.of(stationOperator));
    when(chargingStationRepository.save(chargingStations.get(0)))
        .thenReturn(chargingStations.get(0));
    ChargingStationRequest chargingStation =
        new ChargingStationRequest("Station 1", 12.34, 56.78, stationOperator.getUserId());

    // Act
    ChargingStation result = chargingStationService.createChargingStation(chargingStation);

    // Assert
    assertThat(result).isNotNull();
    assertThat(result.getName()).isEqualTo("Station 1");

    verify(chargingStationRepository, times(1)).save(chargingStations.get(0));
  }

  // TODO: Uncomment the test, this test only works when login and register are
  // implemented
  // @Test
  // @Tag("UnitTest")
  // @Requirement("SV-34")
  // void
  // createChargingStation_WhenOperatorDoesNotExist_ThrowsResourceNotFoundException()
  // throws Exception {
  // Long invalidOperatorId = 999L;
  // when(stationOperatorRepository.findById(invalidOperatorId)).thenReturn(Optional.empty());

  // ChargingStationRequest request =
  // new ChargingStationRequest("Station 1", 12.34, 56.78, invalidOperatorId);

  // Assertions.assertThatThrownBy(() ->
  // chargingStationService.createChargingStation(request))
  // .isInstanceOf(ResourceNotFoundException.class)
  // .hasMessageContaining("Operator not found with id: " + invalidOperatorId);
  // verify(chargingStationRepository, times(0)).save(chargingStations.get(0));
  // }
}
