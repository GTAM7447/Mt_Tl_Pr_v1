package com.spring.jwt.ExpressInterest.service;

import com.spring.jwt.entity.Enums.Gender;
import com.spring.jwt.entity.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class GenderCompatibilityValidator {

    public void validateGenderCompatibility(User fromUser, User toUser) {
        if (fromUser.getGender() == null || toUser.getGender() == null) {
            log.warn("Gender information missing for users {} or {}", fromUser.getId(), toUser.getId());
            throw new IllegalArgumentException("Gender information is required for both users");
        }

        if (fromUser.getGender() == toUser.getGender()) {
            log.warn("Same gender interest attempt: {} ({}) trying to send interest to {} ({})",
                    fromUser.getId(), fromUser.getGender(),
                    toUser.getId(), toUser.getGender());
            throw new IllegalArgumentException(
                    String.format("Cannot send interest to same gender. You are %s and target user is also %s",
                            fromUser.getGender(), toUser.getGender())
            );
        }

        log.debug("Gender compatibility validated: {} ({}) -> {} ({})",
                fromUser.getId(), fromUser.getGender(),
                toUser.getId(), toUser.getGender());
    }

    public boolean areGendersCompatible(Gender gender1, Gender gender2) {
        if (gender1 == null || gender2 == null) {
            return false;
        }
        return gender1 != gender2;
    }
}
