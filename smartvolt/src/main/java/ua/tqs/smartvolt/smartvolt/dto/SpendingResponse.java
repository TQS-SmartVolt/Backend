package ua.tqs.smartvolt.smartvolt.dto;

import java.util.List;

public class SpendingResponse {
  private List<Double> spendingPerMonth;

  public SpendingResponse() {}

  public SpendingResponse(List<Double> spendingPerMonth) {
    this.spendingPerMonth = spendingPerMonth;
  }

  public List<Double> getSpendingPerMonth() {
    return spendingPerMonth;
  }

  public void setSpendingPerMonth(List<Double> spendingPerMonth) {
    this.spendingPerMonth = spendingPerMonth;
  }
}
