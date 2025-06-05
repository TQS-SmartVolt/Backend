package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import ua.tqs.smartvolt.smartvolt.dto.ChargingHistoryResponse;
import ua.tqs.smartvolt.smartvolt.dto.ConsumptionResponse;
import ua.tqs.smartvolt.smartvolt.dto.SpendingResponse;
import ua.tqs.smartvolt.smartvolt.dto.UserInfoResponse;
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

    cleanUpExpiredBookingsForDriver(evDriver);

    // Get all bookings for the found EvDriver using the BookingRepository.
    List<Booking> driverBookings = bookingRepository.findByDriver(evDriver);

    // Stream through the list of bookings and map each Booking object to a
    // ChargingHistoryResponse
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
   * Retrieves the EV Driver's monthly consumption data.
   *
   * @param userId The ID of the EV Driver.
   * @return A ConsumptionResponse containing the monthly consumption data.
   * @throws ResourceNotFoundException If no EV Driver is found with the given ID.
   */
  public ConsumptionResponse getEvDriverConsumption(Long userId) throws ResourceNotFoundException {
    // Find the EvDriver by their ID, throwing an exception if not found.
    EvDriver evDriver =
        evDriverRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("EvDriver not found with id: " + userId));

    cleanUpExpiredBookingsForDriver(evDriver);

    // Get all bookings for the found EvDriver using the BookingRepository.
    List<Booking> driverBookings = bookingRepository.findByDriver(evDriver);

    if (driverBookings.isEmpty()) {
      return new ConsumptionResponse(Collections.nCopies(12, 0.0)); // Return empty consumption
    }

    // Group bookings by month and sum their energy delivered
    java.util.Map<Integer, Double> monthlyConsumptionMap =
        driverBookings.stream()
            .collect(
                java.util.stream.Collectors.groupingBy(
                    booking -> booking.getStartTime().getMonthValue(), // Group by month
                    java.util.stream.Collectors.summingDouble(
                        booking -> getEnergyDelivered(booking.getSlot().getPower()))));

    // Convert the map to a list ordered by month (1 to 12)
    List<Double> monthlyConsumption = new java.util.ArrayList<>();
    for (int month = 1; month <= 12; month++) {
      monthlyConsumption.add(monthlyConsumptionMap.getOrDefault(month, 0.0));
    }

    return new ConsumptionResponse(monthlyConsumption); // Return the consumption response
  }

  /**
   * Retrieves the EV Driver's monthly spending data.
   *
   * @param userId The ID of the EV Driver.
   * @return A SpendingResponse containing the monthly spending data.
   * @throws ResourceNotFoundException If no EV Driver is found with the given ID.
   */
  public SpendingResponse getEvDriverSpending(Long userId) throws ResourceNotFoundException {
    // Find the EvDriver by their ID, throwing an exception if not found.
    EvDriver evDriver =
        evDriverRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("EvDriver not found with id: " + userId));

    cleanUpExpiredBookingsForDriver(evDriver);

    // Get all bookings for the found EvDriver using the BookingRepository.
    List<Booking> driverBookings = bookingRepository.findByDriver(evDriver);

    if (driverBookings.isEmpty()) {
      return new SpendingResponse(Collections.nCopies(12, 0.0)); // Return empty spending
    }

    // Group bookings by month and sum their costs
    java.util.Map<Integer, Double> monthlySpendingMap =
        driverBookings.stream()
            .collect(
                java.util.stream.Collectors.groupingBy(
                    booking -> booking.getStartTime().getMonthValue(), // Group by month
                    java.util.stream.Collectors.summingDouble(Booking::getCost) // Sum costs
                    ));

    // Convert the map to a list ordered by month (1 to 12)
    List<Double> monthlySpending = new java.util.ArrayList<>();
    for (int month = 1; month <= 12; month++) {
      monthlySpending.add(monthlySpendingMap.getOrDefault(month, 0.0));
    }

    return new SpendingResponse(monthlySpending); // Return the spending response
  }

  public UserInfoResponse getEvDriverInfo(Long userId) throws ResourceNotFoundException {
    // Find the EvDriver by their ID, throwing an exception if not found.
    EvDriver evDriver =
        evDriverRepository
            .findById(userId)
            .orElseThrow(
                () -> new ResourceNotFoundException("EvDriver not found with id: " + userId));

    ConsumptionResponse consumptionResponse = getEvDriverConsumption(userId);
    double totalEnergyConsumed =
        consumptionResponse.getConsumptionPerMonth().stream()
            .mapToDouble(Double::doubleValue)
            .sum();
    SpendingResponse spendingResponse = getEvDriverSpending(userId);
    double totalMoneySpent =
        spendingResponse.getSpendingPerMonth().stream().mapToDouble(Double::doubleValue).sum();

    // Return a UserInfoResponse DTO with the driver's name, email, total energy
    // consumed, and
    // total money spent.
    return new UserInfoResponse(
        evDriver.getName(), evDriver.getEmail(), totalEnergyConsumed, totalMoneySpent);
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

  /**
   * Deletes expired bookings for a given EV Driver. A booking is considered expired if its
   * 'createdAt' timestamp is more than 5 minutes in the past and its status is "Not Used".
   *
   * @param driver The EvDriver for whom to clean up bookings.
   */
  private void cleanUpExpiredBookingsForDriver(EvDriver driver) {
    LocalDateTime fiveMinutesAgo = LocalDateTime.now().minusMinutes(5);
    bookingRepository.deleteExpiredBookingsByDriverAndStatus(driver, fiveMinutesAgo, "Not Used");
  }
}
