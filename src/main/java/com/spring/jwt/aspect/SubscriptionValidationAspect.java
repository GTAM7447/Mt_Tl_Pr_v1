package com.spring.jwt.aspect;

import com.spring.jwt.entity.User;
import com.spring.jwt.entity.UserCredit;
import com.spring.jwt.exception.SubscriptionRequiredException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.UserCredit.UserCreditRepository;
import jakarta.servlet.http.HttpServletRequest;
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
        log.debug("SubscriptionValidationAspect triggered for method: {}", joinPoint.getSignature().getName());

        try {
            org.springframework.web.context.request.RequestAttributes requestAttributes = 
                org.springframework.web.context.request.RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request =
                    ((org.springframework.web.context.request.ServletRequestAttributes) requestAttributes).getRequest();
                String requestURI = request.getRequestURI();
                if (requestURI != null && requestURI.contains("/admin/")) {
                    log.debug("Skipping subscription validation for admin endpoint: {}", requestURI);
                    return;
                }
            }
        } catch (Exception e) {
            log.debug("Could not check request URI: {}", e.getMessage());
        }
        
        String email = getCurrentUserEmail();
        log.debug("Validating subscription for user: {}", email);

        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.warn("User not found with email: {}", email);
            throw new UserNotFoundExceptions("User not found: " + email);
        }

        boolean isAdmin = user.getRoles().stream()
                .anyMatch(role -> "ADMIN".equals(role.getName()));
        
        if (isAdmin) {
            log.debug("Skipping subscription validation for admin user: {}", email);
            return;
        }

        if (!hasActiveSubscription(user.getId())) {
            log.warn("Access denied: User {} attempted to access premium feature without active subscription", email);
            throw new SubscriptionRequiredException("Active subscription required for this feature.");
        }
        
        log.debug("Subscription validation passed for user: {}", email);
    }

    private boolean hasActiveSubscription(Integer userId) {
        Optional<UserCredit> userCreditOpt = userCreditRepository.findByUserId(userId);

        if (userCreditOpt.isEmpty()) {
            log.debug("No user credit found for userId: {}", userId);
            return false;
        }

        UserCredit userCredit = userCreditOpt.get();
        LocalDate today = LocalDate.now();
        
        if (userCredit.getEndDate() == null) {
            log.debug("User {} has no end date set for credits", userId);
            return false;
        }
        
        if (userCredit.getEndDate().isBefore(today)) {
            log.debug("User {} subscription expired on {}", userId, userCredit.getEndDate());
            return false;
        }

        log.debug("User {} has active subscription until {}", userId, userCredit.getEndDate());
        return true;
    }

    private String getCurrentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        if (auth == null || !auth.isAuthenticated()) {
            log.warn("No authentication found in security context");
            throw new UserNotFoundExceptions("User not authenticated");
        }
        
        String username = auth.getName();
        
        // Check if user is anonymous
        if ("anonymousUser".equals(username)) {
            log.warn("Anonymous user attempted to access protected resource");
            throw new UserNotFoundExceptions("Authentication required. Please log in to access this feature.");
        }
        
        return username;
    }
}
