package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

  @ManyToOne
  @JoinColumn(name = "driver_id")
  private EvDriver driver;

  @OneToOne
  @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
  private Booking booking;

  public Payment() {}

  public Payment(EvDriver driver, Booking booking) {
    this.driver = driver;
    this.booking = booking;
  }

  // Getters and setters...
}
