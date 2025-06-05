package ua.tqs.smartvolt.smartvolt.dto;

import java.util.Map;

public class OperatorSessionsResponse {
  private int totalSessions;
  private double averageSessionsPerMonth;
  private Map<String, Integer> monthSessions; // Key: Month, Value: Sessions

  public OperatorSessionsResponse() {}

  public OperatorSessionsResponse(
      int totalSessions, double averageSessionsPerMonth, Map<String, Integer> monthSessions) {
    this.totalSessions = totalSessions;
    this.averageSessionsPerMonth = averageSessionsPerMonth;
    this.monthSessions = monthSessions;
  }

  // Getters and setters
  public int getTotalSessions() {
    return totalSessions;
  }

  public void setTotalSessions(int totalSessions) {
    this.totalSessions = totalSessions;
  }

  public double getAverageSessionsPerMonth() {
    return averageSessionsPerMonth;
  }

  public void setAverageSessionsPerMonth(double averageSessionsPerMonth) {
    this.averageSessionsPerMonth = averageSessionsPerMonth;
  }

  public Map<String, Integer> getMonthSessions() {
    return monthSessions;
  }

  public void setMonthSessions(Map<String, Integer> monthSessions) {
    this.monthSessions = monthSessions;
  }
}
