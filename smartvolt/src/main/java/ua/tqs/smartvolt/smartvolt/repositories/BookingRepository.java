package ua.tqs.smartvolt.smartvolt.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ua.tqs.smartvolt.smartvolt.models.Booking;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
  // Custom query methods can be defined here if needed
  // For example, find bookings by user ID, date range, etc.

}
