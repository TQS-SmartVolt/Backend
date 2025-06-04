package ua.tqs.smartvolt.smartvolt.dto;

import java.time.LocalDateTime;

public class ChargingHistoryResponse {

  private LocalDateTime startTime; // atribute of Booking
  private String chargingStationName; // from ChargingSlot atribute of Booking
  private String chargingSpeed; // from ChargingSlot atribute of Booking
  private double power; // from ChargingSlot atribute of Booking
  private double energyDelivered; // calculated used a utils function that uses power and factor 0.5
  private double pricePerKWh; // from ChargingSlot atribute of Booking
  private double cost; // atribute of Booking

  public ChargingHistoryResponse() {}

  public ChargingHistoryResponse(
      LocalDateTime startTime,
      String chargingStationName,
      String chargingSpeed,
      double power,
      double energyDelivered,
      double pricePerKWh,
      double cost) {
    this.startTime = startTime;
    this.chargingStationName = chargingStationName;
    this.chargingSpeed = chargingSpeed;
    this.power = power;
    this.energyDelivered = energyDelivered;
    this.pricePerKWh = pricePerKWh;
    this.cost = cost;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public String getChargingStationName() {
    return chargingStationName;
  }

  public void setChargingStationName(String chargingStationName) {
    this.chargingStationName = chargingStationName;
  }

  public String getChargingSpeed() {
    return chargingSpeed;
  }

  public void setChargingSpeed(String chargingSpeed) {
    this.chargingSpeed = chargingSpeed;
  }

  public double getPower() {
    return power;
  }

  public void setPower(double power) {
    this.power = power;
  }

  public double getEnergyDelivered() {
    return energyDelivered;
  }

  public void setEnergyDelivered(double energyDelivered) {
    this.energyDelivered = energyDelivered;
  }

  public double getPricePerKWh() {
    return pricePerKWh;
  }

  public void setPricePerKWh(double pricePerKWh) {
    this.pricePerKWh = pricePerKWh;
  }

  public double getCost() {
    return cost;
  }

  public void setCost(double cost) {
    this.cost = cost;
  }
}
