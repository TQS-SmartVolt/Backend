package ua.tqs.smartvolt.smartvolt.services;

import org.springframework.stereotype.Service;
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
}
