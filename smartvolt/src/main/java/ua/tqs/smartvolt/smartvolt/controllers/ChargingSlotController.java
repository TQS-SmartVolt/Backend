package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
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
  @Operation(
      summary = "Get details of a charging slot by its ID",
      description = "Retrieves detailed information about a specific charging slot using its ID.")
  public ChargingSlotResponse getSlotDetailsById(@PathVariable Long slotId) {
    return chargingSlotService.getSlotDetailsById(slotId);
  }
}
