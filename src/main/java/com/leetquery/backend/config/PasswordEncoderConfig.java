package com.leetquery.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Password Encoder Configuration
 * Configures BCrypt password encoding for the application
 */
@Configuration
public class PasswordEncoderConfig {
    
    /**
     * Create BCryptPasswordEncoder bean
     * Strength: 12 (balance between security and performance)
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}
