package ua.tqs.smartvolt.smartvolt.models;

import java.util.List;
import java.util.Objects;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class ChargingStation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stationId;

  private String name;
  private String location;
  private boolean availability;
  private double power;

  @ManyToOne private StationOperator operator;

  @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
  private List<ChargingSlot> slots;

  public ChargingStation() {}

  public ChargingStation(
      String name, String location, boolean availability, double power, StationOperator operator) {
    this.name = name;
    this.location = location;
    this.availability = availability;
    this.power = power;
    this.operator = operator;
  }

  // Getters and setters...

  @Override
  public String toString() {
    return "ChargingStation{"
        + "stationId="
        + stationId
        + ", name='"
        + name
        + '\''
        + ", location='"
        + location
        + '\''
        + ", availability="
        + availability
        + ", power="
        + power
        + '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof ChargingStation)) return false;
    ChargingStation that = (ChargingStation) o;
    return Objects.equals(stationId, that.stationId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stationId);
  }
}
