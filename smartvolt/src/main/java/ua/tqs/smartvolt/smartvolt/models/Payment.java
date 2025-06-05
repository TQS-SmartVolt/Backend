package ua.tqs.smartvolt.smartvolt.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;

@Entity
public class Payment {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long paymentId;

  @ManyToOne
  @JoinColumn(name = "driver_id")
  private EvDriver driver;

  @OneToOne
  @JoinColumn(name = "booking_id", referencedColumnName = "booking_id")
  @JsonBackReference
  private Booking booking;

  public Payment() {}

  public Payment(EvDriver driver, Booking booking) {
    this.driver = driver;
    this.booking = booking;
  }

  public Long getPaymentId() {
    return paymentId;
  }

  public void setPaymentId(Long paymentId) {
    this.paymentId = paymentId;
  }

  public EvDriver getDriver() {
    return driver;
  }

  public void setDriver(EvDriver driver) {
    this.driver = driver;
  }

  public Booking getBooking() {
    return booking;
  }

  public void setBooking(Booking booking) {
    this.booking = booking;
  }

  @Override
  public String toString() {
    return "Payment{"
        + "paymentId="
        + paymentId
        + ", driver="
        + driver
        + ", booking="
        + booking
        + '}';
  }
}
