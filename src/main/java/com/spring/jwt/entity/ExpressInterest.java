package com.spring.jwt.entity;

import com.spring.jwt.entity.Enums.InterestStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Express Interest entity with enterprise-level features.
 * Represents interest expressed by one user towards another user's profile.
 * 
 * Features:
 * - Soft delete support
 * - Audit trail (created/updated by, timestamps)
 * - Optimistic locking
 * - Business rule constraints
 * - Performance optimized relationships
 */
@Entity
@Table(name = "express_interest", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_from_to_user_active", 
                           columnNames = {"from_user_id", "to_user_id", "is_deleted"})
       },
       indexes = {
           @Index(name = "idx_express_interest_from_user_status", 
                  columnList = "from_user_id, status, is_deleted, created_at"),
           @Index(name = "idx_express_interest_to_user_status", 
                  columnList = "to_user_id, status, is_deleted, created_at"),
           @Index(name = "idx_express_interest_created_at", 
                  columnList = "created_at"),
           @Index(name = "idx_express_interest_expires_at", 
                  columnList = "expires_at"),
           @Index(name = "idx_express_interest_compatibility", 
                  columnList = "compatibility_score"),
           @Index(name = "idx_express_interest_daily_limit", 
                  columnList = "from_user_id, created_at, is_deleted")
       })
@Where(clause = "is_deleted = false")
@Data
@EqualsAndHashCode(callSuper = false)
public class ExpressInterest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "interest_id")
    private Long interestId;

    // Core relationship fields
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false, 
                foreignKey = @ForeignKey(name = "fk_express_interest_from_user"))
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false,
                foreignKey = @ForeignKey(name = "fk_express_interest_to_user"))
    private User toUser;

    // Status and messaging
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterestStatus status = InterestStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String message;

    @Column(name = "response_message", columnDefinition = "TEXT")
    private String responseMessage;

    // Compatibility and matching
    @Column(name = "compatibility_score")
    private Integer compatibilityScore;

    @Column(name = "auto_matched")
    private Boolean autoMatched = false;

    // Expiry and limits
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "daily_limit_count")
    private Integer dailyLimitCount = 1;

    // Audit fields
    @Column(name = "created_by")
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Soft delete fields
    @Column(name = "is_deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    // Optimistic locking
    @Version
    private Integer version = 0;

    // Additional metadata
    @Column(name = "source_platform", length = 50)
    private String sourcePlatform = "WEB";

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    // Constructors
    public ExpressInterest() {}

    public ExpressInterest(User fromUser, User toUser, String message) {
        this.fromUser = fromUser;
        this.toUser = toUser;
        this.message = message;
        this.status = InterestStatus.PENDING;
        this.autoMatched = false;
        this.deleted = false;
        this.version = 0;
        this.dailyLimitCount = 1;
        this.sourcePlatform = "WEB";
    }

    // Business logic methods
    public boolean isPending() {
        return InterestStatus.PENDING.equals(this.status);
    }

    public boolean isAccepted() {
        return InterestStatus.ACCEPTED.equals(this.status);
    }

    public boolean isDeclined() {
        return InterestStatus.DECLINED.equals(this.status);
    }

    public boolean isWithdrawn() {
        return InterestStatus.WITHDRAWN.equals(this.status);
    }

    public boolean isExpired() {
        return InterestStatus.EXPIRED.equals(this.status) || 
               (this.expiresAt != null && this.expiresAt.isBefore(LocalDateTime.now()));
    }

    public boolean canBeAccepted() {
        return isPending() && !isExpired();
    }

    public boolean canBeDeclined() {
        return isPending() && !isExpired();
    }

    public boolean canBeWithdrawn() {
        return isPending() && !isExpired();
    }

    public void accept(String responseMessage) {
        if (!canBeAccepted()) {
            throw new IllegalStateException("Interest cannot be accepted in current state: " + this.status);
        }
        this.status = InterestStatus.ACCEPTED;
        this.responseMessage = responseMessage;
    }

    public void decline(String responseMessage) {
        if (!canBeDeclined()) {
            throw new IllegalStateException("Interest cannot be declined in current state: " + this.status);
        }
        this.status = InterestStatus.DECLINED;
        this.responseMessage = responseMessage;
    }

    public void withdraw() {
        if (!canBeWithdrawn()) {
            throw new IllegalStateException("Interest cannot be withdrawn in current state: " + this.status);
        }
        this.status = InterestStatus.WITHDRAWN;
    }

    public void expire() {
        if (isPending()) {
            this.status = InterestStatus.EXPIRED;
        }
    }

    public void softDelete(Integer deletedBy) {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = deletedBy;
    }

    // Helper methods for queries
    public Integer getFromUserId() {
        return fromUser != null ? fromUser.getId() : null;
    }

    public Integer getToUserId() {
        return toUser != null ? toUser.getId() : null;
    }

    @Override
    public String toString() {
        return "ExpressInterest{" +
                "interestId=" + interestId +
                ", fromUserId=" + getFromUserId() +
                ", toUserId=" + getToUserId() +
                ", status=" + status +
                ", createdAt=" + createdAt +
                ", deleted=" + deleted +
                '}';
    }
}
