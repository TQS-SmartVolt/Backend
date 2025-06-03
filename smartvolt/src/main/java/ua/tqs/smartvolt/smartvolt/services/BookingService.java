package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;

@Service
public class BookingService {

  private static final String DRIVER_NOT_FOUND_MSG = "Driver not found with id: ";

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
            .orElseThrow(() -> new Exception(DRIVER_NOT_FOUND_MSG + driverId));

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

  public List<Booking> getBookingsToUnlock(Long driverId) throws Exception {
    EvDriver evDriver =
        evDriverRepository
            .findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException(DRIVER_NOT_FOUND_MSG + driverId));

    List<Booking> bookings = bookingRepository.findByDriver(evDriver).orElse(java.util.Collections.emptyList());

    deleteNotUsedBookings(bookings);

    // Filter bookings to only include those that are paid
    return bookings.stream()
        .filter(booking -> booking.getStatus().equals("Paid"))
        .toList();
  }

  public void deleteNotUsedBookings(List<Booking> bookings) {
    LocalDateTime now = LocalDateTime.now();
    List<Booking> notUsedBookings = bookings.stream()
        .filter(booking -> booking.getStatus().equals("Not Used"))
        .toList();

    for (Booking booking : notUsedBookings) {
      LocalDateTime createdAt = booking.getCreatedAt();
      if (createdAt.plusMinutes(5).isBefore(now)) {
        bookingRepository.delete(booking);
      }
    }
  }

  public void unlockChargingSlot(Long bookingId, Long driverId) throws Exception {
    Booking booking =
        bookingRepository.findById(bookingId).
            orElseThrow(() -> new Exception("Booking not found with id: " + bookingId));

    EvDriver evDriver =
        evDriverRepository
            .findById(driverId)
            .orElseThrow(() -> new Exception(DRIVER_NOT_FOUND_MSG + driverId));

    if (!booking.getDriver().equals(evDriver)) {
      throw new Exception("Driver does not match booking driver");
    }

    if (booking.getStatus().equals("Paid")) {
      booking.setStatus("Used");
      ChargingSlot slot = booking.getSlot();
      slot.setLocked(false);
      chargingSlotRepository.save(slot);
    } else {
      throw new Exception("Booking is not paid or already used");
    }
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
