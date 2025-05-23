package ua.tqs.smartvolt.smartvolt.unit_tests;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.util.List;
import java.util.Optional;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;
import ua.tqs.smartvolt.smartvolt.services.ChargingStationService;

@ExtendWith(MockitoExtension.class)
public class ChargingStationServiceTest {

  @Mock private ChargingStationRepository chargingStationRepository;
  @Mock private StationOperatorRepository stationOperatorRepository;

  @InjectMocks private ChargingStationService chargingStationService;

  private StationOperator stationOperator;
  private List<ChargingStation> chargingStations;

  @BeforeEach
  void setUp() {
    stationOperator = new StationOperator();
    stationOperator.setUserId(1L); // Set a mock user ID

    chargingStations =
        List.of(
            new ChargingStation("Station 1", 12.34, 56.78, "Address 1", true, stationOperator),
            new ChargingStation("Station 2", 23.45, 67.89, "Address 2", true, stationOperator));
  }

  // ================== getAllChargingStations() Tests ==================
  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void getAllChargingStations_WhenOperatorExists_ReturnsListOfChargingStations() throws Exception {
    when(stationOperatorRepository.findById(stationOperator.getUserId()))
        .thenReturn(Optional.of(stationOperator));
    when(chargingStationRepository.findByOperator(stationOperator)).thenReturn(chargingStations);

    Long operatorId = stationOperator.getUserId();
    System.out.println("Operator ID: " + operatorId);
    var result = chargingStationService.getAllChargingStations(stationOperator.getUserId());

    Assertions.assertThat(result).isNotNull().hasSize(2);

    verify(chargingStationRepository, times(1)).findByOperator(stationOperator);
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void getAllChargingStations_WhenOperatorDoesNotExist_ThrowsResourceNotFoundException()
      throws Exception {
    Long invalidOperatorId = 999L;
    when(stationOperatorRepository.findById(invalidOperatorId)).thenReturn(Optional.empty());

    Assertions.assertThatThrownBy(
            () -> chargingStationService.getAllChargingStations(invalidOperatorId))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Operator not found with id: " + invalidOperatorId);

    verify(chargingStationRepository, times(0)).findByOperator(stationOperator);
  }

  // ================== createChargingStation() Tests ==================
  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void createChargingStation_WhenOperatorExists_CreatesChargingStation() throws Exception {
    when(stationOperatorRepository.findById(stationOperator.getUserId()))
        .thenReturn(Optional.of(stationOperator));
    when(chargingStationRepository.save(chargingStations.get(0)))
        .thenReturn(chargingStations.get(0));

    ChargingStationRequest request =
        new ChargingStationRequest("Station 1", 12.34, 56.78, stationOperator.getUserId());

    ChargingStation result = chargingStationService.createChargingStation(request);

    Assertions.assertThat(result).isNotNull();
    Assertions.assertThat(result.getName()).isEqualTo("Station 1");

    verify(chargingStationRepository, times(1)).save(chargingStations.get(0));
  }

  @Test
  @Tag("UnitTest")
  @Requirement("SV-34")
  void createChargingStation_WhenOperatorDoesNotExist_ThrowsResourceNotFoundException()
      throws Exception {
    Long invalidOperatorId = 999L;
    when(stationOperatorRepository.findById(invalidOperatorId)).thenReturn(Optional.empty());

    ChargingStationRequest request =
        new ChargingStationRequest("Station 1", 12.34, 56.78, invalidOperatorId);

    Assertions.assertThatThrownBy(() -> chargingStationService.createChargingStation(request))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessageContaining("Operator not found with id: " + invalidOperatorId);
    verify(chargingStationRepository, times(0)).save(chargingStations.get(0));
  }
}
