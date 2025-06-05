package ua.tqs.smartvolt.smartvolt.repositories;

import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  boolean existsBySlotAndStartTime(ChargingSlot slot, LocalDateTime slotTime);

  // Custom query methods can be defined here if needed
  // For example, find bookings by user ID, date range, etc.

  Optional<List<Booking>> findByDriver(EvDriver driver);

  List<Booking> findByStatus(String status);

  Optional<Booking> findBySlotAndStartTime(ChargingSlot slot, LocalDateTime startTime);

  @Modifying // Indicates that this query will modify the database
  @Transactional // Ensures the operation runs within a transaction
  @Query(
      "DELETE FROM Booking b WHERE b.driver = :driver AND b.createdAt < :cutOffTime AND b.status = :status")
  int deleteExpiredBookingsByDriverAndStatus(
      @Param("driver") EvDriver driver,
      @Param("cutOffTime") LocalDateTime cutOffTime,
      @Param("status") String status);
}
