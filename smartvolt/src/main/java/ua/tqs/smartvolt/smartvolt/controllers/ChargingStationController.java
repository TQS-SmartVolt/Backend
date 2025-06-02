package ua.tqs.smartvolt.smartvolt.controllers;

import java.time.LocalDate;
import java.util.List;
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
  public ChargingStation createChargingStation(@RequestBody ChargingStationRequest request)
      throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long currentId = Long.parseLong(authentication.getName());
    return chargingStationService.createChargingStation(request, currentId);
  }

  @GetMapping
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  public List<ChargingStationWithSlots> getAllChargingStations() throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long operatorId = Long.parseLong(authentication.getName());
    return chargingStationService.getAllChargingStations(operatorId);
  }

  @GetMapping("{stationId}/slots")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public ChargingSlotsResponse getChargingSlotsByStationId(
      @PathVariable Long stationId,
      @RequestParam String chargingSpeed,
      @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date)
      throws ResourceNotFoundException {
    return chargingSlotService.getAvailableSlots(stationId, chargingSpeed, date);
  }

  @PostMapping("/{stationId}/slots")
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  public ChargingSlot addChargingSlotToStation(
      @PathVariable Long stationId, @RequestBody ChargingSlotRequest request)
      throws ResourceNotFoundException, InvalidRequestException {
    return chargingSlotService.addChargingSlotToStation(stationId, request);
  }

  @GetMapping("/map")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public ChargingStationsResponse getChargingStationsByChargingSpeed(
      @RequestParam String[] chargingSpeeds) throws ResourceNotFoundException {
    return chargingStationService.getChargingStationsByChargingSpeed(chargingSpeeds);
  }

  @PatchMapping("/{stationId}/status")
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  public ChargingStation updateChargingStationStatus(
      @PathVariable Long stationId, @RequestParam boolean activate)
      throws ResourceNotFoundException {
    return chargingStationService.updateChargingStationStatus(stationId, activate);
  }
}
