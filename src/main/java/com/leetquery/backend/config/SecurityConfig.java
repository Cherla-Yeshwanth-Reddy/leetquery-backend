package com.leetquery.backend.config;

import com.leetquery.backend.security.JwtAuthenticationFilter;
import com.leetquery.backend.security.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration
 * Configures JWT-based authentication and authorization
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
public class SecurityConfig {
    
    @Autowired
    private CustomUserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * Configure DaoAuthenticationProvider
     * Uses CustomUserDetailsService and PasswordEncoder
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return authProvider;
    }

    /**
     * Create AuthenticationManager bean
     */
    @Bean(name = "authenticationManager")
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider())
                .build();
    }

    /**
     * Configure HTTP Security
     * - Define public endpoints (no authentication required)
     * - Define protected endpoints (authentication required)
     * - Configure CORS and CSRF
     * - Add JWT filter
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF for REST API
                .csrf()
                    .disable()
                // Set session policy to stateless (no session cookies)
                .sessionManagement()
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                    .and()
                // Configure endpoint access
                .authorizeHttpRequests(authz -> authz
                        // Public endpoints - no authentication required
                        .requestMatchers("/api/auth/register").permitAll()
                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/refresh").permitAll()
                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/api-docs/**").permitAll()
                        .requestMatchers("/health").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        
                        // GET endpoints - typically public
                        .requestMatchers(HttpMethod.GET, "/api/problems").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/stages").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/levels").permitAll()
                        
                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                // Exception handling
                .exceptionHandling()
                    .authenticationEntryPoint((httpServletRequest, httpServletResponse, e) -> {
                        httpServletResponse.setStatus(401);
                        httpServletResponse.setContentType("application/json");
                        httpServletResponse.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
                    })
                    .accessDeniedHandler((httpServletRequest, httpServletResponse, e) -> {
                        httpServletResponse.setStatus(403);
                        httpServletResponse.setContentType("application/json");
                        httpServletResponse.getWriter().write("{\"error\":\"Forbidden\",\"message\":\"Access denied\"}");
                    })
                    .and()
                // Add JWT filter before UsernamePasswordAuthenticationFilter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
