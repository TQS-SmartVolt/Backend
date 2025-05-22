package ua.tqs.smartvolt.smartvolt.services;

import org.springframework.stereotype.Service;

import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;


@Service
public class BookingService {
  private final BookingRepository bookingRepository;

  public BookingService(
      BookingRepository bookingRepository) {
    this.bookingRepository = bookingRepository;
  }

  public Booking createBooking(BookingRequest request) throws Exception {
    Booking booking = new Booking();
    booking.setDriver(request.getDriverId());
  }

  public Booking finalizeBooking(BookingRequest request) throws Exception {
    // Implement the logic to finalize a booking
    return null;
  }
    
}
