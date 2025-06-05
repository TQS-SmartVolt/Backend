package ua.tqs.smartvolt.smartvolt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
  @JsonBackReference
  private Booking booking;

  public ChargingSession() {}

  public ChargingSession(double energyDelivered, Booking booking) {
    this.energyDelivered = energyDelivered;
    this.booking = booking;
  }

  public Long getSessionId() {
    return sessionId;
  }

  public void setSessionId(Long sessionId) {
    this.sessionId = sessionId;
  }

  public double getEnergyDelivered() {
    return energyDelivered;
  }

  public void setEnergyDelivered(double energyDelivered) {
    this.energyDelivered = energyDelivered;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    this.booking = booking;
  }

  @Override
  public String toString() {
    return "ChargingSession{"
        + "sessionId="
        + sessionId
        + ", energyDelivered="
        + energyDelivered
        + ", booking="
        + booking
        + '}';
  }
}
