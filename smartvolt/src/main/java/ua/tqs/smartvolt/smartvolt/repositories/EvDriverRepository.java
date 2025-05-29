package ua.tqs.smartvolt.smartvolt.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ua.tqs.smartvolt.smartvolt.models.EvDriver;

@Repository
public interface EvDriverRepository extends JpaRepository<EvDriver, Long> {

    Optional<EvDriver> findByEmail(String email);
}
