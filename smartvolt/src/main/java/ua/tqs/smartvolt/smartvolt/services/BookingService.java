package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.BookingRequest;
import ua.tqs.smartvolt.smartvolt.dto.OperatorEnergyResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.exceptions.SlotAlreadyBookedException;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSession;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@Service
public class BookingService {

  private static final String DRIVER_NOT_FOUND_MSG = "Driver not found with id: ";
  private static final String NOT_USED_STATUS = "not_used";

  private final BookingRepository bookingRepository;
  private final EvDriverRepository evDriverRepository;
  private final ChargingSlotRepository chargingSlotRepository;
  private final ChargingSessionService chargingSessionService;

  public BookingService(
      BookingRepository bookingRepository,
      EvDriverRepository evDriverRepository,
      ChargingSlotRepository chargingSlotRepository,
      ChargingSessionService chargingSessionService) {
    this.chargingSessionService = chargingSessionService;
    this.bookingRepository = bookingRepository;
    this.evDriverRepository = evDriverRepository;
    this.chargingSlotRepository = chargingSlotRepository;
  }

  public Booking createBooking(BookingRequest request, Long driverId)
      throws ResourceNotFoundException, SlotAlreadyBookedException {
    // Get the driver, slot and start time from the request
    EvDriver evDriver =
        evDriverRepository
            .findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException(DRIVER_NOT_FOUND_MSG + driverId));

    final Long slotId = request.getSlotId();
    ChargingSlot slot =
        chargingSlotRepository
            .findById(slotId)
            .orElseThrow(() -> new ResourceNotFoundException("Slot not found with id: " + slotId));

    // Check if the start time is exactly on the hour or half-hour mark
    LocalDateTime startTime = request.getStartTime();
    if (startTime == null) {
      throw new IllegalArgumentException("Start time cannot be null");
    }

    if (startTime.getSecond() != 0
        || startTime.getNano() != 0
        || (startTime.getMinute() % 30 != 0)) {
      throw new IllegalArgumentException(
          "Booking start time must be on a 30-minute interval (e.g., HH:00 or HH:30).");
    }

    // Check if the booking is in the past
    LocalDateTime now = LocalDateTime.now();
    if (startTime.isBefore(now)) {
      throw new IllegalArgumentException("Cannot create a booking in the past.");
    }

    Optional<Booking> existingBooking = bookingRepository.findBySlotAndStartTime(slot, startTime);
    if (existingBooking.isPresent()) {
      throw new SlotAlreadyBookedException(
          "Slot "
              + slotId
              + " is already booked at "
              + startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")));
    }

    // Create a new booking
    Booking booking = new Booking();

    final double power =
        chargingSlotRepository
            .getPowerBySlotId(slotId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Power not found for slotId: " + slotId));

    final double pricePerKWh =
        chargingSlotRepository
            .getPricePerKWhBySlotId(slotId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Price not found for slotId: " + slotId));

    if (power < 0 || pricePerKWh < 0) {
      throw new IllegalArgumentException("Invalid power or price");
    }

    // Corrected cost calculation: energyDelivered (power * 0.5) * pricePerKWh
    double energyDelivered = power * 0.5; // Calculate energy delivered for 30 minutes
    double cost = energyDelivered * pricePerKWh; // Calculate cost based on energy delivered

    if (cost < 0) {
      throw new IllegalArgumentException("Invalid cost");
    }

    // Set booking details
    booking.setDriver(evDriver);
    booking.setSlot(slot);
    booking.setStartTime(startTime);
    booking.setStatus(NOT_USED_STATUS);
    booking.setCost(cost);

    return bookingRepository.save(booking);
  }

  public List<Booking> getBookingsToUnlock(Long driverId) throws ResourceNotFoundException {
    EvDriver evDriver =
        evDriverRepository
            .findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException(DRIVER_NOT_FOUND_MSG + driverId));

    List<Booking> bookings =
        bookingRepository.findByDriver(evDriver).orElse(java.util.Collections.emptyList());
    deleteNotUsedBookings(bookings);

    return bookings.stream()
        .filter(
            booking -> {
              if (booking.getStatus().equals("used")) {
                LocalDateTime now = LocalDateTime.now();
                // "Used" bookings: only include if now is within 30 minutes after start time
                return !now.isBefore(booking.getStartTime())
                    && now.isBefore(booking.getStartTime().plusMinutes(30));
              } else if (booking.getStatus().equals("paid")) {
                // "paid" bookings: include all from now on
                return !booking.getStartTime().isBefore(LocalDateTime.now().minusMinutes(30));
              }
              return false;
            })
        .toList();
  }

  public void deleteNotUsedBookings(List<Booking> bookings) {
    LocalDateTime now = LocalDateTime.now();
    List<Booking> notUsedBookings =
        bookings.stream().filter(booking -> booking.getStatus().equals(NOT_USED_STATUS)).toList();

    for (Booking booking : notUsedBookings) {
      LocalDateTime createdAt = booking.getCreatedAt();
      if (createdAt.plusMinutes(5).isBefore(now)) {
        bookingRepository.delete(booking);
      }
    }
  }

  public void unlockChargingSlot(Long bookingId, Long driverId) throws Exception {
    Booking booking =
        bookingRepository
            .findById(bookingId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

    EvDriver evDriver =
        evDriverRepository
            .findById(driverId)
            .orElseThrow(() -> new ResourceNotFoundException(DRIVER_NOT_FOUND_MSG + driverId));

    chargingSessionService.createChargingSession(booking);

    if (!booking.getDriver().equals(evDriver)) {
      throw new IllegalStateException("Driver does not match booking driver");
    }

    if (booking.getStatus().equals("paid")) {
      booking.setStatus("used");
      ChargingSlot slot = booking.getSlot();
      slot.setLocked(false);
      chargingSlotRepository.save(slot);
    } else {
      throw new IllegalStateException("Booking is not paid or already used");
    }
  }

  public void finalizeBookingPayment(Long bookingId) throws Exception {
    Booking booking =
        bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));

    LocalDateTime now = LocalDateTime.now();
    LocalDateTime createdAt = booking.getCreatedAt();
    if (createdAt.plusMinutes(5).isBefore(now)) {
      bookingRepository.delete(booking);
      throw new IllegalStateException("Booking expired");
    }

    if (booking.getStatus().equals(NOT_USED_STATUS)) {
      booking.setStatus("paid");
      bookingRepository.save(booking);
    } else {
      throw new IllegalStateException("Booking already paid");
    }
  }

