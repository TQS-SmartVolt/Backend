package ua.tqs.smartvolt.smartvolt.services;

import java.util.Optional;

import org.springframework.stereotype.Service;

import ua.tqs.smartvolt.smartvolt.repositories.StationOperatorRepository;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;

@Service
public class StationOperatorService {
    
    private StationOperatorRepository stationOperator;

    public StationOperatorService(StationOperatorRepository stationOperator) {
        this.stationOperator = stationOperator;
    }

    public Optional<StationOperator> getStationOperatorByEmail(String email) throws ResourceNotFoundException {
        return stationOperator.findByEmail(email);
    }

}
