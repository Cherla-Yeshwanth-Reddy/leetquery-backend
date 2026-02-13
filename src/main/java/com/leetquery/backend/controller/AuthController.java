package com.leetquery.backend.controller;

import com.leetquery.backend.dto.AuthResponse;
import com.leetquery.backend.dto.LoginRequest;
import com.leetquery.backend.dto.RegisterRequest;
import com.leetquery.backend.service.AuthenticationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

/**
 * Authentication Controller
 * Handles user registration, login, and token refresh endpoints
 */
@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE})
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    @Autowired
    private AuthenticationService authenticationService;

    /**
     * User Registration Endpoint
     * POST /auth/register
     * Request body: RegisterRequest (username, email, password)
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        try {
            logger.info("Register request for email: {}", registerRequest.getEmail());
            AuthResponse authResponse = authenticationService.register(registerRequest);
            return ResponseEntity.status(HttpStatus.CREATED).body(authResponse);
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.badRequest().body(new ErrorMessageResponse(e.getMessage()));
        }
    }

    /**
     * User Login Endpoint
     * POST /auth/login
     * Request body: LoginRequest (username/email and password)
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            logger.info("Login request for: {}", loginRequest.getUsername());
            AuthResponse authResponse = authenticationService.login(loginRequest);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.warn("Login failed for user: {}", loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessageResponse(e.getMessage()));
        }
    }

    /**
     * Refresh Token Endpoint
     * POST /auth/refresh
     * Bearer token in Authorization header
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refreshToken(@RequestHeader("Authorization") String bearerToken) {
        try {
            // Extract token from "Bearer <token>" format
            String token = null;
            if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
                token = bearerToken.substring(7);
            }
            
            if (token == null) {
                return ResponseEntity.badRequest()
                        .body(new ErrorMessageResponse("Missing or invalid Authorization header"));
            }
            
            logger.debug("Refreshing token");
            AuthResponse authResponse = authenticationService.refreshToken(token);
            return ResponseEntity.ok(authResponse);
        } catch (Exception e) {
            logger.warn("Token refresh failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessageResponse(e.getMessage()));
        }
    }

    /**
     * Logout Endpoint (client-side only)
     * POST /auth/logout
     * Instructs client to discard JWT token
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        logger.info("Logout request");
        return ResponseEntity.ok(new MessageResponse("Logged out successfully. Please discard your token."));
    }

    /**
     * Get Current User Info Endpoint
     * GET /auth/me
     * Requires valid JWT token in Authorization header
     */
    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser() {
        try {
            logger.debug("Get current user info request");
            // User info will be extracted from Authentication principal in SecurityContext
            return ResponseEntity.ok(new MessageResponse("User authenticated successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorMessageResponse("Not authenticated"));
        }
    }

    /**
     * Inner class for error message response
     */
    public static class ErrorMessageResponse {
        public String error;
        public String message;

        public ErrorMessageResponse(String message) {
            this.error = "Authentication Error";
            this.message = message;
        }

        public String getError() { return error; }
        public String getMessage() { return message; }
    }

    /**
     * Inner class for generic message response
     */
    public static class MessageResponse {
        public String message;

        public MessageResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
    }
}
