package com.leetquery.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.http.HttpHeaders;

/**
 * CORS and Web Configuration
 * Configures Cross-Origin Resource Sharing (CORS) and other web settings
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configure CORS for the application
     * This allows controlled access from specific origins
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                // Specify allowed origins (not using *)
                .allowedOrigins(
                        "http://localhost:3000",     // Local development
                        "http://localhost:8000",     // Local development
                        "http://localhost:5173",     // Vite development
                        "http://127.0.0.1:3000",
                        "https://leetquery.com",     // Production domain
                        "https://www.leetquery.com"  // Production domain with www
                )
                // Allowed HTTP methods
                .allowedMethods("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
                // Allow credentials (cookies, auth headers)
                .allowCredentials(true)
                // Allowed headers
                .allowedHeaders(
                        HttpHeaders.AUTHORIZATION,
                        HttpHeaders.CONTENT_TYPE,
                        HttpHeaders.ACCEPT,
                        "X-Requested-With",
                        "X-API-Key"
                )
                // Exposed headers for client access
                .exposedHeaders(
                        HttpHeaders.AUTHORIZATION,
                        "X-Total-Count"  // For pagination
                )
                // Cache preflight requests for 1 hour
                .maxAge(3600);
    }
}
