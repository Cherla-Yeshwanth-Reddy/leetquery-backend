package com.leetquery.backend.service;

import com.leetquery.backend.dto.AuthResponse;
import com.leetquery.backend.dto.LoginRequest;
import com.leetquery.backend.dto.RegisterRequest;
import com.leetquery.backend.exception.ValidationException;
import com.leetquery.backend.exception.UnauthorizedException;
import com.leetquery.backend.model.User;
import com.leetquery.backend.repository.UserRepository;
import com.leetquery.backend.security.JwtTokenProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Authentication Service
 * Handles user registration and login operations
 */
@Service
public class AuthenticationService {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationService.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Register a new user
     * Validates input and creates a new user account
     */
    @Transactional
    public AuthResponse register(RegisterRequest registerRequest) {
        logger.info("Attempting to register user: {}", registerRequest.getEmail());
        
        // Validate input
        if (registerRequest.getEmail() == null || registerRequest.getEmail().trim().isEmpty()) {
            throw new ValidationException("Email cannot be empty");
        }
        
        if (registerRequest.getUsername() == null || registerRequest.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username cannot be empty");
        }
        
        if (registerRequest.getPassword() == null || registerRequest.getPassword().length() < 6) {
            throw new ValidationException("Password must be at least 6 characters long");
        }
        
        // Validate email format
        if (!isValidEmail(registerRequest.getEmail())) {
            throw new ValidationException("Invalid email format");
        }
        
        // Check if user already exists
        if (userRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new ValidationException("Username already exists");
        }
        
        if (userRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new ValidationException("Email already registered");
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        user.setEnabled(true);
        user.setAccountNotLocked(true);
        user.setAccountNotExpired(true);
        user.setCredentialsNotExpired(true);
        
        User savedUser = userRepository.save(user);
        logger.info("User registered successfully: {}", savedUser.getUsername());
        
        // Generate JWT tokens
        String accessToken = jwtTokenProvider.generateToken(savedUser.getUsername());
        String refreshToken = jwtTokenProvider.generateRefreshToken(savedUser.getUsername());
        
        return new AuthResponse(
                accessToken,
                refreshToken,
                "Bearer",
                jwtTokenProvider.getExpirationMs(),
                savedUser.getUsername(),
                savedUser.getEmail()
        );
    }

    /**
     * Login user with username/email and password
     * Returns JWT tokens upon successful authentication
     */
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest loginRequest) {
        logger.info("Attempting to login: {}", loginRequest.getUsername());
        
        // Validate input
        if (loginRequest.getUsername() == null || loginRequest.getUsername().trim().isEmpty()) {
            throw new ValidationException("Username or email is required");
        }
        
        if (loginRequest.getPassword() == null || loginRequest.getPassword().isEmpty()) {
            throw new ValidationException("Password is required");
        }
        
        try {
            // Authenticate user
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );
            
            // Get authenticated user
            String username = authentication.getName();
            Optional<User> userOptional = userRepository.findByUsername(username)
                    .or(() -> userRepository.findByEmail(username));
            
            if (userOptional.isEmpty()) {
                throw new UnauthorizedException("User not found");
            }
            
            User user = userOptional.get();
            
            // Generate JWT tokens
            String accessToken = jwtTokenProvider.generateToken(username);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);
            
            logger.info("User logged in successfully: {}", username);
            
            return new AuthResponse(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtTokenProvider.getExpirationMs(),
                    user.getUsername(),
                    user.getEmail()
            );
            
        } catch (AuthenticationException e) {
            logger.warn("Authentication failed for user: {}", loginRequest.getUsername());
            throw new UnauthorizedException("Invalid username or password");
        }
    }

    /**
     * Refresh JWT token
     * Validates refresh token and returns new access token
     */
    public AuthResponse refreshToken(String refreshToken) {
        logger.debug("Attempting to refresh token");
        
        if (refreshToken == null || !jwtTokenProvider.validateRefreshToken(refreshToken)) {
            throw new UnauthorizedException("Invalid or expired refresh token");
        }
        
        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);
        Optional<User> userOptional = userRepository.findByUsername(username);
        
        if (userOptional.isEmpty()) {
            throw new UnauthorizedException("User not found");
        }
        
        User user = userOptional.get();
        String newAccessToken = jwtTokenProvider.generateToken(username);
        
        return new AuthResponse(
                newAccessToken,
                refreshToken,
                "Bearer",
                jwtTokenProvider.getExpirationMs(),
                user.getUsername(),
                user.getEmail()
        );
    }

    /**
     * Validate email format
     */
    private boolean isValidEmail(String email) {
        return email != null && email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$");
    }
}
