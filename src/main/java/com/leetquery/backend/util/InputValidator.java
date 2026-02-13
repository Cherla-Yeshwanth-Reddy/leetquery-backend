package com.leetquery.backend.util;

import com.leetquery.backend.exception.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for input validation and sanitization
 * Provides protection against SQL injection, XSS, and other attacks
 */
public class InputValidator {
    
    private static final Logger logger = LoggerFactory.getLogger(InputValidator.class);
    
    // SQL injection patterns
    private static final String SQL_INJECTION_PATTERN = 
            "(?i:.*(" +
            "('\\s*;)|" +
            "(-{2})|" +
            "(/\\*.*?\\*/)|" +
            "(;\\s*drop)|" +
            "(;\\s*delete)|" +
            "(;\\s*update)|" +
            "(;\\s*insert)|" +
            "(;\\s*create)|" +
            "(;\\s*alter)|" +
            "(;\\s*execute)|" +
            "(;\\s*exec)|" +
            "(union.*select)|" +
            "(select.*from)|" +
            "(insert.*into)|" +
            "(delete.*from)|" +
            "(update.*set)|" +
            "(drop.*table)|" +
            "(create.*table))" +
            ".*)";
    
    // XSS patterns
    private static final String XSS_PATTERN = 
            "(?i:.*(" +
            "(<script[^>]*>)|" +
            "(javascript:)|" +
            "(onerror=)|" +
            "(onload=)|" +
            "(onclick=)|" +
            "(onmouseover=)|" +
            "(&lt;script[^&]*&gt;)|" +
            "(<iframe[^>]*>)|" +
            "(<object[^>]*>)|" +
            "(<embed[^>]*>))" +
            ".*)";

    /**
     * Validate that the input is not null or blank
     */
    public static void validateNotBlank(String value, String fieldName) {
        if (value == null || value.trim().isEmpty()) {
            throw new ValidationException(fieldName + " cannot be blank");
        }
    }

    /**
     * Validate that the input length is within acceptable range
     */
    public static void validateLength(String value, int minLength, int maxLength, String fieldName) {
        if (value == null) {
            throw new ValidationException(fieldName + " cannot be null");
        }
        
        int length = value.length();
        if (length < minLength || length > maxLength) {
            throw new ValidationException(
                    fieldName + " must be between " + minLength + " and " + maxLength + " characters"
            );
        }
    }

    /**
     * Check for SQL injection patterns in the input
     */
    public static void checkSQLInjection(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            return;
        }
        
        if (value.matches(SQL_INJECTION_PATTERN)) {
            logger.warn("Potential SQL injection detected in field: {}", fieldName);
            throw new ValidationException(
                    "Invalid characters detected in " + fieldName + ". Please check your input."
            );
        }
    }

    /**
     * Check for XSS patterns in the input
     */
    public static void checkXSS(String value, String fieldName) {
        if (value == null || value.isEmpty()) {
            return;
        }
        
        if (value.matches(XSS_PATTERN)) {
            logger.warn("Potential XSS attack detected in field: {}", fieldName);
            throw new ValidationException(
                    "Invalid HTML/script tags detected in " + fieldName + ". Please check your input."
            );
        }
    }

    /**
     * Sanitize input by removing or encoding dangerous characters
     */
    public static String sanitizeInput(String value) {
        if (value == null) {
            return null;
        }
        
        return value
                .replaceAll("<", "&lt;")
                .replaceAll(">", "&gt;")
                .replaceAll("\"", "&quot;")
                .replaceAll("'", "&#x27;")
                .replaceAll("&", "&amp;");
    }

    /**
     * Validate and check a query string
     */
    public static void validateQuery(String query) {
        validateNotBlank(query, "Query");
        validateLength(query, 1, 50000, "Query");
        checkSQLInjection(query, "Query");
    }

    /**
     * Validate a user input field
     */
    public static void validateInputField(String input, String fieldName, int maxLength) {
        validateNotBlank(input, fieldName);
        validateLength(input, 1, maxLength, fieldName);
        checkXSS(input, fieldName);
    }
}
