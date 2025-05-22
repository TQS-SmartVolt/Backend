package ua.tqs.smartvolt.smartvolt.services;

import java.util.ArrayList;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

@Service
public class ChargingStationService {
  private final ChargingStationRepository chargingStationRepository;
  private final StationOperatorRepository stationOperatorRepository;

  public ChargingStationService(
      ChargingStationRepository chargingStationRepository,
      StationOperatorRepository stationOperatorRepository) {
    this.chargingStationRepository = chargingStationRepository;
    this.stationOperatorRepository = stationOperatorRepository;
  }

  public ChargingStation createChargingStation(ChargingStationRequest request) {
    // StationOperator stationOperator =
    //     stationOperatorRepository
    //         .findById(request.getOperatorId())
    //         .orElseThrow(() -> new RuntimeException("Station operator not found"));

    // ============== REMOVE THIS ==================
    StationOperator stationOperator = new StationOperator();
    stationOperator.setName("Test Operator");
    stationOperator.setEmail("operator@example.com");
    stationOperator.setPassword("password");
    stationOperatorRepository.save(stationOperator);
    // ============== REMOVE THIS ==================

    ChargingStation chargingStation = new ChargingStation();
    chargingStation.setName(request.getName());
    chargingStation.setLocation(request.getLocation());
    chargingStation.setOperator(stationOperator);
    chargingStation.setAvailability(true);
    chargingStation.setSlots(new ArrayList<>());
    return chargingStationRepository.save(chargingStation);
  }
}
