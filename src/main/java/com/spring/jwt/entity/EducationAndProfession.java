





package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

/**
 * Entity representing education and profession information for users.
 * Implements soft delete, optimistic locking, and comprehensive audit trail.
 */
@Entity
@Table(name = "education_and_profession", 
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_education_profession_user_id", columnNames = "user_id")
       },
       indexes = {
           @Index(name = "idx_education_profession_user_id", columnList = "user_id"),
           @Index(name = "idx_education_profession_occupation", columnList = "occupation"),
           @Index(name = "idx_education_profession_education", columnList = "education"),
           @Index(name = "idx_education_profession_deleted", columnList = "deleted"),
           @Index(name = "idx_education_profession_income", columnList = "income_per_year")
       })
@SQLDelete(sql = "UPDATE education_and_profession SET deleted = true, deleted_at = CURRENT_TIMESTAMP, updated_at = CURRENT_TIMESTAMP WHERE education_id = ? AND version = ?")
@Where(clause = "deleted = false")
@Getter
@Setter
public class EducationAndProfession {

    @Id
    @Column(name = "education_id", nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer educationId;

    @Column(name = "education", length = 100, nullable = false)
    private String education;

    @Column(name = "degree", length = 100, nullable = false)
    private String degree;

    @Column(name = "occupation", length = 200, nullable = false)
    private String occupation;

    @Column(name = "occupation_details", length = 500)
    private String occupationDetails;

    @Column(name = "income_per_year", nullable = false)
    private Integer incomePerYear;

    @Column(name = "additional_details", length = 1000)
    private String additionalDetails;

    @Column(name = "work_location", length = 100)
    private String workLocation;

    @Column(name = "company_name", length = 200)
    private String companyName;

    @Column(name = "experience_years")
    private Integer experienceYears;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @OneToOne(mappedBy = "educationAndProfession", fetch = FetchType.LAZY)
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
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}