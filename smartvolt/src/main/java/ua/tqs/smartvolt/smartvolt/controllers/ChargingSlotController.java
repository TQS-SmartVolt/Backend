package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PathVariable;

import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotResponse;
import ua.tqs.smartvolt.smartvolt.services.ChargingSlotService;

@RestController
@RequestMapping("/api/v1/slots")
public class ChargingSlotController {
  
  private final ChargingSlotService chargingSlotService;

  public ChargingSlotController(ChargingSlotService chargingSlotService) {
    this.chargingSlotService = chargingSlotService;
  }

  @GetMapping("/{slotId}")
  public ChargingSlotResponse getSlotDetailsById(@PathVariable Long slotId) {
    return chargingSlotService.getSlotDetailsById(slotId);
  }
}