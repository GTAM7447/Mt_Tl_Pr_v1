package com.spring.jwt.audit;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuditorAwareImpl implements AuditorAware<Integer> {

    @Override
    public Optional<Integer> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication == null || !authentication.isAuthenticated() || 
            "anonymousUser".equals(authentication.getPrincipal())) {
            return Optional.empty();
        }

        try {
            Object principal = authentication.getPrincipal();
            
            if (principal instanceof Integer) {
                return Optional.of((Integer) principal);
            }
            
            if (principal instanceof String) {
                try {
                    return Optional.of(Integer.parseInt((String) principal));
                } catch (NumberFormatException e) {
                    return Optional.empty();
                }
            }
            
            return Optional.empty();
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}
