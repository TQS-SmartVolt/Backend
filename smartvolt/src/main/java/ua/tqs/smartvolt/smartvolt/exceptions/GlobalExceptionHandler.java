package ua.tqs.smartvolt.smartvolt.exceptions;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
  private static final String ERROR_KEY = "error";
  private static final String MESSAGE_KEY = "message";

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<Map<String, String>> handleResourceNotFoundException(
      ResourceNotFoundException ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put(ERROR_KEY, "Not Found");
    errorResponse.put(MESSAGE_KEY, ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND); // 404 Not Found
  }

  @ExceptionHandler(SlotAlreadyBookedException.class)
  public ResponseEntity<Map<String, String>> handleSlotAlreadyBookedException(
      SlotAlreadyBookedException ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put(ERROR_KEY, "Conflict"); // General error type for 409
    errorResponse.put(MESSAGE_KEY, ex.getMessage()); // Specific message from the exception
    return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT); // 409 Conflict
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<Map<String, String>> handleIllegalArgumentException(
      IllegalArgumentException ex) {
    Map<String, String> errorResponse = new HashMap<>();
    errorResponse.put(ERROR_KEY, "Bad Request");
    errorResponse.put(MESSAGE_KEY, ex.getMessage());
    return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST); // 400 Bad Request
  }

  @ExceptionHandler(InvalidRequestException.class)
  public ResponseEntity<String> handleInvalidRequestException(InvalidRequestException ex) {
    return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
  }
}
