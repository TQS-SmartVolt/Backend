package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.Entity;
import java.util.Set;

@Entity
public class StationOperator extends User {

  public StationOperator() {}

  public StationOperator(String name, String email, String password) {
    super(name, email, password, Set.of("ROLE_STATION_OPERATOR"));
  }

  public Long getUserId() {
    return super.getUserId();
  }

  public void setUserId(Long userId) {
    super.setUserId(userId);
  }

  public String getName() {
    return super.getName();
  }

  public void setName(String name) {
    super.setName(name);
  }

  public String getEmail() {
    return super.getEmail();
  }

  public void setEmail(String email) {
    super.setEmail(email);
  }

  public String getPassword() {
    return super.getPassword();
  }

  public void setPassword(String password) {
    super.setPassword(password);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    StationOperator that = (StationOperator) o;

    return getUserId() != null && getUserId().equals(that.getUserId());
  }

  @Override
  public int hashCode() {
    return getUserId() != null ? getUserId().hashCode() : 0;
  }
}
