package ua.tqs.smartvolt.smartvolt.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;

@Service
public class EvDriverService {
    
    private EvDriverRepository evDriver;

    public EvDriverService(EvDriverRepository evDriver) {
        this.evDriver = evDriver;
    }


    public Optional<EvDriver> getEvDriverByEmail(String email) throws ResourceNotFoundException {
        return evDriver.findByEmail(email);
    }

}
