package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.OperatorSessionsResponse;
import ua.tqs.smartvolt.smartvolt.services.ChargingSessionService;

@RestController
@RequestMapping("/api/v1/sessions")
public class ChargingSessionController {
  private ChargingSessionService chargingSessionService;

  public ChargingSessionController(ChargingSessionService chargingSessionService) {
    this.chargingSessionService = chargingSessionService;
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  public OperatorSessionsResponse getOperatorSessions() {
    return chargingSessionService.getSessions();
  }
}
