package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotResponse;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.services.ChargingSlotService;

@RestController
@RequestMapping("/api/v1/slots")
public class ChargingSlotController {
  
  private final ChargingSlotService chargingSlotService;

  public ChargingSlotController(ChargingSlotService chargingSlotService) {
    this.chargingSlotService = chargingSlotService;
  }

  @GetMapping("/")
  public ChargingSlotResponse getSlotById(@PathVariable Long slotId) {
    ChargingSlot slot = chargingSlotService.getSlotById(slotId);
    if (slot == null) {
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found");
    }

    ChargingSlotResponse response = new ChargingSlotResponse();
    double power = slot.getPower();
    double pricePerKWh = slot.getPricePerKWh();
    ChargingStation station = slot.getStation();
    Long stationId = station.getStationId();
    response.setPower(power);
    response.setPricePerKWh(pricePerKWh);
    response.setStationId(stationId);

  }
}