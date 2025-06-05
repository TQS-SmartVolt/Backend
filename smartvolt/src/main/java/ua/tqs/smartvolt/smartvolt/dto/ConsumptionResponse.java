package ua.tqs.smartvolt.smartvolt.dto;

import java.util.List;

public class ConsumptionResponse {
  private List<Double> consumptionPerMonth;

  public ConsumptionResponse() {}

  public ConsumptionResponse(List<Double> consumptionPerMonth) {
    this.consumptionPerMonth = consumptionPerMonth;
  }

  public List<Double> getConsumptionPerMonth() {
    return consumptionPerMonth;
  }

  public void setConsumptionPerMonth(List<Double> consumptionPerMonth) {
    this.consumptionPerMonth = consumptionPerMonth;
  }
}
