package ua.tqs.smartvolt.smartvolt.controllers;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ua.tqs.smartvolt.smartvolt.dto.auth.AuthRequest;
import ua.tqs.smartvolt.smartvolt.dto.auth.AuthResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.User;
import ua.tqs.smartvolt.smartvolt.services.AuthService;
import ua.tqs.smartvolt.smartvolt.services.UserSignUp;

@RestController
@RequestMapping("/api/v1")
public class AuthController {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @GetMapping("/auth/me")
  @Operation(
      summary = "Get authenticated user details",
      description =
          "Retrieves the details of the currently authenticated user using the token from the cookie.")
  public AuthResponse api_auth_me() throws ResourceNotFoundException {
    return authService.me(); // with token from cookie
  }

  @PostMapping("/auth/sign-in")
  @Operation(
      summary = "Sign in",
      description =
          "Allows a user to sign in using their credentials and receive an authentication token.")
  public AuthResponse api_sign_in(@RequestBody AuthRequest authRequest)
      throws ResourceNotFoundException {
    return authService.signIn(authRequest);
  }

  @PostMapping("/auth/sign-up")
  @Operation(
      summary = "Sign up",
      description =
          "Allows a new user to sign up by providing their details and creating an account.")
  public User api_create_user(@RequestBody UserSignUp userSignUp) throws ResourceNotFoundException {
    return authService.createUser(userSignUp);
  }
}
