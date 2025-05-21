package ua.tqs.smartvolt.smartvolt.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class StationOperator extends User {

  @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
  private List<ChargingStation> stations;

  public StationOperator() {}

  public StationOperator(Long userId, String name, String email, String password) {
    super(userId, name, email, password);
  }

  // Getters and setters...
}
