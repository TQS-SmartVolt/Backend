package ua.tqs.smartvolt.smartvolt.dto;

import java.util.ArrayList;
import java.util.List;
import ua.tqs.smartvolt.smartvolt.models.ChargingSlot;
import ua.tqs.smartvolt.smartvolt.models.StationOperator;

public class ChargingStationWithSlots {
  private Long stationId;

  private String name;
  private double latitude;
  private double longitude;
  private String address;
  private boolean availability;

  private StationOperator stationOperator;
  private List<ChargingSlot> slots;

  public ChargingStationWithSlots() {}

  public ChargingStationWithSlots(
      Long stationId,
      String name,
      double latitude,
      double longitude,
      String address,
      boolean availability,
      StationOperator stationOperator) {
    this.stationId = stationId;
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.address = address;
    this.availability = availability;
    this.stationOperator = stationOperator;
    this.slots = new ArrayList<>();
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

  public StationOperator getStationOperator() {
    return stationOperator;
  }

  public void setStationOperator(StationOperator stationOperator) {
    this.stationOperator = stationOperator;
  }

  public List<ChargingSlot> getSlots() {
    return slots;
  }

  public void setSlots(List<ChargingSlot> slots) {
    this.slots = slots;
  }
}
