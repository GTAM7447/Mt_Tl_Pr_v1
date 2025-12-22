package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import java.time.LocalDateTime;

@Entity
@Table(name = "family_background")
@Getter
@Setter
@SQLDelete(sql = "UPDATE family_background SET deleted = true, deleted_at = CURRENT_TIMESTAMP WHERE family_background_id = ?")
@Where(clause = "deleted = false")
public class FamilyBackground {

    @Id
    @Column(nullable = false, updatable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer familyBackgroundId;

    @Column(length = 45, nullable = false)
    private String fathersName;

    @Column(length = 45, nullable = false)
    private String fatherOccupation;

    @Column(length = 45, nullable = false)
    private String mothersName;

    @Column(length = 45, nullable = false)
    private String motherOccupation;

    @Column(nullable = false)
    private Integer brother;

    @Column(nullable = false)
    private Integer marriedBrothers;

    @Column(nullable = false)
    private Integer sisters;

    @Column(nullable = false)
    private Integer marriedSisters;

    @Column(length = 45, nullable = false)
    private Boolean interCasteInFamily;

    @Column(length = 45, nullable = false)
    private String parentResiding;

    @Column(length = 45)
    private String familyWealth;

    @Column(length = 45, nullable = false)
    private String mamaSurname;

    @Column(length = 45, nullable = false)
    private String mamaPlace;

    @Column(length = 45)
    private String familyBackgroundCol;

    @Column(length = 45)
    private String relativeSurnames;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne(mappedBy = "familyBackground")
    private CompleteProfile status;

    // Version for optimistic locking
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

    @Column(name = "created_by", nullable = false, updatable = false)
    private Integer createdBy;

    @Column(name = "updated_by")
    private Integer updatedBy;

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