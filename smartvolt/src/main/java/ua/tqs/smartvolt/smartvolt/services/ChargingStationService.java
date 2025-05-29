package ua.tqs.smartvolt.smartvolt.services;

import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
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

  public ChargingStation createChargingStation(ChargingStationRequest request, Long operatorId)
      throws ResourceNotFoundException {

    StationOperator stationOperator =
        stationOperatorRepository
            .findById(operatorId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Operator not found with id: " + operatorId));

    ChargingStation chargingStation = new ChargingStation();
    chargingStation.setName(request.getName());
    chargingStation.setLatitude(request.getLatitude());
    chargingStation.setLongitude(request.getLongitude());

    // TODO: Get address from a geocoding service
    String address = "address";
    chargingStation.setAddress(address);

    chargingStation.setOperator(stationOperator);
    chargingStation.setAvailability(true);
    chargingStation.setSlots(new ArrayList<>());
    return chargingStationRepository.save(chargingStation);
  }

  public List<ChargingStation> getAllChargingStations(Long operatorId)
      throws ResourceNotFoundException {
    StationOperator operator =
        stationOperatorRepository
            .findById(operatorId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Operator not found with id: " + operatorId));
    return chargingStationRepository.findByOperator(operator);
  }
}
