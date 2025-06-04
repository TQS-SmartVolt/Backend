package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.dto.OperatorEnergyResponse;
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
  public Booking createBooking(@RequestBody BookingRequest request) throws Exception {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    Long driverId = Long.parseLong(authentication.getName());
    return bookingService.createBooking(request, driverId);
  }

  @PostMapping("/{bookingId}/finalize-payment")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public void finalizeBookingPayment(@PathVariable Long bookingId) throws Exception {
    bookingService.finalizeBookingPayment(bookingId);
  }

  @DeleteMapping("/{bookingId}")
  @PreAuthorize("hasRole('ROLE_EV_DRIVER')")
  public void cancelBooking(@PathVariable Long bookingId) throws Exception {
    bookingService.cancelBooking(bookingId);
  }

  @GetMapping("/consumption")
  @PreAuthorize("hasRole('ROLE_STATION_OPERATOR')")
  public OperatorEnergyResponse getEnergyConsumption() {
    return bookingService.getEnergyConsumption();
  }
}
