package ua.tqs.smartvolt.smartvolt;

import java.util.Arrays;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebSecurity
public class WebConfig {

  @Value("${FRONTEND_PORT:80}")
  private String frontendPort;

  @Value("${FRONTEND_IP:localhost}")
  private String frontendIp;

  @Value("${FRONTEND_PROTOCOL:http}")
  private String frontendprotocol;

  private String testContainersHost = "host.testcontainers.internal";
  private String deploymentHost = "deti-tqs-21.ua.pt";

  private static final String DELETE = "DELETE";
  private static final String OPTIONS = "OPTIONS";

  @Bean
  public WebMvcConfigurer corsConfigurer() {
    return new WebMvcConfigurer() {
      @Override
      public void addCorsMappings(CorsRegistry registry) {
        registry
            .addMapping("/api/**")
            .allowedOrigins(
                frontendprotocol + "://" + frontendIp + ":" + frontendPort,
                frontendprotocol + "://" + testContainersHost + ":" + frontendPort,
                frontendprotocol + "://" + deploymentHost + ":" + frontendPort)
            .allowedMethods("GET", "POST", "PUT", DELETE, OPTIONS)
            .allowedHeaders("*")
            .allowCredentials(true);
      }
    };
  }

  @Bean
  @Order(0)
  public SecurityFilterChain apiFilterChain(HttpSecurity http) throws Exception {
    http.securityMatcher("/api/**")
        .cors(cors -> cors.configurationSource(apiConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  @Order(1)
  public SecurityFilterChain myOtherFilterChain(HttpSecurity http) throws Exception {
    http.cors(cors -> cors.configurationSource(myWebsiteConfigurationSource()))
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
    return http.build();
  }

  @Bean
  UrlBasedCorsConfigurationSource apiConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList(
            frontendprotocol + "://" + frontendIp + ":" + frontendPort,
            frontendprotocol + "://" + testContainersHost + ":" + frontendPort,
            frontendprotocol + "://" + deploymentHost + ":" + frontendPort));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", DELETE, OPTIONS));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }

  @Bean
  UrlBasedCorsConfigurationSource myWebsiteConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(
        Arrays.asList(
            frontendprotocol + "://" + frontendIp + ":" + frontendPort,
            frontendprotocol + "://" + testContainersHost + ":" + frontendPort,
            frontendprotocol + "://" + deploymentHost + ":" + frontendPort));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", DELETE, OPTIONS));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type"));
    configuration.setAllowCredentials(true);
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
