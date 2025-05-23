package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDateTime;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;
import java.util.Optional;

@Service
public class BookingService {
  private final BookingRepository bookingRepository;
  private final EvDriverRepository evDriverRepository;
  private final ChargingSlotRepository chargingSlotRepository;

  public BookingService(
      BookingRepository bookingRepository,
      EvDriverRepository evDriverRepository,
      ChargingSlotRepository chargingSlotRepository) {
    this.bookingRepository = bookingRepository;
    this.evDriverRepository = evDriverRepository;
    this.chargingSlotRepository = chargingSlotRepository;
  }

  public Booking createBooking(BookingRequest request) throws Exception {
    // TODO: Remove the hardcoded driver and slot
    EvDriver evDriver =
        evDriverRepository
            .findById(request.getDriverId())
            .orElseGet(
                () -> {
                  EvDriver newDriver = new EvDriver();
                  newDriver.setName("Test Driver");
                  newDriver.setEmail("xpto@gmail.com");
                  newDriver.setPassword("password");
                  return evDriverRepository.save(newDriver);
                });

    ChargingSlot slot =
        chargingSlotRepository
            .findById(request.getSlotId())
            .orElseGet(
                () -> {
                  ChargingSlot newSlot = new ChargingSlot();
                  newSlot.setLocked(true);
                  newSlot.setPricePerKWh(0.5);
                  newSlot.setPower(22.0);
                  newSlot.setChargingSpeed(11.0);
                  newSlot.setStation(null); // Set the station to null or assign a valid station
                  return chargingSlotRepository.save(newSlot);
                });

    // Add a print statement to check if the driver is found
    System.out.println("Driver found: " + evDriver.getName());
    System.out.println("Slot found: " + slot.getSlotId());

    Booking booking = new Booking();

    // Long driverId = request.getDriverId();
    // EvDriver evDriver = evDriverRepository.findByUserId(driverId)
    //     .orElseThrow(() -> new Exception("Driver not found"));
    booking.setDriver(evDriver);

    // Long slotId = request.getSlotId();
    // ChargingSlot slot =
    //     chargingSlotRepository.findById(slotId).orElseThrow(() -> new Exception("Slot not found"));
    booking.setSlot(slot);
    Long slotId = slot.getSlotId();

    LocalDateTime startTime = request.getStartTime();
    if (startTime == null) {
      throw new Exception("Start time cannot be null");
    }
    booking.setStartTime(startTime);

    booking.setStatus("Not Used");

    double power = chargingSlotRepository.getPowerBySlotId(slotId)
    .orElseThrow(() -> new Exception("Power not found for slotId: " + slotId));

    double pricePerKWh = chargingSlotRepository.getPricePerKWhBySlotId(slotId)
        .orElseThrow(() -> new Exception("Price not found for slotId: " + slotId));

    if (power < 0 || pricePerKWh < 0) {
      throw new Exception("Invalid power or price");
    }
    double cost = power * pricePerKWh;

    if (cost < 0) {
      throw new Exception("Invalid cost");
    }
    booking.setCost(cost);

    return bookingRepository.save(booking);
  }

  public void finalizeBookingPayment(Long bookingId) throws Exception {
    Booking booking =
        bookingRepository.findById(bookingId).orElseThrow(() -> new Exception("Booking not found"));

    // Verify if booking has expired
    LocalDateTime now = LocalDateTime.now();
    LocalDateTime createdAt = booking.getCreatedAt();

    if (createdAt.plusMinutes(5).isBefore(now)) {
      // Delete the booking if it has expired
      bookingRepository.delete(booking);
      throw new Exception("Booking expired");
    }

    if (booking.getStatus().equals("Not Used")) {
      booking.setStatus("Paid");
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
