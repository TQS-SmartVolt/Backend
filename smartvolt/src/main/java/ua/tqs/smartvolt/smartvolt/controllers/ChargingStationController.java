package ua.tqs.smartvolt.smartvolt.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
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
  @ResponseStatus(HttpStatus.CREATED)
  public ChargingStation createChargingStation(@RequestBody ChargingStationRequest request)
      throws ResourceNotFoundException {
    return chargingStationService.createChargingStation(request);
  }

  @GetMapping
  public List<ChargingStation> getAllChargingStations(@RequestParam Long operatorId)
      throws ResourceNotFoundException {
    return chargingStationService.getAllChargingStations(operatorId);
  }

  @PatchMapping("/{stationId}/status")
  public ChargingStation updateChargingStationStatus(
      @PathVariable Long stationId, @RequestParam boolean activate)
      throws ResourceNotFoundException {
    return chargingStationService.updateChargingStationStatus(stationId, activate);
  }
}
