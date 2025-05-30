package ua.tqs.smartvolt.smartvolt.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationResponse;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationsResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;

@Service
public class ChargingStationService {
  private final ChargingStationRepository chargingStationRepository;
  private final StationOperatorRepository stationOperatorRepository;
  private final ChargingSlotRepository chargingSlotRepository;

  public ChargingStationService(
      ChargingStationRepository chargingStationRepository,
      StationOperatorRepository stationOperatorRepository,
      ChargingSlotRepository chargingSlotRepository) {
    this.chargingStationRepository = chargingStationRepository;
    this.stationOperatorRepository = stationOperatorRepository;
    this.chargingSlotRepository = chargingSlotRepository;
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
    chargingStation.setAddress(request.getAddress());

    chargingStation.setOperator(stationOperator);
    chargingStation.setAvailability(true);
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

  public ChargingStationsResponse getChargingStationsByChargingSpeed(String[] chargingSpeeds)
      throws ResourceNotFoundException {

    if (chargingSpeeds == null || chargingSpeeds.length == 0) {
      throw new ResourceNotFoundException("No charging speeds provided");
    }

    // Use a Set to avoid duplicate stations
    Set<ChargingStation> stationsSet = new HashSet<>();

    for (String speed : chargingSpeeds) {
      List<ChargingStation> matchingStations =
          chargingSlotRepository.findStationsByChargingSpeed(speed);
      stationsSet.addAll(matchingStations);
    }

    if (stationsSet.isEmpty()) {
      throw new ResourceNotFoundException(
          "No charging stations found for the given speeds: " + Arrays.toString(chargingSpeeds));
    }

    List<ChargingStationResponse> stationResponses = new ArrayList<>();

    for (ChargingStation station : stationsSet) {
      // Collect distinct charging speeds at this station
      Set<String> speedSet = new HashSet<>();
      List<ChargingSlot> slots = chargingSlotRepository.findByStation(station);
      for (ChargingSlot slot : slots) {
        speedSet.add(slot.getChargingSpeed());
      }

      List<String> distinctSpeeds = new ArrayList<>(speedSet);

      ChargingStationResponse response =
          new ChargingStationResponse(
              station.getStationId(),
              station.getName(),
              station.getAddress(),
              station.getLatitude(),
              station.getLongitude(),
              distinctSpeeds);

      stationResponses.add(response);
    }

    return new ChargingStationsResponse(stationResponses);
  }

  public ChargingStation updateChargingStationStatus(Long stationId, boolean activate)
      throws ResourceNotFoundException {
    ChargingStation chargingStation =
        chargingStationRepository
            .findById(stationId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Charging station not found with id: " + stationId));

    chargingStation.setAvailability(activate);
    return chargingStationRepository.save(chargingStation);
  }
}
