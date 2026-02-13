package com.leetquery.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * User entity representing a user account in the system
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Email must be valid")
    private String email;
    
    @Column(unique = true, nullable = false)
    @NotBlank(message = "Username cannot be blank")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters")
    private String username;
    
    @Column(nullable = false)
    @NotBlank(message = "Password cannot be blank")
    private String password;
    
    @Column(name = "first_name")
    private String firstName;
    
    @Column(name = "last_name")
    private String lastName;
    
    @Column(name = "is_enabled")
    private Boolean isEnabled = true;
    
    @Column(name = "is_account_locked")
    private Boolean isAccountLocked = false;
    
    @Column(name = "account_not_expired", nullable = false)
    private Boolean accountNotExpired = true;
    
    @Column(name = "credentials_not_expired", nullable = false)
    private Boolean credentialsNotExpired = true;
    
    @Column(name = "failed_login_attempts")
    private Integer failedLoginAttempts = 0;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;
    
    // Audit columns
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Explicit security field setters (in case Lombok doesn't generate them)
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
    }
    
    public void setAccountNotLocked(boolean accountNotLocked) {
        this.isAccountLocked = !accountNotLocked;
    }
    
    public void setAccountNotExpired(boolean accountNotExpired) {
        this.accountNotExpired = accountNotExpired;
    }
    
    public void setCredentialsNotExpired(boolean credentialsNotExpired) {
        this.credentialsNotExpired = credentialsNotExpired;
    }
    
    // Explicit security field getters (matching Spring Security expectations)
    public boolean isEnabled() {
        return isEnabled != null && isEnabled;
    }
    
    public boolean isAccountNonLocked() {
        return isAccountLocked == null || !isAccountLocked;
    }
    
    public boolean isAccountNonExpired() {
        return accountNotExpired == null || accountNotExpired;
    }
    
    public boolean isCredentialsNonExpired() {
        return credentialsNotExpired == null || credentialsNotExpired;
    }
}
