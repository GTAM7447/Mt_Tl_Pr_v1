package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing partner preferences for users.
 * Implements soft delete, optimistic locking, and comprehensive audit trail.
 */
@Entity
@Table(name = "partner_preference",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_partner_preference_user_id", columnNames = "user_id")
       },
       indexes = {
           @Index(name = "idx_partner_preference_user_id", columnList = "user_id"),
           @Index(name = "idx_partner_preference_religion", columnList = "religion"),
           @Index(name = "idx_partner_preference_caste", columnList = "caste"),
           @Index(name = "idx_partner_preference_education", columnList = "education"),
           @Index(name = "idx_partner_preference_deleted", columnList = "deleted"),
           @Index(name = "idx_partner_preference_income", columnList = "partner_income")
       })
@SQLDelete(sql = "UPDATE partner_preference SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE partner_preference_id = ? AND version = ?")
@Where(clause = "deleted = false")
@Getter
@Setter
public class PartnerPreference {

    @Id
    @Column(name = "partner_preference_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer partnerPreferenceId;

    @Column(name = "age_range", length = 50, nullable = false)
    private String ageRange;

    @Column(name = "looking_for", length = 100, nullable = false)
    private String lookingFor;

    @Column(name = "height_range", length = 50, nullable = false)
    private String heightRange;

    @Column(name = "eating_habits", length = 50)
    private String eatingHabits;

    @Column(name = "drinking_habits", length = 50)
    private String drinkingHabits;

    @Column(name = "smoking_habits", length = 50)
    private String smokingHabits;

    @Column(name = "country_living_in", length = 100)
    private String countryLivingIn;

    @Column(name = "city_living_in", length = 100)
    private String cityLivingIn;

    @Column(name = "state_living_in", length = 100)
    private String stateLivingIn;

    @Column(name = "complexion", length = 50)
    private String complexion;

    @Column(name = "religion", length = 50)
    private String religion;

    @Column(name = "caste", length = 50)
    private String caste;

    @Column(name = "sub_caste", length = 100)
    private String subCaste;

    @Column(name = "education", length = 100)
    private String education;

    @Column(name = "mangal")
    private Boolean mangal;

    @Column(name = "resident_status", length = 50)
    private String residentStatus;

    @Column(name = "partner_occupation", length = 100)
    private String partnerOccupation;

    @Column(name = "partner_income")
    private Integer partnerIncome;

    @Column(name = "marital_status", length = 50)
    private String maritalStatus;

    @Column(name = "mother_tongue", length = 50)
    private String motherTongue;

    @Column(name = "additional_preferences", length = 1000)
    private String additionalPreferences;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "partnerPreference", fetch = FetchType.LAZY)
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

    // Optimistic locking
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    // JPA lifecycle callbacks
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.deleted == null) {
            this.deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
