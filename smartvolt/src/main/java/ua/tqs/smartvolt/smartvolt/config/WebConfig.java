package ua.tqs.smartvolt.smartvolt.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
  @Override
  public void addCorsMappings(CorsRegistry registry) {
    registry
        .addMapping("/**") // Allow CORS for all endpoints
        .allowedOrigins("http://localhost:5173") // Origin of frontend
        .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS") // Allowed HTTP methods
        .allowedHeaders("*") // Allow all headers
        .allowCredentials(false); // Allow credentials (cookies, authorization headers, etc.)
  }
}
