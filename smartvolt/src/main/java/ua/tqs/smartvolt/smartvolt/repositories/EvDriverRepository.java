package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;

@Repository
public interface EvDriverRepository extends JpaRepository<EvDriver, Long> {
    // Custom query methods can be defined here if needed
    
}
