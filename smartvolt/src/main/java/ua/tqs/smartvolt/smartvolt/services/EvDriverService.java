package ua.tqs.smartvolt.smartvolt.services;

import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.ChargingHistoryResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;

@Service
public class EvDriverService {

  private final EvDriverRepository evDriverRepository;
  private final BookingRepository bookingRepository;

  public EvDriverService(
      EvDriverRepository evDriverRepository, BookingRepository bookingRepository) {
    this.evDriverRepository = evDriverRepository;
    this.bookingRepository = bookingRepository;
  }

  public Optional<EvDriver> getEvDriverByEmail(String email) throws ResourceNotFoundException {
    return evDriverRepository.findByEmail(email);
  }

  public Optional<EvDriver> getEvDriverById(Long id) throws ResourceNotFoundException {
    return evDriverRepository.findById(id);
  }

  /**
   * Retrieves a list of charging history responses for a given EV Driver. This method fetches the
   * EV Driver by ID, then retrieves all their associated bookings, and maps them to
   * ChargingHistoryResponse DTOs, calculating energy delivered.
   *
   * @param userId The ID of the EV Driver.
   * @return A list of ChargingHistoryResponse objects representing the driver's booking history.
   * @throws ResourceNotFoundException If no EV Driver is found with the given ID.
   */
  public List<ChargingHistoryResponse> getEvDriverBookings(Long userId)
      throws ResourceNotFoundException {
    // Find the EvDriver by their ID, throwing an exception if not found.
    EvDriver evDriver =
        evDriverRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("EvDriver not found with id: " + userId));

    // Get all bookings for the found EvDriver using the BookingRepository.
    // The 'findByDriver' method assumes a relationship where Booking has a 'driver' field.
    List<Booking> driverBookings = bookingRepository.findByDriver(evDriver);

    // Stream through the list of bookings and map each Booking object to a ChargingHistoryResponse
    // DTO.
    return driverBookings.stream()
        .map(
            booking ->
                new ChargingHistoryResponse(
                    booking.getStartTime(), // Start time from the booking
                    booking
                        .getSlot()
                        .getStation()
                        .getName(), // Charging station name from the slot's station
                    booking.getSlot().getChargingSpeed(), // Charging speed from the slot
                    booking.getSlot().getPower(), // Power from the slot
                    getEnergyDelivered(booking.getSlot().getPower()), // Calculated energy delivered
                    booking.getSlot().getPricePerKWh(), // Price per kWh from the slot
                    booking.getCost()) // Cost from the booking
            )
        .toList(); // Collect the mapped objects into a List
  }

  /**
   * Calculates the energy delivered based on the power. Assumes each time slot is 30 minutes (0.5
   * hours), so energy (kWh) = power (kW) * 0.5 (h).
   *
   * @param power The power in kW.
   * @return The energy delivered in kWh.
   */
  private double getEnergyDelivered(double power) {
    // Energy (kWh) = Power (kW) * Time (h)
    // Since each time slot is 30 minutes, time = 0.5 hours.
    return power * 0.5;
  }
}
