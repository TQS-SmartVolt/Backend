package ua.tqs.smartvolt.smartvolt.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import java.util.List;

@Entity
public class EvDriver extends User {


  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  private List<Payment> payments;

  public EvDriver() {}

  public EvDriver(Long userId, String name, String email, String password) {
    super(userId, name, email, password);
  }

  // Getters and setters...

  @Override
  public boolean equals(Object o) { 
    if (this == o) return true;
    if (!(o instanceof EvDriver)) return false;
    if (!super.equals(o)) return false;
    EvDriver evDriver = (EvDriver) o;
    return getUserId().equals(evDriver.getUserId());
  }

  @Override
  public int hashCode() {
    return 31 * super.hashCode() + (getUserId() != null ? getUserId().hashCode() : 0);
  }

  @Override
  public String toString() {
    return "EvDriver{" +
        "userId=" + getUserId() +
        ", name='" + getName() + '\'' +
        ", email='" + getEmail() + '\'' +
        ", password='" + getPassword() + '\'' +
        '}';
  }
}
