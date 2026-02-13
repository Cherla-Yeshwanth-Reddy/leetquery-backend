package com.leetquery.backend.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * JWT Token Provider
 * Handles token generation, validation, and claims extraction
 */
@Component
public class JwtTokenProvider {
    
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    
    @Value("${app.jwtSecret:MyVeryLongSecretKeyForJWTTokenSigningThatIsAtLeast256BitsLongForHmacSha256Algorithm}")
    private String jwtSecret;
    
    @Value("${app.jwtExpirationMs:86400000}")  // 24 hours
    private long jwtExpirationMs;
    
    @Value("${app.jwtRefreshExpirationMs:604800000}")  // 7 days
    private long jwtRefreshExpirationMs;
    
    /**
     * Generate JWT token from Authentication
     */
    public String generateToken(Authentication authentication) {
        String username = authentication.getName();
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Generate JWT token from username
     */
    public String generateTokenFromUsername(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Generate refresh token
     */
    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtRefreshExpirationMs);
        
        return Jwts.builder()
                .subject(username)
                .issuedAt(now)
                .expiration(expiryDate)
                .claim("type", "refresh")
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * Get username from token
     */
    public String getUsernameFromJWT(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getSubject();
    }
    
    /**
     * Get expiration date from token
     */
    public Date getExpirationDateFromToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return claims.getExpiration();
    }
    
    /**
     * Check if token is expired
     */
    public Boolean isTokenExpired(String token) {
        try {
            Date expiration = getExpirationDateFromToken(token);
            return expiration.before(new Date());
        } catch (ExpiredJwtException ex) {
            logger.warn("Token is expired: {}", ex.getMessage());
            return true;
        }
    }
    
    /**
     * Validate JWT token
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty: {}", ex.getMessage());
        } catch (SignatureException ex) {
            logger.error("JWT signature validation failed: {}", ex.getMessage());
        }
        return false;
    }
    
    /**
     * Get all claims from token
     */
    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getPayload();
        } catch (JwtException ex) {
            logger.error("Failed to parse JWT token: {}", ex.getMessage());
            throw new JwtException("Invalid JWT token", ex);
        }
    }
    
    /**
     * Get signing key for token
     * Uses HMAC-SHA256 algorithm
     */
    private SecretKey getSigningKey() {
        // For production, ensure jwtSecret is at least 256 bits (32 bytes)
        byte[] keyBytes = jwtSecret.getBytes();
        if (keyBytes.length < 32) {
            logger.warn("JWT secret is less than 256 bits. Consider using a stronger secret.");
        }
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * Get token expiration time in milliseconds
     */
    public long getTokenExpirationMs() {
        return jwtExpirationMs;
    }
    
    /**
     * Get refresh token expiration time in milliseconds
     */
    public long getRefreshTokenExpirationMs() {
        return jwtRefreshExpirationMs;
    }
}
