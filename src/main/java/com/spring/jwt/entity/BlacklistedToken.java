package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity to store blacklisted JWT tokens in database.
 * Ensures tokens remain invalid even after server restart.
 * 
 * Industry Standard: Persistent token blacklist
 * Used by: Google, AWS, Auth0, Microsoft
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Entity
@Table(name = "blacklisted_tokens", indexes = {
    @Index(name = "idx_token_id", columnList = "token_id", unique = true),
    @Index(name = "idx_expires_at", columnList = "expires_at"),
    @Index(name = "idx_user_email", columnList = "user_email")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlacklistedToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * JWT token ID (jti claim)
     */
    @Column(name = "token_id", nullable = false, unique = true, length = 100)
    private String tokenId;

    /**
     * User email who owned this token
     */
    @Column(name = "user_email", nullable = false, length = 255)
    private String userEmail;

    /**
     * Token type: ACCESS or REFRESH
     */
    @Column(name = "token_type", nullable = false, length = 20)
    private String tokenType;

    /**
     * Reason for blacklisting: logout, security_breach, password_change, etc.
     */
    @Column(name = "reason", nullable = false, length = 50)
    private String reason;

    /**
     * When the token was blacklisted
     */
    @Column(name = "blacklisted_at", nullable = false)
    private LocalDateTime blacklistedAt;

    /**
     * When the token expires (can be removed from blacklist after this)
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    /**
     * IP address from which logout/blacklist was initiated
     */
    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    /**
     * Number of times this blacklisted token was attempted to be reused
     */
    @Column(name = "reuse_attempts", nullable = false)
    @Builder.Default
    private Integer reuseAttempts = 0;

    /**
     * Last time this blacklisted token was attempted to be reused
     */
    @Column(name = "last_reuse_attempt")
    private LocalDateTime lastReuseAttempt;

    /**
     * Increment reuse attempts counter
     */
    public void incrementReuseAttempts() {
        this.reuseAttempts++;
        this.lastReuseAttempt = LocalDateTime.now();
    }
}
