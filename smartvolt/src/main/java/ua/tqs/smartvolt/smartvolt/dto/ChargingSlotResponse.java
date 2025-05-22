package ua.tqs.smartvolt.smartvolt.dto;

public class ChargingSlotResponse {
  private Long stationId;
  private double power;
  private double pricePerKWh;

  public ChargingSlotResponse() {}

  public ChargingSlotResponse(Long stationId, double power, double pricePerKWh) {
    this.stationId = stationId;
    this.power = power;
    this.pricePerKWh = pricePerKWh;
  }

  public Long getStationId() {
    return stationId;
  }

  public void setStationId(Long stationId) {
    this.stationId = stationId;
  }

  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public double getPricePerKWh() {
    return pricePerKWh;
  }

  public void setPricePerKWh(double pricePerKWh) {
    this.pricePerKWh = pricePerKWh;
  }
}
