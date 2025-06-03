package ua.tqs.smartvolt.smartvolt.dto;

public class ChargingSlotRequest {
  private double pricePerKWh;
  private String chargingSpeed;

  public ChargingSlotRequest() {}

  public ChargingSlotRequest(double pricePerKWh, String chargingSpeed) {
    this.pricePerKWh = pricePerKWh;
    this.chargingSpeed = chargingSpeed;
  }

  public double getPricePerKWh() {
    return pricePerKWh;
  }

  public void setPricePerKWh(double pricePerKWh) {
    this.pricePerKWh = pricePerKWh;
  }

  public String getChargingSpeed() {
    return chargingSpeed;
  }

  public void setChargingSpeed(String chargingSpeed) {
    this.chargingSpeed = chargingSpeed;
  }
}
