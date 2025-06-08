package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.OperatorSessionsResponse;
import ua.tqs.smartvolt.smartvolt.services.ChargingSessionService;

@RestController
@RequestMapping("/api/v1/sessions")
public class ChargingSessionController {

  private static final Logger logger = LogManager.getLogger(ChargingSessionController.class);

  private ChargingSessionService chargingSessionService;

  public ChargingSessionController(ChargingSessionService chargingSessionService) {
    this.chargingSessionService = chargingSessionService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  @Operation(
      summary = "Get charging sessions statistics for the operator",
      description =
          "Retrieves the total number of charging sessions, average sessions per month, and a breakdown of sessions by month for the operator.")
  public OperatorSessionsResponse getOperatorSessions() {
    logger.info("Retrieving charging sessions statistics for the operator");
    return chargingSessionService.getSessions();
  }
}
