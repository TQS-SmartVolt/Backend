package ua.tqs.smartvolt.smartvolt.services;

import org.springframework.stereotype.Service;

import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.repositories.ChargingSlotRepository;

@Service
public class ChargingSlotService {
    private final ChargingSlotRepository chargingSlotRepository;

    public ChargingSlotService(ChargingSlotRepository chargingSlotRepository) {
        this.chargingSlotRepository = chargingSlotRepository;
    }

    public ChargingSlot getSlotById(Long slotId) {
        return chargingSlotRepository.findById(slotId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Slot not found"));
    }

    
}
