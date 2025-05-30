package ua.tqs.smartvolt.smartvolt.dto;

import java.time.LocalDateTime;

public class BookingRequest {
  private Long slotId;
  private LocalDateTime startTime;

  public BookingRequest() {}

  public BookingRequest(Long slotId, LocalDateTime startTime) {
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
