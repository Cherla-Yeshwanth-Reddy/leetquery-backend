package com.leetquery.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
    
    private final JdbcTemplate jdbcTemplate;
    
    @Value("${supabase.jwt.secret:}")
    private String supabaseJwtSecret;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, 
                                   FilterChain filterChain) throws ServletException, IOException {
        
        // Only apply to /admin/* endpoints
        if (!request.getRequestURI().startsWith("/admin/")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        // Extract Authorization header
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Missing or invalid Authorization header");
            return;
        }
        
        String token = authHeader.substring(7);
        
        try {
            // Verify JWT and extract user_id
            Claims claims = Jwts.parser()
                .verifyWith(getSupabasePublicKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
            
            String userId = claims.getSubject();
            
            if (userId == null) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token: missing user ID");
                return;
            }
            
            // Query user_roles table
            String sql = "SELECT role FROM user_roles WHERE user_id = ?";
            String role = jdbcTemplate.queryForObject(sql, String.class, userId);
            
            if (role == null || !role.equals("ADMIN")) {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.getWriter().write("Access denied: Admin role required");
                return;
            }
            
            // User is admin, allow request
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Invalid token: " + e.getMessage());
        }
    }
    
    private javax.crypto.SecretKey getSupabasePublicKey() {
        // For Supabase, the JWT secret is typically a symmetric key (HS256)
        // If using asymmetric (RS256), you'd need to parse the public key differently
        return io.jsonwebtoken.security.Keys.hmacShaKeyFor(supabaseJwtSecret.getBytes());
    }
}
