package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;

@Entity
public class ChargingSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sessionId;

  private double energyDelivered;

  @OneToOne
  @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
  private Booking booking;

  public ChargingSession() {}

  public ChargingSession(double energyDelivered, Booking booking) {
    this.energyDelivered = energyDelivered;
    this.booking = booking;
  }

  // Getters and setters...
}
