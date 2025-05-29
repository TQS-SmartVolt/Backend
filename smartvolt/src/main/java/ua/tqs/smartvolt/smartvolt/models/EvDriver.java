package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;
import java.util.Set;

@Entity
public class EvDriver extends User {

  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  private List<Payment> payments;

  public EvDriver() {}

  public EvDriver(String name, String email, String password) {
    super(name, email, password, Set.of("ROLE_EV_DRIVER"));
  }

  // Getters and setters...

  @Override
  public boolean equals(Object o) { 
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    return super.equals(o);
  }

  @Override
  public int hashCode() {
    return super.hashCode();
  }

  @Override
  public String toString() {
    return "EvDriver{} " + super.toString();
  }
}
