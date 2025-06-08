package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.ChargingHistoryResponse;
import ua.tqs.smartvolt.smartvolt.dto.ConsumptionResponse;
import ua.tqs.smartvolt.smartvolt.dto.SpendingResponse;
import ua.tqs.smartvolt.smartvolt.dto.UserInfoResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.services.EvDriverService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

  private static final Logger logger = LogManager.getLogger(UserController.class);
  private final EvDriverService evDriverService;

  public UserController(EvDriverService evDriverService) {
    this.evDriverService = evDriverService;
  }

  @GetMapping("/bookings")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get user bookings",
      description = "Retrieves the list of charging history for the authenticated EV driver.")
  public List<ChargingHistoryResponse> getUserBookings() throws ResourceNotFoundException {
    logger.info("Retrieving bookings for the authenticated EV driver");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverBookings(userId);
  }

  @GetMapping("/consumption")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get user consumption",
      description = "Retrieves the total energy consumption for the authenticated EV driver.")
  public ConsumptionResponse getUserConsumption() throws ResourceNotFoundException {
    logger.info("Retrieving energy consumption for the authenticated EV driver");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverConsumption(userId);
  }

  @GetMapping("/spending")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get user spending",
      description = "Retrieves the total spending for the authenticated EV driver.")
  public SpendingResponse getUserSpending() throws ResourceNotFoundException {
    logger.info("Retrieving spending for the authenticated EV driver");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverSpending(userId);
  }

  @GetMapping()
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get user information",
      description = "Retrieves detailed information about the authenticated EV driver.")
  public UserInfoResponse getUserInfo() throws ResourceNotFoundException {
    logger.info("Retrieving user information for the authenticated EV driver");
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverInfo(userId);
  }
}
