package ua.tqs.smartvolt.smartvolt.controllers;

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

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
  private final EvDriverService evDriverService;

  public UserController(EvDriverService evDriverService) {
    this.evDriverService = evDriverService;
  }

  @GetMapping("/bookings")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public List<ChargingHistoryResponse> getUserBookings() throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverBookings(userId);
  }

  @GetMapping("/consumption")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public ConsumptionResponse getUserConsumption() throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverConsumption(userId);
  }

  @GetMapping("/spending")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public SpendingResponse getUserSpending() throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverSpending(userId);
  }

  @GetMapping()
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public UserInfoResponse getUserInfo() throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long userId = Long.parseLong(authentication.getName());
    return evDriverService.getEvDriverInfo(userId);
  }
}
