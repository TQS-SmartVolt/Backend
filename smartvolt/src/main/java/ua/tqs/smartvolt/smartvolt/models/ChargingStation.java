package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Objects;

@Entity
public class ChargingStation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stationId;

  private String name;
  private String location;
  private boolean availability;

  @ManyToOne private StationOperator operator;

  @OneToMany(mappedBy = "station", cascade = CascadeType.ALL)
  private List<ChargingSlot> slots;

  public ChargingStation() {}

  public ChargingStation(
      String name, String location, boolean availability, StationOperator operator) {
    this.name = name;
    this.location = location;
    this.availability = availability;
    this.operator = operator;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public boolean isAvailability() {
    return availability;
  }

  public void setAvailability(boolean availability) {
    this.availability = availability;
  }

  public StationOperator getOperator() {
    return operator;
  }

  public void setOperator(StationOperator operator) {
    this.operator = operator;
  }

  public List<ChargingSlot> getSlots() {
    return slots;
  }

  public void setSlots(List<ChargingSlot> slots) {
    this.slots = slots;
  }

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
