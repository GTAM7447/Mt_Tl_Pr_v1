package com.spring.jwt.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "userCredit")
@Data
public class UserCredit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "userCredit")
    private Integer userCreditId;

    private Integer totalCredit;
    private LocalDate date;
    private LocalDate endDate;
    private String status;
    private Integer dayCredit;
    private Integer useCredit;
    private Integer balanceCredit;

    // FK → USER
    @Column(name = "user_userId")
    private Integer userId;

    // FK → SUBSCRIPTION TABLE (old: credit table)
    @ManyToOne
    @JoinColumn(name = "subscription_id")
    private Subscription subscription;
}
