package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
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
  public Booking createBooking(@RequestBody BookingRequest request) throws Exception {
    return bookingService.createBooking(request);
  }

  @PostMapping("/payment")
  public void finalizeBookingPayment(@RequestBody Long request) throws Exception {
    bookingService.finalizeBookingPayment(request);
  }

  @DeleteMapping("/{bookingId}")
  public void cancelBooking(@PathVariable Long bookingId) throws Exception {
    bookingService.cancelBooking(bookingId);
  }
}
