package com.spring.jwt.entity;

import com.spring.jwt.entity.Enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.BatchSize;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_user_email", columnList = "email"),
                @Index(name = "idx_user_mobile", columnList = "mobile_number"),
                @Index(name = "idx_user_gender", columnList = "gender"),
                @Index(name = "idx_user_status", columnList = "completeProfile")
        }
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Integer id;

    @NotBlank
    @Email
    @Pattern(
            regexp = "^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$",
            message = "Invalid email format"
    )
    @Column(name = "email", nullable = false, unique = true, length = 250)
    private String email;

    @Column(name = "mobile_number")
    private Long mobileNumber;

    @Column(name = "password", nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "completeProfile")
    private Boolean completeProfile = false;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "login_attempts", nullable = false)
    private Integer loginAttempts = 0;

    @Column(name = "account_locked_until")
    private LocalDateTime accountLockedUntil;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "reset_password_token")
    private String resetPasswordToken;

    @Column(name = "reset_password_token_expiry")
    private LocalDateTime resetPasswordTokenExpiry;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    @BatchSize(size = 25)
    private Set<Role> roles = new HashSet<>();

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public User(String email, String hashedPassword) {
        changeEmail(email);
        changePassword(hashedPassword);
        this.emailVerified = false;
        this.loginAttempts = 0;
    }

    public void changeEmail(String email) {
        if (email == null || !email.matches("^[\\w.-]+@[\\w.-]+\\.[a-zA-Z]{2,}$")) {
            throw new IllegalArgumentException("Invalid email");
        }
        this.email = email.toLowerCase().trim();
    }

    public void changePassword(String hashedPassword) {
        if (hashedPassword == null || hashedPassword.length() < 60) {
            throw new IllegalArgumentException("Password must be hashed");
        }
        this.password = hashedPassword;
    }

    public void changeMobileNumber(Long mobileNumber) {
        if (mobileNumber != null && mobileNumber <= 0) {
            throw new IllegalArgumentException("Invalid mobile number");
        }
        this.mobileNumber = mobileNumber;
    }

    public void changeGender(Gender gender) {
        if (gender == null) {
            throw new IllegalArgumentException("Gender cannot be null");
        }
        this.gender = gender;
    }

    public void markEmailVerified() {
        this.emailVerified = true;
    }

    public void assignRole(Role role) {
        if (role == null) {
            throw new IllegalArgumentException("Role cannot be null");
        }
        this.roles.add(role);
    }

    public void removeRole(Role role) {
        this.roles.remove(role);
    }

    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
        this.accountLockedUntil = null;
    }

    public void lockAccountForMinutes(int minutes) {
        if (minutes <= 0) {
            throw new IllegalArgumentException("Invalid lock duration");
        }
        this.accountLockedUntil = LocalDateTime.now().plusMinutes(minutes);
    }

    public boolean isAccountLocked() {
        return accountLockedUntil != null &&
                accountLockedUntil.isAfter(LocalDateTime.now());
    }

    public void updateLastLogin() {
        this.lastLogin = LocalDateTime.now();
    }

    public void setPasswordResetToken(String token, LocalDateTime expiry) {
        if (token == null || token.trim().isEmpty()) {
            throw new IllegalArgumentException("Token cannot be empty");
        }
        if (expiry == null || expiry.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Expiry must be in the future");
        }
        this.resetPasswordToken = token;
        this.resetPasswordTokenExpiry = expiry;
    }

    public void clearPasswordResetToken() {
        this.resetPasswordToken = null;
        this.resetPasswordTokenExpiry = null;
    }

    @PrePersist
    protected void onCreate() {
        if (emailVerified == null) emailVerified = false;
        if (loginAttempts == null) loginAttempts = 0;
        if (version == null) version = 0;
    }
}
