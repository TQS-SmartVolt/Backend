package ua.tqs.smartvolt.smartvolt.services;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import ua.tqs.smartvolt.smartvolt.dto.ChargingSlotResponse;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;

@Service
public class ChargingSlotService {
  private final ChargingSlotRepository chargingSlotRepository;

  public ChargingSlotService(ChargingSlotRepository chargingSlotRepository) {
    this.chargingSlotRepository = chargingSlotRepository;
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


}
