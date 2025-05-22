package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;

@Repository
public interface ChargingSlotRepository extends JpaRepository<ChargingSlot, Long> {
  // Custom query methods can be defined here if needed
  // For example, find slots by station ID, availability, etc.

  public double getCostById(Long id) {
    double power = getPowerById(id);
    double pricePerKWh = getPricePerKWhById(id);
    return power * pricePerKWh;
  }

  ChargingSlot findById(Long id);
}
