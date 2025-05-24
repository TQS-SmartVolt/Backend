package ua.tqs.smartvolt.smartvolt.utils.validation;

import java.util.List;
import java.util.Map;

public class Validator {

  private Validator() {
    // Private constructor to prevent instantiation
  }

  public static void checkNotNull(Object object, String message) {
    if (object == null) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkNotEmpty(String string, String message) {
    if (string == null || string.trim().isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkNotEmpty(List<?> list, String message) {
    if (list == null || list.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkNotEmpty(Map<?, ?> map, String message) {
    if (map == null || map.isEmpty()) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkCoordinates(Double latitude, Double longitude, String message) {
    if (latitude == null || longitude == null) {
      throw new IllegalArgumentException(message);
    }
    if ((latitude < -90 || latitude > 90) || (longitude < -180 || longitude > 180)) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkUrl(String url, String message) {
    if (url == null || !url.matches("^(http|https)://.*")) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkGreaterThan(Integer value, Integer threshold, String message) {
    if (value == null || value <= threshold) {
      throw new IllegalArgumentException(message);
    }
  }

  public static void checkLessThan(Integer value, Integer threshold, String message) {
    if (value == null || value >= threshold) {
      throw new IllegalArgumentException(message);
    }
  }
}
