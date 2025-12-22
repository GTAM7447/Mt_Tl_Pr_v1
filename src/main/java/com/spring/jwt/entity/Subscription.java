package com.spring.jwt.entity;

import com.spring.jwt.entity.Enums.Status;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "subscription")
@Data
public class Subscription {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "subscription_id")
    private Integer subscriptionId;

    @Column(nullable = false, length = 45)
    private String name;  // Ex: Gold Plan, Premium Plan

    @Column(nullable = false)
    private Integer credit;  // How many credits included?

    @Column(name = "created_at", nullable = false)
    private LocalDate createdDate;

    private Integer dayLimit;

    private Integer timePeriodMonths;

    @Enumerated(EnumType.STRING)
    @Column(length = 45)
    private Status status;
}
