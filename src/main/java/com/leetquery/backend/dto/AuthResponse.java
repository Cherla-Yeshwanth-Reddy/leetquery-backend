package com.leetquery.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Authentication response DTO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    
    @JsonProperty("access_token")
    private String accessToken;
    
    @JsonProperty("refresh_token")
    private String refreshToken;
    
    @JsonProperty("token_type")
    private String tokenType = "Bearer";
    
    @JsonProperty("expires_in")
    private long expiresIn;
    
    @JsonProperty("user")
    private UserInfo user;
    
    /**
     * Constructor with username and email (convenience method)
     */
    public AuthResponse(String accessToken, String refreshToken, String tokenType, long expiresIn, String username, String email) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = new UserInfo(null, email, username, null, null);
    }
    
    /**
     * User info included in auth response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UserInfo {
        private Long id;
        private String email;
        private String username;
        private String firstName;
        private String lastName;
    }
}
