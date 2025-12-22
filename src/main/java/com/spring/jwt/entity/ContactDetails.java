package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing contact details for users.
 * Implements soft delete, optimistic locking, and comprehensive audit trail.
 * Enhanced with enterprise-grade features for production readiness.
 */
@Entity
@Table(name = "contact_details",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_contact_details_user_id", columnNames = "user_id"),
           @UniqueConstraint(name = "uk_contact_details_mobile", columnNames = "mobile_number")
       },
       indexes = {
           @Index(name = "idx_contact_details_user_id", columnList = "user_id"),
           @Index(name = "idx_contact_details_mobile", columnList = "mobile_number"),
           @Index(name = "idx_contact_details_city", columnList = "city"),
           @Index(name = "idx_contact_details_state", columnList = "state"),
           @Index(name = "idx_contact_details_country", columnList = "country"),
           @Index(name = "idx_contact_details_deleted", columnList = "deleted"),
           @Index(name = "idx_contact_details_pin_code", columnList = "pin_code")
       })
@SQLDelete(sql = "UPDATE contact_details SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE contact_details_id = ? AND version = ?")
@Where(clause = "deleted = false")
@Getter
@Setter
public class ContactDetails {

    @Id
    @Column(name = "contact_details_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contactDetailsId;

    @Column(name = "full_address", length = 500)
    private String fullAddress;

    @Column(name = "street_address", length = 200)
    private String streetAddress;

    @Column(name = "city", length = 100, nullable = false)
    private String city;

    @Column(name = "state", length = 100)
    private String state;

    @Column(name = "country", length = 100, nullable = false)
    private String country;

    @Column(name = "pin_code", length = 20, nullable = false)
    private String pinCode;

    @Column(name = "mobile_number", length = 20, nullable = false, unique = true)
    private String mobileNumber;

    @Column(name = "alternate_number", length = 20)
    private String alternateNumber;

    @Column(name = "whatsapp_number", length = 20)
    private String whatsappNumber;

    @Column(name = "email_address", length = 100)
    private String emailAddress;

    @Column(name = "emergency_contact_name", length = 100)
    private String emergencyContactName;

    @Column(name = "emergency_contact_number", length = 20)
    private String emergencyContactNumber;

    @Column(name = "emergency_contact_relation", length = 50)
    private String emergencyContactRelation;

    @Column(name = "preferred_contact_method", length = 50)
    private String preferredContactMethod;

    @Column(name = "contact_visibility", length = 50, nullable = false)
    private String contactVisibility = "PRIVATE";

    @Column(name = "is_verified_mobile", nullable = false)
    private Boolean isVerifiedMobile = false;

    @Column(name = "is_verified_email", nullable = false)
    private Boolean isVerifiedEmail = false;

    @Column(name = "verification_attempts", nullable = false)
    private Integer verificationAttempts = 0;

    @Column(name = "last_verification_attempt")
    private LocalDateTime lastVerificationAttempt;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "contactDetails", fetch = FetchType.LAZY)
    private CompleteProfile completeProfile;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.contactVisibility == null) {
            this.contactVisibility = "PRIVATE";
        }
        if (this.isVerifiedMobile == null) {
            this.isVerifiedMobile = false;
        }
        if (this.isVerifiedEmail == null) {
            this.isVerifiedEmail = false;
        }
        if (this.verificationAttempts == null) {
            this.verificationAttempts = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}