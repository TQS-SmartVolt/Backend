package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDate;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotsResponse;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationWithSlots;
import ua.tqs.smartvolt.smartvolt.dto.ChargingStationsResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.InvalidRequestException;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.services.ChargingSlotService;
import ua.tqs.smartvolt.smartvolt.services.ChargingStationService;

@RestController
@RequestMapping("/api/v1/stations")
public class ChargingStationController {
  private static final Logger logger = LogManager.getLogger(ChargingStationController.class);
  private final ChargingStationService chargingStationService;
  private final ChargingSlotService chargingSlotService;

  public ChargingStationController(
      ChargingStationService chargingStationService, ChargingSlotService chargingSlotService) {
    this.chargingStationService = chargingStationService;
    this.chargingSlotService = chargingSlotService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  @Operation(
      summary = "Create a new charging station",
      description = "Allows a station operator to create a new charging station.")
  public ChargingStation createChargingStation(@RequestBody ChargingStationRequest request)
      throws ResourceNotFoundException {
    logger.info("Creating charging station");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long currentId = Long.parseLong(authentication.getName());
    return chargingStationService.createChargingStation(request, currentId);
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  @Operation(
      summary = "Get all charging stations for the operator",
      description = "Returns a list of all charging stations managed by the operator.")
  public List<ChargingStationWithSlots> getAllChargingStations() throws ResourceNotFoundException {
    logger.info("Retrieving all charging stations for the operator");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long operatorId = Long.parseLong(authentication.getName());
    return chargingStationService.getAllChargingStations(operatorId);
  }

  @GetMapping("/{stationId}/slots")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get available charging slots by station ID",
      description =
          "Returns available charging slots for a specific station based on charging speed and date.")
  public ChargingSlotsResponse getChargingSlotsByStationId(
      @PathVariable Long stationId,
      @RequestParam String chargingSpeed,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
      throws ResourceNotFoundException {
    logger.info("Retrieving available charging slots for station ID: {}", stationId);
    return chargingSlotService.getAvailableSlots(stationId, chargingSpeed, date);
  }

  @PostMapping("/{stationId}/slots")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  @Operation(
      summary = "Add a new charging slot to a station",
      description =
          "Allows a station operator to add a new charging slot to an existing charging station.")
  public ChargingSlot addChargingSlotToStation(
      @PathVariable Long stationId, @RequestBody ChargingSlotRequest request)
      throws ResourceNotFoundException, InvalidRequestException {
    logger.info("Adding charging slot to station ID: {}", stationId);
    return chargingSlotService.addChargingSlotToStation(stationId, request);
  }

  @GetMapping("/map")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get all charging stations with their slots",
      description =
          "Returns all charging stations along with their available slots for EV drivers.")
  public ChargingStationsResponse getChargingStationsByChargingSpeed(
      @RequestParam String[] chargingSpeeds) throws ResourceNotFoundException {
    logger.info("Retrieving charging stations by charging speeds");
    return chargingStationService.getChargingStationsByChargingSpeed(chargingSpeeds);
  }

  @PatchMapping("/{stationId}/status")
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  @Operation(
      summary = "Update charging station status",
      description = "Allows a station operator to activate or deactivate a charging station.")
  public ChargingStation updateChargingStationStatus(
      @PathVariable Long stationId, @RequestParam boolean activate)
      throws ResourceNotFoundException {
    logger.info("Updating status of charging station ID: {} to {}", stationId, activate);
    return chargingStationService.updateChargingStationStatus(stationId, activate);
  }
}
