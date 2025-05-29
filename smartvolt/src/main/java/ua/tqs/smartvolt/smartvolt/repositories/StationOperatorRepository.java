package ua.tqs.smartvolt.smartvolt.repositories;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;

@Repository
public interface StationOperatorRepository extends JpaRepository<StationOperator, Long> {
  Optional<StationOperator> findById(Long id);
  Optional<StationOperator> findByEmail(String email);
}
