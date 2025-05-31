package ua.tqs.smartvolt.smartvolt.repositories;

import java.time.LocalDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.Booking;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

  boolean existsBySlotAndStartTime(ChargingSlot slot, LocalDateTime slotTime);
  // Custom query methods can be defined here if needed
  // For example, find bookings by user ID, date range, etc.

}
