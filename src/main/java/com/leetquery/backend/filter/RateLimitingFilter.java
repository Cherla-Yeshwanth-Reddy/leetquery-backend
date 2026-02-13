package com.leetquery.backend.filter;

import com.leetquery.backend.config.RateLimitConfig;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Rate Limiting Filter using Bucket4j
 * Limits the number of requests per user/IP address
 */
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RateLimitingFilter.class);
    
    // Skip rate limiting for health checks and static resources
    private static final String[] EXCLUDED_PATHS = {
            "/health",
            "/actuator",
            "/swagger",
            "/api-docs",
            ".js",
            ".css",
            ".png",
            ".jpg",
            ".gif",
            ".ico"
    };

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // Skip rate limiting for excluded paths
        if (shouldSkipRateLimit(request)) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Get client identifier
        String clientKey = getClientKey(request);
        
        // Get or create bucket for this client
        Bucket bucket = RateLimitConfig.resolveBucket(clientKey);
        
        // Try to consume a token
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        
        if (probe.isConsumed()) {
            // Token consumed successfully
            long tokensRemaining = probe.getRemainingTokens();
            response.addHeader("X-Rate-Limit-Remaining", String.valueOf(tokensRemaining));
            
            filterChain.doFilter(request, response);
        } else {
            // Rate limit exceeded
            long waitForRefillSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;
            
            response.addHeader("X-Rate-Limit-Retry-After-Seconds", String.valueOf(waitForRefillSeconds));
            response.setStatus(HttpServletResponse.SC_TOO_MANY_REQUESTS);
            response.setContentType("application/json");
            
            String errorMessage = String.format(
                    "{\"error\":\"Rate limit exceeded\",\"retry_after_seconds\":%d,\"message\":\"Too many requests. Please try again after %d seconds\"}",
                    waitForRefillSeconds,
                    waitForRefillSeconds
            );
            
            response.getWriter().write(errorMessage);
            
            logger.warn("Rate limit exceeded for client: {} ({})", clientKey, request.getRemoteAddr());
        }
    }

    /**
     * Get client identifier (IP address or user ID)
     * Tries to get the real IP if behind a proxy
     */
    private String getClientKey(HttpServletRequest request) {
        String clientIP = request.getHeader("X-Forwarded-For");
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getHeader("X-Real-IP");
        }
        if (clientIP == null || clientIP.isEmpty()) {
            clientIP = request.getRemoteAddr();
        }
        
        // If multiple IPs are forwarded, get the first one
        if (clientIP.contains(",")) {
            clientIP = clientIP.split(",")[0].trim();
        }
        
        return clientIP;
    }

    /**
     * Check if rate limiting should be skipped for this request
     */
    private boolean shouldSkipRateLimit(HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        for (String excludedPath : EXCLUDED_PATHS) {
            if (requestURI.contains(excludedPath)) {
                return true;
            }
        }
        return false;
    }
}
