package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@Service
public class BookingService {
  private final BookingRepository bookingRepository;
  private final EvDriverRepository evDriverRepository;
  private final ChargingSlotRepository chargingSlotRepository;
  private final ChargingStationRepository chargingStationRepository;

  public BookingService(
      BookingRepository bookingRepository,
      EvDriverRepository evDriverRepository,
      ChargingSlotRepository chargingSlotRepository,
      ChargingStationRepository chargingStationRepository) {
    this.bookingRepository = bookingRepository;
    this.evDriverRepository = evDriverRepository;
    this.chargingSlotRepository = chargingSlotRepository;
    this.chargingStationRepository = chargingStationRepository;
  }

  public Booking createBooking(BookingRequest request, Long driverId) throws Exception {
    // Get the driver, slot and start time from the request
    EvDriver evDriver =
        evDriverRepository
            .findById(driverId)
            .orElseThrow(() -> new Exception("Driver not found with id: " + driverId));

    final Long slotId = request.getSlotId();
    ChargingSlot slot =
        chargingSlotRepository
            .findById(slotId)
            .orElseThrow(() -> new Exception("Slot not found with id: " + slotId));

    LocalDateTime startTime = request.getStartTime();
    if (startTime == null) {
      throw new Exception("Start time cannot be null");
    }

    // Create a new booking
    Booking booking = new Booking();

    final double power =
        chargingSlotRepository
            .getPowerBySlotId(slotId)
            .orElseThrow(() -> new Exception("Power not found for slotId: " + slotId));

    final double pricePerKWh =
        chargingSlotRepository
            .getPricePerKWhBySlotId(slotId)
            .orElseThrow(() -> new Exception("Price not found for slotId: " + slotId));

    if (power < 0 || pricePerKWh < 0) {
      throw new Exception("Invalid power or price");
    }
    double cost = power * pricePerKWh;

    if (cost < 0) {
      throw new Exception("Invalid cost");
    }

    // Set booking details
    booking.setDriver(evDriver);
    booking.setSlot(slot);
    booking.setStartTime(startTime);
    booking.setStatus("Not Used");
    booking.setCost(cost);

    return bookingRepository.save(booking);
  }

  public void finalizeBookingPayment(Long bookingId) throws Exception {
    Booking booking =
        bookingRepository.findById(bookingId).orElseThrow(() -> new Exception("Booking not found"));

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime createdAt = booking.getCreatedAt();
    if (createdAt.plusMinutes(5).isBefore(now)) {
      bookingRepository.delete(booking);
      throw new Exception("Booking expired");
    }

    if (booking.getStatus().equals("Not Used")) {
      booking.setStatus("Paid");
      bookingRepository.save(booking);
    } else {
      throw new Exception("Booking already paid");
    }
  }

  public void cancelBooking(Long bookingId) throws Exception {
    Booking booking =
        bookingRepository.findById(bookingId).orElseThrow(() -> new Exception("Booking not found"));
    if (booking.getStatus().equals("Not Used")) {
      bookingRepository.delete(booking);
    } else {
      throw new Exception("Booking cannot be cancelled");
    }
  }
}
