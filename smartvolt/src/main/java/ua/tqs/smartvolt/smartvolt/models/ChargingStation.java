package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class ChargingStation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long stationId;

  private String name;
  private double latitude;
  private double longitude;
  private String address;
  private boolean availability;

  @ManyToOne
  @JoinColumn(name = "operator_id")
  private StationOperator operator;

  public ChargingStation() {}

  public ChargingStation(
      String name,
      double latitude,
      double longitude,
      String address,
      boolean availability,
      StationOperator operator) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.address = address;
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

  public double getLatitude() {
    return latitude;
  }

  public void setLatitude(double latitude) {
    this.latitude = latitude;
  }

  public double getLongitude() {
    return longitude;
  }

  public void setLongitude(double longitude) {
    this.longitude = longitude;
  }

  public String getAddress() {
    return address;
  }

  public void setAddress(String address) {
    this.address = address;
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

  @Override
  public String toString() {
    return "ChargingStation{"
        + "stationId="
        + stationId
        + ", name='"
        + name
        + '\''
        + ", address='"
        + address
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
