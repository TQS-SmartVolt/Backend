package ua.tqs.smartvolt.smartvolt.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ChargingSlotsResponse {

  public static class SlotAvailability {
    private Long slotId;
    private LocalDateTime startTime;

    public SlotAvailability() {}

    public SlotAvailability(Long slotId, LocalDateTime startTime) {
      this.slotId = slotId;
      this.startTime = startTime;
    }

    public Long getSlotId() {
      return slotId;
    }

    public void setSlotId(Long slotId) {
      this.slotId = slotId;
    }

    public LocalDateTime getStartTime() {
      return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
      this.startTime = startTime;
    }
  }

  private List<SlotAvailability> availableSlotMapping;
  private double pricePerKWh;

  public ChargingSlotsResponse() {}

  public ChargingSlotsResponse(List<SlotAvailability> availableSlotMapping, double pricePerKWh) {
    this.availableSlotMapping = availableSlotMapping;
    this.pricePerKWh = pricePerKWh;
  }

  public List<SlotAvailability> getAvailableSlotMapping() {
    return availableSlotMapping;
  }

  public void setAvailableSlotMapping(List<SlotAvailability> availableSlotMapping) {
    this.availableSlotMapping = availableSlotMapping;
  }

  public double getPricePerKWh() {
    return pricePerKWh;
  }

  public void setPricePerKWh(double pricePerKWh) {
    this.pricePerKWh = pricePerKWh;
  }
}
