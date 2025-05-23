package ua.tqs.smartvolt.smartvolt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;

import java.util.Optional;

@Repository
public interface EvDriverRepository extends JpaRepository<EvDriver, Long> {
  // Custom query methods can be defined here if needed
  Optional<EvDriver> findById(Long id);
}
