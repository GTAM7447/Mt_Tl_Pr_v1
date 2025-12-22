package com.spring.jwt.entity;

import com.spring.jwt.entity.Enums.Gender;
import com.spring.jwt.entity.Enums.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "user_profile", uniqueConstraints = {
        @UniqueConstraint(name = "uk_user_profile_user_id", columnNames = "user_id")
}, indexes = {
        @Index(name = "idx_profile_gender_status", columnList = "gender, status"),
        @Index(name = "idx_profile_religion_caste", columnList = "religion, caste"),
        @Index(name = "idx_profile_district", columnList = "district"),
        @Index(name = "idx_profile_deleted", columnList = "deleted")
})
@SQLDelete(sql = "UPDATE user_profile SET deleted = true, updated_at = CURRENT_TIMESTAMP WHERE user_profile_id = ? AND version = ?")
@Where(clause = "deleted = false")
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_profile_id")
    private Integer userProfileId;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    // Basic Info
    @Column(length = 45, nullable = false)
    private String firstName;

    @Column(length = 45)
    private String middleName;

    @Column(length = 45, nullable = false)
    private String lastName;

    @Column(nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status = Status.DEACTIVE;

    // Contact
    @Column(length = 250, nullable = false)
    private String address;

    @Column(length = 45, nullable = false)
    private String taluka;

    @Column(length = 45, nullable = false)
    private String district;

    @Column(nullable = false)
    private Integer pinCode;

    // Note: mobileNumber and email removed - these are stored in User entity to
    // avoid duplication

    // Personal Details
    @Column(length = 45, nullable = false)
    private String religion;

    @Column(length = 45, nullable = false)
    private String caste;

    @Column(length = 45, nullable = false)
    private String maritalStatus;

    @Column(nullable = false)
    private Double height;

    @Column(nullable = false)
    private Integer weight;

    @Column(length = 45, nullable = false)
    private String bloodGroup;

    @Column(length = 45, nullable = false)
    private String complexion;

    @Column(length = 45, nullable = false)
    private String diet;

    @Column(nullable = false)
    private Boolean spectacle;

    @Column(nullable = false)
    private Boolean lens;

    @Column(nullable = false)
    private Boolean physicallyChallenged;

    @Column(length = 45, nullable = false)
    private String homeTownDistrict;

    @Column(length = 45, nullable = false)
    private String nativeTaluka;

    @Column(length = 45, nullable = false)
    private String currentCity;

    // Audit Fields
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

    // Relationship
    @OneToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.deleted = false;
        if (this.version == null) {
            this.version = 0;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
