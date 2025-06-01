package ua.tqs.smartvolt.smartvolt.exceptions;

public class SlotAlreadyBookedException extends RuntimeException {
  public SlotAlreadyBookedException(String message) {
    super(message);
  }
}
