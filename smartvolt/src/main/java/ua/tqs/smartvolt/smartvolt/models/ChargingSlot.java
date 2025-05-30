package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class ChargingSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long slotId;

  private boolean isLocked;
  private double pricePerKWh;
  private double power;
  private double chargingSpeed;

  @ManyToOne
  @JoinColumn(name = "station_id")
  private ChargingStation station;

  @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  public ChargingSlot() {}

  public ChargingSlot(
      boolean isLocked,
      double pricePerKWh,
      double power,
      double chargingSpeed,
      ChargingStation station) {
    this.isLocked = isLocked;
    this.pricePerKWh = pricePerKWh;
    this.power = power;
    this.chargingSpeed = chargingSpeed;
    this.station = station;
  }

  // Getters and setters...
}
