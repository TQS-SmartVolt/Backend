package ua.tqs.smartvolt.smartvolt.dto;

public class UserInfoResponse {

  private String name;
  private String email;
  private double totalEnergyConsumed;
  private double totalMoneySpent;

  public UserInfoResponse() {}

  public UserInfoResponse(
      String name, String email, double totalEnergyConsumed, double totalMoneySpent) {
    this.name = name;
    this.email = email;
    this.totalEnergyConsumed = totalEnergyConsumed;
    this.totalMoneySpent = totalMoneySpent;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public double getTotalEnergyConsumed() {
    return totalEnergyConsumed;
  }

  public void setTotalEnergyConsumed(double totalEnergyConsumed) {
    this.totalEnergyConsumed = totalEnergyConsumed;
  }

  public double getTotalMoneySpent() {
    return totalMoneySpent;
  }

  public void setTotalMoneySpent(double totalMoneySpent) {
    this.totalMoneySpent = totalMoneySpent;
  }
}
