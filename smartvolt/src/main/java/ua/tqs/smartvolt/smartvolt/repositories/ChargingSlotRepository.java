package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;

import java.util.Optional;

@Repository
public interface ChargingSlotRepository extends JpaRepository<ChargingSlot, Long> {
  Optional<ChargingSlot> findById(Long id);
  double getPowerById(Long id);
  double getPricePerKWhById(Long id);
}


