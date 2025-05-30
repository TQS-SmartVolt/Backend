package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

@Entity
public class ChargingSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long slotId;

  private boolean isLocked;
  private double pricePerKWh;
  private double power;
  private String chargingSpeed;

  @ManyToOne
  @JoinColumn(name = "station_id")
  private ChargingStation station;

  public ChargingSlot() {}

  public ChargingSlot(
      boolean isLocked,
      double pricePerKWh,
      double power,
      String chargingSpeed,
      ChargingStation station) {
    this.isLocked = isLocked;
    this.pricePerKWh = pricePerKWh;
    this.power = power;
    this.chargingSpeed = chargingSpeed;
    this.station = station;
  }

  // Getters and setters...
  public Long getSlotId() {
    return slotId;
  }

  public boolean isLocked() {
    return isLocked;
  }

  public void setLocked(boolean locked) {
    isLocked = locked;
  }

  public double getPricePerKWh() {
    return pricePerKWh;
  }

  public void setPricePerKWh(double pricePerKWh) {
    this.pricePerKWh = pricePerKWh;
  }

  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public String getChargingSpeed() {
    return chargingSpeed;
  }

  public void setChargingSpeed(String chargingSpeed) {
    this.chargingSpeed = chargingSpeed;
  }

  public ChargingStation getStation() {
    return station;
  }

  public void setStation(ChargingStation station) {
    this.station = station;
  }
}
