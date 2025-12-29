package com.spring.jwt.aspect;

import com.spring.jwt.entity.User;
import com.spring.jwt.entity.UserCredit;
import com.spring.jwt.exception.SubscriptionRequiredException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.UserCredit.UserCreditRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Optional;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class SubscriptionValidationAspect {

    private final UserRepository userRepository;
    private final UserCreditRepository userCreditRepository;

    @Before("@annotation(com.spring.jwt.aspect.RequiresSubscription)")
    public void validateSubscription(JoinPoint joinPoint) {
        String email = getCurrentUserEmail();

        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundExceptions("User not found: " + email);
        }

        if (!hasActiveSubscription(user.getId())) {
            log.warn("Access denied: User {} attempted to access premium feature without active subscription", email);
            throw new SubscriptionRequiredException("Active subscription required for this feature.");
        }
    }

    private boolean hasActiveSubscription(Integer userId) {
        Optional<UserCredit> userCreditOpt = userCreditRepository.findByUserId(userId);

        if (userCreditOpt.isEmpty()) {
            return false;
        }

        UserCredit userCredit = userCreditOpt.get();
        if (userCredit.getEndDate() == null || userCredit.getEndDate().isBefore(LocalDate.now())) {
            return false;
        }

        return true;
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            throw new UserNotFoundExceptions("User not authenticated");
        }
        return auth.getName();
    }
}
