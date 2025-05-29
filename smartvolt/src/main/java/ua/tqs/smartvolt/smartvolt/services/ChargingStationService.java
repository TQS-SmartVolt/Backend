package ua.tqs.smartvolt.smartvolt.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationsResponse;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationResponse;
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
      ChargingSlotRepository chargingSlotRepository
      ) {
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

  public ChargingStationsResponse getChargingStationsByChargingSpeed(String[] chargingSpeeds) throws ResourceNotFoundException {

    // Use a Set to avoid duplicate stations
    Set<ChargingStation> stationsSet = new HashSet<>();

    for (String speed : chargingSpeeds) {
        List<ChargingStation> matchingStations = chargingSlotRepository.findStationsByChargingSpeed(speed);
        stationsSet.addAll(matchingStations);
    }

    if (stationsSet.isEmpty()) {
        throw new ResourceNotFoundException("No charging stations found for the given speeds: " + Arrays.toString(chargingSpeeds));
    }

    List<ChargingStationResponse> stationResponses = new ArrayList<>();

    for (ChargingStation station : stationsSet) {
        // Collect distinct charging speeds at this station
        Set<String> speedSet = new HashSet<>();
        for (ChargingSlot slot : station.getSlots()) {
            speedSet.add(slot.getChargingSpeed());
        }

        List<String> distinctSpeeds = new ArrayList<>(speedSet);

        ChargingStationResponse response = new ChargingStationResponse(
            station.getStationId(),
            station.getName(),
            station.getAddress(),
            station.getLatitude(),
            station.getLongitude(),
            distinctSpeeds
        );

        stationResponses.add(response);
    }

    return new ChargingStationsResponse(stationResponses);
  }



  @PostConstruct
    public void init() {
        populateTestStations(); // Called only once on app startup
  }

  // TODO: Remove this method after testing
  private void populateTestStations() {
    if (!chargingStationRepository.findAll().isEmpty()) return; // avoid duplicates on restart

    StationOperator operator = stationOperatorRepository.findByEmail("test@example.com")
        .orElseGet(() -> {
            StationOperator op = new StationOperator();
            op.setName("Test Operator");
            op.setEmail("test@example.com");
            op.setPassword("pass");
            return stationOperatorRepository.save(op);
        });

    ChargingStation station1 = new ChargingStation("Station 1",  40.6343605, -8.647361, "Rua 1", true, operator);
    ChargingStation station2 = new ChargingStation("Station 2", 40.613605, -8.647361, "Rua 2", true, operator);
    ChargingStation station3 = new ChargingStation("Station 3", 40.623605, -8.647361, "Rua 3", true, operator);

    chargingStationRepository.saveAll(List.of(station1, station2, station3));

    chargingSlotRepository.saveAll(List.of(
        new ChargingSlot(true, 0.20, 10, "Slow", station1),
        new ChargingSlot(true, 0.20, 10, "Slow", station1),
        new ChargingSlot(true, 0.30, 20, "Medium", station1),
        new ChargingSlot(true, 0.30, 20, "Medium", station2),
        new ChargingSlot(true, 0.50, 30, "Fast", station3),
        new ChargingSlot(true, 0.50, 30, "Fast", station3)
    ));
}


}
