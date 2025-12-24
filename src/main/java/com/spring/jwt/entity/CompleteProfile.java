package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entity representing complete user profile aggregation.
 * Implements soft delete, optimistic locking, and comprehensive audit trail.
 * Serves as the central hub for all profile-related information.
 */
@Entity
@Table(name = "complete_profile",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_complete_profile_user_id", columnNames = "user_id")
       },
       indexes = {
           @Index(name = "idx_complete_profile_user_id", columnList = "user_id"),
           @Index(name = "idx_complete_profile_completed", columnList = "profile_completed"),
           @Index(name = "idx_complete_profile_percentage", columnList = "completion_percentage"),
           @Index(name = "idx_complete_profile_quality", columnList = "profile_quality"),
           @Index(name = "idx_complete_profile_verification", columnList = "verification_status"),
           @Index(name = "idx_complete_profile_deleted", columnList = "deleted"),
           @Index(name = "idx_complete_profile_created_at", columnList = "created_at")
       })
@SQLDelete(sql = "UPDATE complete_profile SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE complete_profile_id = ? AND version = ?")
@Where(clause = "deleted = false")
@Getter
@Setter
public class CompleteProfile {

    @Id
    @Column(name = "complete_profile_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer completeProfileId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_profile_id")
    private UserProfile userProfile;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "horoscope_details_id")
    private HoroscopeDetails horoscopeDetails;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "education_id")
    private EducationAndProfession educationAndProfession;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "family_background_id")
    private FamilyBackground familyBackground;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "partner_preference_id")
    private PartnerPreference partnerPreference;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contact_details_id")
    private ContactDetails contactDetails;

    @OneToMany(mappedBy = "completeProfile", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Document> documents = new ArrayList<>();

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "profile_completed", nullable = false)
    private Boolean profileCompleted = false;

    @Column(name = "completion_percentage", nullable = false)
    private Integer completionPercentage = 0;

    @Column(name = "completeness_score", nullable = false)
    private Integer completenessScore = 0;

    @Column(name = "profile_quality", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileQuality profileQuality = ProfileQuality.POOR;

    @Column(name = "missing_sections_count", nullable = false)
    private Integer missingSectionsCount = 7;

    @Column(name = "profile_visibility", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ProfileVisibility profileVisibility = ProfileVisibility.PRIVATE;

    @Column(name = "verification_status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.UNVERIFIED;

    @Column(name = "basic_info_score")
    private Integer basicInfoScore = 0;

    @Column(name = "contact_info_score")
    private Integer contactInfoScore = 0;

    @Column(name = "personal_details_score")
    private Integer personalDetailsScore = 0;

    @Column(name = "family_info_score")
    private Integer familyInfoScore = 0;

    @Column(name = "professional_info_score")
    private Integer professionalInfoScore = 0;

    @Column(name = "preferences_score")
    private Integer preferencesScore = 0;

    @Column(name = "document_score")
    private Integer documentScore = 0;

    @Column(name = "has_profile_photo", nullable = false)
    private Boolean hasProfilePhoto = false;

    @Column(name = "mobile_verified", nullable = false)
    private Boolean mobileVerified = false;

    @Column(name = "email_verified", nullable = false)
    private Boolean emailVerified = false;

    @Column(name = "identity_verified", nullable = false)
    private Boolean identityVerified = false;

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

    public enum ProfileQuality {
        POOR, FAIR, GOOD, VERY_GOOD, EXCELLENT
    }

    public enum ProfileVisibility {
        PRIVATE, MEMBERS_ONLY, PUBLIC
    }

    public enum VerificationStatus {
        UNVERIFIED, PENDING, VERIFIED, REJECTED
    }

    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.deleted == null) {
            this.deleted = false;
        }
        if (this.profileCompleted == null) {
            this.profileCompleted = false;
        }
        if (this.completionPercentage == null) {
            this.completionPercentage = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
