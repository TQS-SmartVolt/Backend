package ua.tqs.smartvolt.smartvolt.models;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

  @ManyToOne private EvDriver driver;

  @OneToOne private Booking booking;

  public Payment() {}

  public Payment(EvDriver driver, Booking booking) {
    this.driver = driver;
    this.booking = booking;
  }

  // Getters and setters...
}
