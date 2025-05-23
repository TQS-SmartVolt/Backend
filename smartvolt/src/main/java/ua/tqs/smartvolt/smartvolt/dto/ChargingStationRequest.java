package ua.tqs.smartvolt.smartvolt.dto;

public class ChargingStationRequest {
  private String name;
  private double latitude;
  private double longitude;
  private Long operatorId;

  public ChargingStationRequest() {}

  public ChargingStationRequest(String name, double latitude, double longitude, Long operatorId) {
    this.name = name;
    this.latitude = latitude;
    this.longitude = longitude;
    this.operatorId = operatorId;
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

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }
}
