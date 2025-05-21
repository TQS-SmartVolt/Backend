package ua.tqs.smartvolt.smartvolt.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;

@Entity
public class ChargingSession {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long sessionId;

  private double energyDelivered;

  @OneToOne private Booking booking;

  public ChargingSession() {}

  public ChargingSession(double energyDelivered, Booking booking) {
    this.energyDelivered = energyDelivered;
    this.booking = booking;
  }

  // Getters and setters...
}
