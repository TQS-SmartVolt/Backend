package ua.tqs.smartvolt.smartvolt.controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
  public Booking createBooking(@RequestBody BookingRequest request)
      throws Exception {
    return bookingService.createBooking(request);
  }

  @PostMapping("/payment")
  public Booking finalizeBooking(@RequestBody BookingRequest request)
      throws Exception {
    return bookingService.finalizeBooking(request);
  }

}
