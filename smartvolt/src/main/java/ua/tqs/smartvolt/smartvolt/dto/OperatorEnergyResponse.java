package ua.tqs.smartvolt.smartvolt.dto;

import java.util.Map;

public class OperatorEnergyResponse {
  private double totalEnergy;
  private double averageEnergyPerMonth;
  private Map<String, Double> monthEnergy; // Key: Month, Value: Energy in kWh

  public OperatorEnergyResponse() {}

  public OperatorEnergyResponse(
      double totalEnergy, double averageEnergyPerMonth, Map<String, Double> monthEnergy) {
    this.totalEnergy = totalEnergy;
    this.averageEnergyPerMonth = averageEnergyPerMonth;
    this.monthEnergy = monthEnergy;
  }

  // Getters and setters
  public double getTotalEnergy() {
    return totalEnergy;
  }

  public void setTotalEnergy(double totalEnergy) {
    this.totalEnergy = totalEnergy;
  }

  public double getAverageEnergyPerMonth() {
    return averageEnergyPerMonth;
  }

  public void setAverageEnergyPerMonth(double averageEnergyPerMonth) {
    this.averageEnergyPerMonth = averageEnergyPerMonth;
  }

  public Map<String, Double> getMonthEnergy() {
    return monthEnergy;
  }

  public void setMonthEnergy(Map<String, Double> monthEnergy) {
    this.monthEnergy = monthEnergy;
  }
}
