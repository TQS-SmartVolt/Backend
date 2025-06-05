package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.dto.OperatorEnergyResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.exceptions.SlotAlreadyBookedException;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.services.BookingService;

@RestController
@RequestMapping("/api/v1/bookings")
public class BookingController {

  private final BookingService bookingService;

  public BookingController(BookingService bookingService) {
    this.bookingService = bookingService;
  }

  @PostMapping("/start-payment")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Create a new booking",
      description = "Allows an EV driver to create a new booking for a charging station.")
  public Booking createBooking(@RequestBody BookingRequest request)
      throws ResourceNotFoundException, SlotAlreadyBookedException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long driverId = Long.parseLong(authentication.getName());
    return bookingService.createBooking(request, driverId);
  }

  @GetMapping("/current-bookings")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Get current bookings",
      description = "Retrieves the list of current bookings for the authenticated EV driver.")
  public List<Booking> getBookingsToUnlock() throws ResourceNotFoundException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long driverId = Long.parseLong(authentication.getName());
    return bookingService.getBookingsToUnlock(driverId);
  }

  @PatchMapping("/{bookingId}/unlock")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Unlock a charging station",
      description = "Allows an EV driver to unlock a charging station after payment.")
  public void unlockChargingSlot(@PathVariable Long bookingId) throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long driverId = Long.parseLong(authentication.getName());
    bookingService.unlockChargingSlot(bookingId, driverId);
  }

  @PostMapping("/{bookingId}/finalize-payment")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Finalize booking payment",
      description =
          "Finalizes the payment for a booking, allowing the EV driver to complete the transaction.")
  public void finalizeBookingPayment(@PathVariable Long bookingId) throws Exception {
    bookingService.finalizeBookingPayment(bookingId);
  }

  @GetMapping("/{bookingId}")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public Booking getBooking(@PathVariable Long bookingId) throws ResourceNotFoundException {
    return bookingService.getBookingDetails(bookingId);
  }

  @DeleteMapping("/{bookingId}")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  @Operation(
      summary = "Cancel a booking",
      description = "Allows an EV driver to cancel a booking before it is finalized.")
  public void cancelBooking(@PathVariable Long bookingId) throws Exception {
    bookingService.cancelBooking(bookingId);
  }

  @GetMapping("/consumption")
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  @Operation(
      summary = "Get energy consumption statistics",
      description = "Retrieves the total energy consumption statistics for the operator.")
  public OperatorEnergyResponse getEnergyConsumption() {
    return bookingService.getEnergyConsumption();
  }
}
