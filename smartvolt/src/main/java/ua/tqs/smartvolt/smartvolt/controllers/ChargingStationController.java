package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.services.ChargingStationService;

@RestController
@RequestMapping("/api/v1/stations")
public class ChargingStationController {
  private final ChargingStationService chargingStationService;

  public ChargingStationController(ChargingStationService chargingStationService) {
    this.chargingStationService = chargingStationService;
  }

  @PostMapping
  public ChargingStation createChargingStation(@RequestBody ChargingStationRequest request) {
    return chargingStationService.createChargingStation(request);
  }
}