  public void cancelBooking(Long bookingId)
      throws ResourceNotFoundException, IllegalStateException {
    Booking booking =
        bookingRepository
            .findById(bookingId)
            .orElseThrow(() -> new ResourceNotFoundException("Booking not found"));
    if (booking.getStatus().equals(NOT_USED_STATUS)) {
      bookingRepository.delete(booking);
    } else {
      throw new IllegalStateException("Booking cannot be cancelled");
    }
  }

  public OperatorEnergyResponse getEnergyConsumption() {
    OperatorEnergyResponse response = new OperatorEnergyResponse();
    List<Booking> bookings = bookingRepository.findAll();
    Map<String, Double> monthEnergy = new LinkedHashMap<>();
    LocalDateTime today = LocalDateTime.now();
    LocalDateTime oneYearAgo = today.minusYears(1).plusMonths(1);

    YearMonth startMonth = YearMonth.from(oneYearAgo).plusMonths(1);
    YearMonth endMonth = YearMonth.from(today);
    while (!startMonth.isAfter(endMonth)) {
      String monthName = startMonth.getMonth().toString().substring(0, 3).toLowerCase();
      monthName = monthName.substring(0, 1).toUpperCase() + monthName.substring(1);
      monthEnergy.put(monthName, 0.0);
      startMonth = startMonth.plusMonths(1);
    }

    for (Booking booking : bookings) {
      if (booking != null && "used".equals(booking.getStatus())) {
        LocalDateTime startTime = booking.getStartTime();
        if (startTime.isAfter(oneYearAgo) && startTime.isBefore(today)) {
          String month = startTime.getMonth().toString().substring(0, 3).toLowerCase();
          month =
              month.substring(0, 1).toUpperCase() + month.substring(1); // Capitalize first letter
          ChargingSession session = booking.getChargingSession();
          double energy = session.getEnergyDelivered();
          monthEnergy.put(month, monthEnergy.getOrDefault(month, 0.0) + energy);
        }
      }
    }
    response.setMonthEnergy(monthEnergy);
    double totalEnergy = monthEnergy.values().stream().mapToDouble(Double::doubleValue).sum();
    response.setTotalEnergy(totalEnergy);
    double averageEnergyPerMonth =
        Math.round(
                monthEnergy.values().stream().mapToDouble(Double::doubleValue).average().orElse(0.0)
                    * 100.0)
            / 100.0;
    response.setAverageEnergyPerMonth(averageEnergyPerMonth);
    return response;
  }

  public Booking getBookingDetails(Long bookingId) throws ResourceNotFoundException {
    return bookingRepository
        .findById(bookingId)
        .orElseThrow(
            () -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
  }
}
