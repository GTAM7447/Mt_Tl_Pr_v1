package com.spring.jwt.UserCredit;

import com.spring.jwt.entity.Subscription;
import com.spring.jwt.entity.UserCredit;

public class UserCreditMapper {

    public static UserCreditDTO toDTO(UserCredit uc) {
        return new UserCreditDTO(
                uc.getUserCreditId(),
                uc.getTotalCredit(),
                uc.getDate(),
                uc.getEndDate(),
                uc.getStatus(),
                uc.getUseCredit(),
                uc.getBalanceCredit(),
                uc.getUserId(),
                uc.getSubscription() != null ? uc.getSubscription().getSubscriptionId() : null
        );
    }

    public static UserCredit toEntity(UserCreditDTO dto, Subscription sub) {

        UserCredit uc = new UserCredit();
        uc.setUserCreditId(dto.getUserCreditId());
        uc.setTotalCredit(dto.getTotalCredit());
        uc.setDate(dto.getDate());
        uc.setEndDate(dto.getEndDate());
        uc.setStatus(dto.getStatus());
        uc.setUseCredit(dto.getUseCredit());
        uc.setBalanceCredit(dto.getBalanceCredit());
        uc.setUserId(dto.getUserId());
        uc.setSubscription(sub);

        return uc;
    }
}
