package ua.tqs.smartvolt.smartvolt.controllers;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotsResponse;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.services.ChargingStationService;
import ua.tqs.smartvolt.smartvolt.services.ChargingSlotService;


@RestController
@RequestMapping("/api/v1/stations")
public class ChargingStationController {
  private final ChargingStationService chargingStationService;
  private final ChargingSlotService chargingSlotService;

  public ChargingStationController(ChargingStationService chargingStationService, ChargingSlotService chargingSlotService) {
    this.chargingStationService = chargingStationService;
    this.chargingSlotService = chargingSlotService;
  }

  @PostMapping
  public ChargingStation createChargingStation(@RequestBody ChargingStationRequest request)
      throws Exception {
    return chargingStationService.createChargingStation(request);
  }

  @GetMapping
  public List<ChargingStation> getAllChargingStations(@RequestParam Long operatorId)
      throws Exception {
    return chargingStationService.getAllChargingStations(operatorId);
  }

  @GetMapping("{stationId}/slots")
  public ChargingSlotsResponse getChargingSlotsByStationId( 
    @PathVariable Long stationId,
    @RequestParam String chargingSpeed,
    @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
  ) {
    return chargingSlotService.getAvailableSlots(stationId, chargingSpeed, date);
  }
}
