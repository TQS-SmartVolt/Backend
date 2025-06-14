package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotResponse;
import ua.tqs.smartvolt.smartvolt.services.ChargingSlotService;

@RestController
@RequestMapping("/api/v1/slots")
public class ChargingSlotController {

  private static final Logger logger = LogManager.getLogger(ChargingSlotController.class);

  private final ChargingSlotService chargingSlotService;

  public ChargingSlotController(ChargingSlotService chargingSlotService) {
    this.chargingSlotService = chargingSlotService;
  }

  @GetMapping("/{slotId}")
  @Operation(
      summary = "Get details of a charging slot by its ID",
      description = "Retrieves detailed information about a specific charging slot using its ID.")
  public ChargingSlotResponse getSlotDetailsById(@PathVariable Long slotId) {
    logger.info("Retrieving details for charging slot with ID: {}", slotId);
    return chargingSlotService.getSlotDetailsById(slotId);
  }
}
