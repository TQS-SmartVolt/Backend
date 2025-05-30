package ua.tqs.smartvolt.smartvolt.dto;

import java.util.List;

public class ChargingStationsResponse {
  private List<ChargingStationResponse> stations;

  public ChargingStationsResponse() {}

  public ChargingStationsResponse(List<ChargingStationResponse> stations) {
    this.stations = stations;
  }

  public List<ChargingStationResponse> getStations() {
    return stations;
  }

  public void setStations(List<ChargingStationResponse> stations) {
    this.stations = stations;
  }
}
