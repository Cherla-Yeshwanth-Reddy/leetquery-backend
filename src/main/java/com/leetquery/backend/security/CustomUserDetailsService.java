package com.leetquery.backend.security;

import com.leetquery.backend.model.User;
import com.leetquery.backend.repository.UserRepository;
import com.leetquery.backend.exception.ResourceNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Custom UserDetailsService implementation
 * Loads user details from database for Spring Security
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);
    
    @Autowired
    private UserRepository userRepository;

    /**
     * Load user details by username
     * Throws UsernameNotFoundException if user not found
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(usernameOrEmail)
                .or(() -> userRepository.findByEmail(usernameOrEmail))
                .orElseThrow(() -> {
                    logger.warn("User not found: {}", usernameOrEmail);
                    return new UsernameNotFoundException("User not found with username or email: " + usernameOrEmail);
                });
        
        return buildUserDetails(user);
    }

    /**
     * Load user details by user ID
     */
    @Transactional(readOnly = true)
    public UserDetails loadUserById(Long id) throws UsernameNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    logger.warn("User not found with id: {}", id);
                    return new UsernameNotFoundException("User not found with id: " + id);
                });
        
        return buildUserDetails(user);
    }

    /**
     * Build UserDetails from User entity
     */
    private UserDetails buildUserDetails(User user) {
        if (!user.getIsEnabled()) {
            throw new UsernameNotFoundException("User account is disabled");
        }
        
        if (user.getIsAccountLocked()) {
            throw new UsernameNotFoundException("User account is locked");
        }
        
        Collection<GrantedAuthority> authorities = new ArrayList<>();
        
        // Add default USER role
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        
        // In future, could add additional roles based on user type
        // authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
        
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(authorities)
                .accountExpired(false)
                .accountLocked(user.getIsAccountLocked())
                .credentialsExpired(false)
                .disabled(!user.getIsEnabled())
                .build();
    }
}
