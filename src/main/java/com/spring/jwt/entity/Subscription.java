package com.spring.jwt.entity;

import com.spring.jwt.entity.Enums.Status;
import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "subscription",
       indexes = {
           @Index(name = "idx_subscription_status", columnList = "status"),
           @Index(name = "idx_subscription_created", columnList = "created_at")
       })
@EntityListeners(AuditingEntityListener.class)
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Integer subscriptionId;

    @Column(nullable = false, length = 45)
    private String name;

    @Column(nullable = false)
    private Integer credit;

    @Column(name = "created_at", nullable = false)
    private LocalDate createdDate;

    private Integer dayLimit;

    private Integer timePeriodMonths;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private Status status;

    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;

    @CreatedDate
    @Column(name = "audit_created_at", updatable = false)
    private LocalDateTime auditCreatedAt;

    @LastModifiedDate
    @Column(name = "audit_updated_at")
    private LocalDateTime auditUpdatedAt;

    @CreatedBy
    @Column(name = "created_by", updatable = false)
    private Integer createdBy;

    @LastModifiedBy
    @Column(name = "updated_by")
    private Integer updatedBy;

    @PrePersist
    protected void onCreate() {
        if (this.version == null) {
            this.version = 0;
        }
    }
}
