package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class StationOperator extends User {

  @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL)
  private List<ChargingStation> stations;

  public StationOperator() {}

  public StationOperator(Long userId, String name, String email, String password) {
    super(userId, name, email, password);
  }

  // Getters and setters...

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    StationOperator that = (StationOperator) o;

    return stations != null ? stations.equals(that.stations) : that.stations == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (stations != null ? stations.hashCode() : 0);
    return result;
  }
}
