package ua.tqs.smartvolt.smartvolt.utils.validation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

class ValidatorTest {

  @Test
  @Tag("UnitTest")
  void checkNotNull_shouldThrowException_whenObjectIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkNotNull(null, "Object cannot be null"));
  }

  @Test
  @Tag("UnitTest")
  void checkNotNull_shouldNotThrowException_whenObjectIsNotNull() {
    assertDoesNotThrow(() -> Validator.checkNotNull("Test", "Object cannot be null"));
  }

  @Test
  @Tag("UnitTest")
  void checkNotEmptyString_shouldThrowException_whenStringIsNullOrEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkNotEmpty((String) null, "String cannot be empty"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkNotEmpty("", "String cannot be empty"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkNotEmpty("   ", "String cannot be empty"));
  }

  @Test
  @Tag("UnitTest")
  void checkNotEmptyString_shouldNotThrowException_whenStringIsNotEmpty() {
    assertDoesNotThrow(() -> Validator.checkNotEmpty("Test", "String cannot be empty"));
  }

  @Test
  @Tag("UnitTest")
  void checkNotEmptyList_shouldThrowException_whenListIsNullOrEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkNotEmpty((List<?>) null, "List cannot be empty"));
    Executable executable = () -> Validator.checkNotEmpty(List.of(), "List cannot be empty");
    assertThrows(IllegalArgumentException.class, executable);
  }

  @Test
  @Tag("UnitTest")
  void checkNotEmptyList_shouldNotThrowException_whenListIsNotEmpty() {
    assertDoesNotThrow(() -> Validator.checkNotEmpty(List.of("Item"), "List cannot be empty"));
  }

  @Test
  @Tag("UnitTest")
  void checkNotEmptyMap_shouldThrowException_whenMapIsNullOrEmpty() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkNotEmpty((Map<?, ?>) null, "Map cannot be empty"));
    Executable executable = () -> Validator.checkNotEmpty(Map.of(), "Map cannot be empty");
    assertThrows(IllegalArgumentException.class, executable);
  }

  @Test
  @Tag("UnitTest")
  void checkNotEmptyMap_shouldNotThrowException_whenMapIsNotEmpty() {
    assertDoesNotThrow(
        () -> Validator.checkNotEmpty(Map.of("Key", "Value"), "Map cannot be empty"));
  }

  @Test
  @Tag("UnitTest")
  void checkCoordinates_shouldThrowException_whenCoordinatesAreInvalid() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkCoordinates(-91.0, 0.0, "Invalid coordinates"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkCoordinates(91.0, 0.0, "Invalid coordinates"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkCoordinates(0.0, -181.0, "Invalid coordinates"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkCoordinates(0.0, 181.0, "Invalid coordinates"));
  }

  @Test
  @Tag("UnitTest")
  void checkCoordinates_shouldNotThrowException_whenCoordinatesAreValid() {
    assertDoesNotThrow(() -> Validator.checkCoordinates(0.0, 0.0, "Invalid coordinates"));
    assertDoesNotThrow(() -> Validator.checkCoordinates(45.0, 90.0, "Invalid coordinates"));
  }

  // nulls
  @Test
  @Tag("UnitTest")
  void checkCoordinates_shouldThrowException_whenLatitudeIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkCoordinates(null, 0.0, "Invalid coordinates"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkCoordinates(0.0, null, "Invalid coordinates"));
  }

  @Test
  @Tag("UnitTest")
  void checkUrl_shouldThrowException_whenUrlIsInvalid() {
    assertThrows(IllegalArgumentException.class, () -> Validator.checkUrl(null, "Invalid URL"));
    assertThrows(
        IllegalArgumentException.class, () -> Validator.checkUrl("invalid-url", "Invalid URL"));
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkUrl("ftp://example.com", "Invalid URL"));
  }

  @Test
  @Tag("UnitTest")
  void checkUrl_shouldNotThrowException_whenUrlIsValid() {
    assertDoesNotThrow(() -> Validator.checkUrl("http://example.com", "Invalid URL"));
    assertDoesNotThrow(() -> Validator.checkUrl("https://example.com", "Invalid URL"));
  }

  @Test
  @Tag("UnitTest")
  void checkGreaterThan_shouldThrowException_whenValueIsLessThanThreshold() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkGreaterThan(5, 10, "Value must not be greater than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkGreaterThan_shouldNotThrowException_whenValueIsGraterThanThreshold() {
    assertDoesNotThrow(
        () -> Validator.checkGreaterThan(10, 5, "Value must not be greater than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkGreaterThan_shouldThrowException_whenValueIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkGreaterThan(null, 5, "Value must not be greater than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkLessThan_shouldThrowException_whenValueIsEqualToExpected() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkGreaterThan(5, 5, "Value must not be greater than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkLessThan_shouldThrowException_whenValueIsGreaterThanThreshold() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkLessThan(10, 5, "Value must not be less than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkLessThan_shouldNotThrowException_whenValueIsLessThanThreshold() {
    assertDoesNotThrow(
        () -> Validator.checkLessThan(5, 10, "Value must not be less than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkLessThan_shouldThrowException_whenValueIsEqualToThreshold() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkLessThan(10, 10, "Value must not be less than threshold"));
  }

  @Test
  @Tag("UnitTest")
  void checkLessThan_shouldThrowException_whenValueIsNull() {
    assertThrows(
        IllegalArgumentException.class,
        () -> Validator.checkLessThan(null, 5, "Value must not be less than threshold"));
  }
}
