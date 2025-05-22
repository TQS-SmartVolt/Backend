package ua.tqs.smartvolt.smartvolt.dto;

public class ChargingStationRequest {
  private String name;
  private String location;
  private Long operatorId;

  public ChargingStationRequest() {}

  public ChargingStationRequest(String name, String location, Long operatorId) {
    this.name = name;
    this.location = location;
    this.operatorId = operatorId;
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

  public Long getOperatorId() {
    return operatorId;
  }

  public void setOperatorId(Long operatorId) {
    this.operatorId = operatorId;
  }
}
