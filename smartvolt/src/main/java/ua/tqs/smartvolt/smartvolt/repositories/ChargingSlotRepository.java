package ua.tqs.smartvolt.smartvolt.repositories;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;

@Repository
public interface ChargingSlotRepository extends JpaRepository<ChargingSlot, Long> {
  Optional<ChargingSlot> findBySlotId(Long slotId);

  @Query("SELECT s.power FROM ChargingSlot s WHERE s.slotId = :slotId")
  Optional<Double> getPowerBySlotId(@Param("slotId") Long slotId);

  @Query("SELECT s.pricePerKWh FROM ChargingSlot s WHERE s.slotId = :slotId")
  Optional<Double> getPricePerKWhBySlotId(@Param("slotId") Long slotId);

  @Query("SELECT DISTINCT s.station FROM ChargingSlot s WHERE s.chargingSpeed = :chargingSpeed")
  List<ChargingStation> findStationsByChargingSpeed(@Param("chargingSpeed") String chargingSpeed);

  List<ChargingSlot> findByStationAndChargingSpeed(ChargingStation station, String chargingSpeed);

  List<ChargingSlot> findByStation(ChargingStation station);
}
