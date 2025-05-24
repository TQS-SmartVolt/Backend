package ua.tqs.smartvolt.smartvolt.services;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotResponse;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotsResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingStationRepository;

import java.time.LocalDateTime;

@Service
public class ChargingSlotService {
  private final ChargingSlotRepository chargingSlotRepository;
  private final ChargingStationRepository chargingStationRepository;

  public ChargingSlotService(ChargingSlotRepository chargingSlotRepository,
                             ChargingStationRepository chargingStationRepository) {
    this.chargingSlotRepository = chargingSlotRepository;
    this.chargingStationRepository = chargingStationRepository;
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

  public ChargingSlotsResponse getAvailableSlots(Long stationId, String chargingSpeed, LocalDate date) throws ResourceNotFoundException {
    
    ChargingStation station = chargingStationRepository.findById(stationId)
        .orElseThrow(() -> new ResourceNotFoundException("Charging station not found with id: " + stationId));
    
    List<ChargingSlot> matchingSlots = station.getSlots().stream()
        .filter(slot -> slot.getChargingSpeed().equalsIgnoreCase(chargingSpeed))
        .toList();
    
    if (matchingSlots.isEmpty()) {  
      throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No slots found for the given speed.");
    }

    // 1. Get all booked times for the given date and speed
    List<LocalDateTime> bookedTimes = matchingSlots.stream()
        .flatMap(slot -> slot.getBookings().stream())
        .map(booking -> booking.getStartTime())
        .filter(startTime -> startTime.toLocalDate().equals(date))
        .toList();
    
    // 2. Generate all 30-minute slots from 00:00 to 23:30 for that date
    List<LocalDateTime> allSlots = new ArrayList<>();
    LocalDateTime start = date.atStartOfDay();
    for (int i = 0; i < 48; i++) {
        allSlots.add(start.plusMinutes(30 * i));
    }

    // 3. Remove booked times
    List<LocalDateTime> availableSlots = allSlots.stream()
        .filter(slot -> !bookedTimes.contains(slot))
        .toList();

    ChargingSlotsResponse response = new ChargingSlotsResponse();
    response.setAvailableTimeSlots(availableSlots.toArray(new LocalDateTime[0])); 
    response.setPricePerKWh(matchingSlots.get(0).getPricePerKWh());

    return response;

  }
}
