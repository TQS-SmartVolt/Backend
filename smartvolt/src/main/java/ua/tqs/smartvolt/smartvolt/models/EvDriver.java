package ua.tqs.smartvolt.smartvolt.models;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class EvDriver extends User {

  private String vehiclePlate;

  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  private List<Booking> bookings;

  @OneToMany(mappedBy = "driver", cascade = CascadeType.ALL)
  private List<Payment> payments;

  public EvDriver() {}

  public EvDriver(Long userId, String name, String email, String password, String vehiclePlate) {
    super(userId, name, email, password);
    this.vehiclePlate = vehiclePlate;
  }

  // Getters and setters...

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    if (!super.equals(o)) return false;

    EvDriver evDriver = (EvDriver) o;

    return vehiclePlate != null
        ? vehiclePlate.equals(evDriver.vehiclePlate)
        : evDriver.vehiclePlate == null;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + (vehiclePlate != null ? vehiclePlate.hashCode() : 0);
    return result;
  }

  @Override
  public String toString() {
    return "EvDriver{" + "vehiclePlate='" + vehiclePlate + '\'' + "} " + super.toString();
  }
}
