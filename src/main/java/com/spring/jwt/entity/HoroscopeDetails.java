package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "horoscope_details")
@Getter
@Setter
@SQLDelete(sql = "UPDATE horoscope_details SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE horoscope_details_id = ?")
@Where(clause = "deleted = false")
public class HoroscopeDetails {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer horoscopeDetailsId;

    @Column(nullable = false)
    private Date dob;

    @Column(length = 45, nullable = false)
    private String time;

    @Column(length = 45, nullable = false)
    private String birthPlace;

    @Column(length = 45, nullable = false)
    private String rashi;

    @Column(length = 45, nullable = false)
    private String nakshatra;

    @Column(length = 45, nullable = false)
    private String charan;

    @Column(length = 45, nullable = false)
    private String nadi;

    @Column(length = 45, nullable = false)
    private String gan;

    @Column(length = 45, nullable = false)
    private String mangal;

    @Column(length = 45, nullable = false)
    private String gotra;

    @Column(name = "created_by", nullable = false, updatable = false)
    private Integer createdBy;

    @Column
    private Integer updatedBy;

    @Column(length = 45, nullable = false)
    private String devak;

    @OneToOne(optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(mappedBy = "horoscopeDetails")
    private CompleteProfile completeProfile;

    @Version
    @Column(name = "version")
    private Integer version;

    // Soft delete fields
    @Column(name = "deleted", nullable = false)
    private Boolean deleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Integer deletedBy;

    // Audit fields
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (deleted == null) {
            deleted = false;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}