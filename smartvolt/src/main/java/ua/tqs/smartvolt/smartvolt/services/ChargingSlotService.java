package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotRequest;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotResponse;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotsResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.InvalidRequestException;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.repositories.BookingRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;

@Service
public class ChargingSlotService {
  private final ChargingSlotRepository chargingSlotRepository;
  private final ChargingStationRepository chargingStationRepository;
  private final BookingRepository bookingRepository;

  public ChargingSlotService(
      ChargingSlotRepository chargingSlotRepository,
      ChargingStationRepository chargingStationRepository,
      BookingRepository bookingRepository) {
    this.chargingSlotRepository = chargingSlotRepository;
    this.chargingStationRepository = chargingStationRepository;
    this.bookingRepository = bookingRepository;
  }

  public ChargingSlot addChargingSlotToStation(Long stationId, ChargingSlotRequest slotRequest)
      throws ResourceNotFoundException, InvalidRequestException {

    ChargingStation station =
        chargingStationRepository
            .findById(stationId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Charging station not found with id: " + stationId));

    String chargingSpeed = slotRequest.getChargingSpeed();
    double power;
    switch (chargingSpeed) {
      case "Slow":
        power = 10.0;
        break;
      case "Medium":
        power = 20.0;
        break;
      case "Fast":
        power = 30.0;
        break;
      default:
        throw new InvalidRequestException(
            "Invalid charging speed: " + chargingSpeed + ". Valid options are Slow, Medium, Fast.");
    }

    ChargingSlot newSlot = new ChargingSlot();
    newSlot.setLocked(false);
    newSlot.setPricePerKWh(slotRequest.getPricePerKWh());
    newSlot.setPower(power);
    newSlot.setChargingSpeed(slotRequest.getChargingSpeed());
    newSlot.setStation(station);
    return chargingSlotRepository.save(newSlot);
  }

  public ChargingSlotResponse getSlotDetailsById(Long slotId) {
    ChargingSlot slot =
        chargingSlotRepository
            .findById(slotId)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));

    ChargingSlotResponse response = new ChargingSlotResponse();
    double power = slot.getPower();
    double pricePerKWh = slot.getPricePerKWh();
    ChargingStation station = slot.getStation();
    Long stationId = station.getStationId();

    response.setPower(power);
    response.setPricePerKWh(pricePerKWh);
    response.setStationId(stationId);

    return response;
  }

  public ChargingSlotsResponse getAvailableSlots(
      Long stationId, String chargingSpeed, LocalDate date) throws ResourceNotFoundException {

    if (date.isBefore(LocalDate.now())) {
      // Return an empty response for past dates
      ChargingSlotsResponse response = new ChargingSlotsResponse();
      response.setAvailableSlotMapping(new ArrayList<>()); // Empty list
      response.setPricePerKWh(0.0); // Or perhaps a configurable default for non-bookable dates
      return response;
    }

    ChargingStation station =
        chargingStationRepository
            .findById(stationId)
            .orElseThrow(
                () ->
                    new ResourceNotFoundException(
                        "Charging station not found with id: " + stationId));

    List<ChargingSlot> matchingSlots =
        chargingSlotRepository.findByStationAndChargingSpeed(station, chargingSpeed);

    if (matchingSlots.isEmpty()) {
      ChargingSlotsResponse response = new ChargingSlotsResponse();
      response.setAvailableSlotMapping(new ArrayList<>()); // Return an empty list
      response.setPricePerKWh(0.0); // Set a default price, as no slots are available
      return response;
    }

    Map<LocalDateTime, List<ChargingSlot>> slotAvailabilityMap = new HashMap<>();

    // Iterate over 48 half-hour slots in the selected day (00:00 to 23:30)
    LocalDateTime startOfDay = date.atStartOfDay();
    for (int i = 0; i < 48; i++) {
      LocalDateTime slotTime = startOfDay.plusMinutes(30L * i);
      for (ChargingSlot slot : matchingSlots) {
        // Check if this slot is already booked at this time
        boolean isBooked = bookingRepository.existsBySlotAndStartTime(slot, slotTime);

        // If it's not booked, mark this slot as available for that time
        if (!isBooked) {
          slotAvailabilityMap.computeIfAbsent(slotTime, k -> new ArrayList<>()).add(slot);
        }
      }
    }

    // Convert available map to a flat list of slotId + time pairs
    List<ChargingSlotsResponse.SlotAvailability> availableSlotMapping = new ArrayList<>();
    for (Map.Entry<LocalDateTime, List<ChargingSlot>> entry : slotAvailabilityMap.entrySet()) {
      for (ChargingSlot slot : entry.getValue()) {
        availableSlotMapping.add(
            new ChargingSlotsResponse.SlotAvailability(slot.getSlotId(), entry.getKey()));
      }
    }

    // Create and return the response DTO
    ChargingSlotsResponse response = new ChargingSlotsResponse();
    response.setAvailableSlotMapping(availableSlotMapping);
    response.setPricePerKWh(matchingSlots.get(0).getPricePerKWh());

    return response;
  }
}
