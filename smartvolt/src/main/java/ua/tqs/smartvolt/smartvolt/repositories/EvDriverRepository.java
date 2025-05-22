package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;

@Repository
public interface EvDriverRepository extends JpaRepository<EvDriver, Long> {
  // Custom query methods can be defined here if needed
  EvDriver findById(Long id);
}
