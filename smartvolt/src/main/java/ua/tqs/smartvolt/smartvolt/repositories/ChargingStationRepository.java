package ua.tqs.smartvolt.smartvolt.repositories;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.ChargingStation;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;

@Repository
public interface ChargingStationRepository extends JpaRepository<ChargingStation, Long> {
  List<ChargingStation> findByOperator(StationOperator operator);
}
