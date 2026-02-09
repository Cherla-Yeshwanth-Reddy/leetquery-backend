package com.leetquery.backend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AdminController {
    
    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @GetMapping("/admin/checkRole")
    public String checkRole(@RequestHeader("Authorization") String authHeader) {
        try {
            // Extract token from "Bearer <token>"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return "USER";
            }
            
            String token = authHeader.substring(7);
            
            // Decode JWT (base64 decode the payload part)
            // JWT format: header.payload.signature
            String[] parts = token.split("\\.");
            if (parts.length < 2) {
                return "USER";
            }
            
            // Decode the payload (second part)
            String payload = new String(Base64.getUrlDecoder().decode(parts[1]));
            
            // Parse JSON to extract "sub" field
            JsonNode jsonNode = objectMapper.readTree(payload);
            String userId = jsonNode.get("sub").asText();
            
            if (userId == null || userId.isEmpty()) {
                return "USER";
            }
            
            // Query user_roles table
            String sql = "SELECT role FROM user_roles WHERE user_id = ?";
            
            try {
                String role = jdbcTemplate.queryForObject(sql, String.class, userId);
                
                if ("ADMIN".equals(role)) {
                    return "ADMIN";
                } else {
                    return "USER";
                }
                
            } catch (EmptyResultDataAccessException e) {
                // No row found for this user
                return "USER";
            }
            
        } catch (Exception e) {
            // Any error in decoding or parsing
            return "USER";
        }
    }
}
