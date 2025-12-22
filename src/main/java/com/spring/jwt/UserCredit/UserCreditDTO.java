package com.spring.jwt.UserCredit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserCreditDTO {

    private Integer userCreditId;

    private Integer totalCredit;
    private LocalDate date;
    private LocalDate endDate;
    private String status;
    private Integer dayCredit;
    private Integer useCredit;
    private Integer balanceCredit;

    private Integer userId;          // FK → User
    private Integer subscriptionId;  // FK → Subscription

    public UserCreditDTO(Integer userCreditId, Integer totalCredit, LocalDate date, LocalDate endDate, String status, Integer useCredit, Integer balanceCredit, Integer userId, Integer integer) {
    }
}
