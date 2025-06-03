package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import java.time.LocalDateTime;

@Entity
public class Booking {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "booking_id")
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

  @Column(nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @PrePersist
  protected void onCreate() {
    this.createdAt = LocalDateTime.now();
  }

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
  public Long getBookingId() {
    return bookingId;
  }

  public void setBookingId(Long bookingId) {
    this.bookingId = bookingId;
  }

  public EvDriver getDriver() {
    return driver;
  }

  public void setDriver(EvDriver driver) {
    this.driver = driver;
  }

  public ChargingSlot getSlot() {
    return slot;
  }

  public void setSlot(ChargingSlot slot) {
    this.slot = slot;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }

  public ChargingSession getChargingSession() {
    return chargingSession;
  }

  public void setChargingSession(ChargingSession chargingSession) {
    this.chargingSession = chargingSession;
  }

  public Payment getPayment() {
    return payment;
  }

  public void setPayment(Payment payment) {
    this.payment = payment;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
