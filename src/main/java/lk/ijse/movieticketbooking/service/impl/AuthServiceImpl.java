package lk.ijse.movieticketbooking.service.impl;

import lk.ijse.movieticketbooking.dto.AuthResponse;
import lk.ijse.movieticketbooking.dto.SigninRequest;
import lk.ijse.movieticketbooking.dto.SignupRequest;
import lk.ijse.movieticketbooking.entity.Role;
import lk.ijse.movieticketbooking.entity.User;
import lk.ijse.movieticketbooking.exception.BadRequestException;
import lk.ijse.movieticketbooking.repository.UserRepository;
import lk.ijse.movieticketbooking.security.JwtUtil;
import lk.ijse.movieticketbooking.security.UserDetailsImpl;
import lk.ijse.movieticketbooking.service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder,
                           AuthenticationManager authenticationManager, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public AuthResponse signup(SignupRequest request) {
        log.info("Signup request received for email: {}", request.getEmail());

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Signup failed, email already registered: {}", request.getEmail());
            throw new BadRequestException("Email already registered");
        }

        User user = new User();
        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.CUSTOMER);
        userRepository.save(user);

        String token = jwtUtil.generateToken(new UserDetailsImpl(user));

        log.info("New customer registered successfully: {}", user.getEmail());

        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }

    @Override
    public AuthResponse signin(SigninRequest request) {
        log.info("Signin request received for email: {}", request.getEmail());

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));
        } catch (BadCredentialsException e) {
            log.warn("Signin failed, wrong email or password: {}", request.getEmail());
            throw e;
        }

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtil.generateToken(userDetails);

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        log.info("User signed in successfully: {} (role: {})", user.getEmail(), user.getRole().name());

        return new AuthResponse(token, user.getEmail(), user.getFullName(), user.getRole().name());
    }
}