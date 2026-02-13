package com.leetquery.backend.filter;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Filter to log HTTP request and response details
 * Useful for debugging and audit trails
 */
@Component
public class RequestResponseLoggingFilter extends OncePerRequestFilter {
    
    private static final Logger logger = LoggerFactory.getLogger(RequestResponseLoggingFilter.class);
    private static final int MAX_PAYLOAD_SIZE = 10000;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        ContentCachingRequestWrapper wrappedRequest = new ContentCachingRequestWrapper(request);
        ContentCachingResponseWrapper wrappedResponse = new ContentCachingResponseWrapper(response);
        
        long startTime = System.currentTimeMillis();
        
        try {
            filterChain.doFilter(wrappedRequest, wrappedResponse);
        } finally {
            long duration = System.currentTimeMillis() - startTime;
            
            logRequest(wrappedRequest, duration);
            logResponse(wrappedResponse, duration);
            
            wrappedResponse.copyBodyToResponse();
        }
    }

    private void logRequest(ContentCachingRequestWrapper request, long duration) {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String remoteAddr = request.getRemoteAddr();
        
        logger.info("REQUEST: {} {} {} - Remote: {}", 
                method, uri, queryString != null ? "?" + queryString : "", remoteAddr);
        
        // Log request headers (exclude sensitive ones)
        String contentType = request.getContentType();
        if (contentType != null) {
            logger.debug("Content-Type: {}", contentType);
        }
        
        // Log request body for POST/PUT/PATCH
        if (shouldLogBody(method)) {
            byte[] content = request.getContentAsByteArray();
            if (content.length > 0) {
                String body = new String(content, StandardCharsets.UTF_8);
                if (body.length() > MAX_PAYLOAD_SIZE) {
                    body = body.substring(0, MAX_PAYLOAD_SIZE) + "... [truncated]";
                }
                logger.debug("Request Body: {}", body);
            }
        }
    }

    private void logResponse(ContentCachingResponseWrapper response, long duration) {
        int status = response.getStatus();
        String contentType = response.getContentType();
        
        logger.info("RESPONSE: Status: {} - Duration: {}ms", status, duration);
        
        if (contentType != null) {
            logger.debug("Response Content-Type: {}", contentType);
        }
        
        // Log response body
        byte[] content = response.getContentAsByteArray();
        if (content.length > 0 && contentType != null && contentType.contains("application/json")) {
            String body = new String(content, StandardCharsets.UTF_8);
            if (body.length() > MAX_PAYLOAD_SIZE) {
                body = body.substring(0, MAX_PAYLOAD_SIZE) + "... [truncated]";
            }
            logger.debug("Response Body: {}", body);
        }
    }

    private boolean shouldLogBody(String method) {
        return "POST".equals(method) || "PUT".equals(method) || "PATCH".equals(method);
    }
}
