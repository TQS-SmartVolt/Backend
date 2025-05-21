package ua.tqs.smartvolt.smartvolt.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ChargingSlot {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long slotId;

  private boolean isLocked;
  private double pricePerKWh;
  private double chargingSpeed;

  @ManyToOne private ChargingStation station;

  @OneToMany(mappedBy = "slot", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  public ChargingSlot() {}

  public ChargingSlot(
      boolean isLocked, double pricePerKWh, double chargingSpeed, ChargingStation station) {
    this.isLocked = isLocked;
    this.pricePerKWh = pricePerKWh;
    this.chargingSpeed = chargingSpeed;
    this.station = station;
  }

  // Getters and setters...
}
