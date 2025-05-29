package ua.tqs.smartvolt.smartvolt.services;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import ua.tqs.smartvolt.smartvolt.dto.auth.AuthRequest;
import ua.tqs.smartvolt.smartvolt.dto.auth.AuthResponse;
import ua.tqs.smartvolt.smartvolt.exceptions.ResourceNotFoundException;
import ua.tqs.smartvolt.smartvolt.models.User;
import ua.tqs.smartvolt.smartvolt.repositories.EvDriverRepository;
import ua.tqs.smartvolt.smartvolt.models.EvDriver;
import ua.tqs.smartvolt.smartvolt.security.JwtTokenUtil;

@Service
public class AuthService {

    private final PasswordEncoder passwordEncoder;
    private JwtTokenUtil jwtTokenUtil;
    private StationOperatorService stationOperatorService;
    private EvDriverService evDriverService;
    private EvDriverRepository evDriverRepository;

    public AuthService(PasswordEncoder passwordEncoder, JwtTokenUtil jwtTokenUtil,
            StationOperatorService stationOperatorService,
            EvDriverService evDriverService,
            EvDriverRepository evDriverRepository) {
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenUtil = jwtTokenUtil;
        this.stationOperatorService = stationOperatorService;
        this.evDriverService = evDriverService;
        this.evDriverRepository = evDriverRepository;
    }

    public User createUser(UserSignUp userSignUp) throws ResourceNotFoundException {
        // type: Ev Driver always!
        Optional<EvDriver> existingUser = evDriverService.getEvDriverByEmail(userSignUp.getEmail());
        if (existingUser.isPresent()) {
            throw new ResourceNotFoundException("User with email " + userSignUp.getEmail() + " already exists");
        }

        EvDriver user = new EvDriver(userSignUp.getName(), userSignUp.getEmail(),
                passwordEncoder.encode(userSignUp.getPassword()));
        evDriverRepository.save(user);
        
        return user;
    }

    public AuthResponse signIn(AuthRequest authRequest) throws ResourceNotFoundException {
        String currentEmail = authRequest.getEmail();
        User user = null;

        user = evDriverService.getEvDriverByEmail(currentEmail)
                .orElse(null);

        if (user == null) {
            user = stationOperatorService.getStationOperatorByEmail(currentEmail)
                    .orElse(null);
        }

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }


        if (!passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
            throw new ResourceNotFoundException("Invalid password");
        }

        String token = jwtTokenUtil.generateToken(user.getEmail(), user.getRoles());

        AuthResponse authResponse = new AuthResponse();
        authResponse.setToken(token);
        authResponse.setUserId(user.getUserId());
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());
        authResponse.setRoles(user.getRoles());

        return authResponse;
    }

    public AuthResponse me() throws ResourceNotFoundException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentEmail = authentication.getName();

        User user = null;

        user = evDriverService.getEvDriverByEmail(currentEmail)
                .orElse(null);

        if (user == null) {
            user = stationOperatorService.getStationOperatorByEmail(currentEmail)
                    .orElse(null);
        }

        if (user == null) {
            throw new ResourceNotFoundException("User not found");
        }

        AuthResponse authResponse = new AuthResponse();
        authResponse.setUserId(user.getUserId());
        authResponse.setName(user.getName());
        authResponse.setEmail(user.getEmail());
        authResponse.setRoles(user.getRoles());

        return authResponse;
    }

}
