package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import java.time.LocalDateTime;

@Entity
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long bookingId;

  @ManyToOne
  @JoinColumn(name = "driver_id")
  private EvDriver driver;

  @ManyToOne
  @JoinColumn(name = "slot_id")
  private ChargingSlot slot;

  private LocalDateTime startTime;
  private String status;
  private double cost;

  @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
  private ChargingSession chargingSession;

  @OneToOne(mappedBy = "booking", cascade = CascadeType.ALL)
  private Payment payment;

  public Booking() {}

  public Booking(
      EvDriver driver, ChargingSlot slot, LocalDateTime startTime, String status, double cost) {
    this.driver = driver;
    this.slot = slot;
    this.startTime = startTime;
    this.status = status;
    this.cost = cost;
  }

  // Getters and setters...
}
