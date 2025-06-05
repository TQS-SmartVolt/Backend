package ua.tqs.smartvolt.smartvolt.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import app.getxray.xray.junit.customjunitxml.annotations.Requirement;
import java.net.HttpURLConnection;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class ExternalServicesIT {

  @Test
  @Tag("IT-Slow")
  @Requirement("SV-281")
  void googleMapsDirectionsUrl_IsAccessible() throws Exception {
    double latitude = 40.634650;
    double longitude = -8.646357;

    String url =
        String.format(
            "https://www.google.com/maps/dir/?api=1&destination=%f,%f", latitude, longitude);
    HttpURLConnection connection =
        (HttpURLConnection) java.net.URI.create(url).toURL().openConnection();
    connection.setRequestMethod("GET");
    connection.setInstanceFollowRedirects(true);
    connection.connect();

    int statusCode = connection.getResponseCode();
    System.out.println("DEBUG: Google Maps responded with status: " + statusCode);

    assertTrue(statusCode >= 200 && statusCode < 400, "Expected a successful HTTP status code.");
  }
}
