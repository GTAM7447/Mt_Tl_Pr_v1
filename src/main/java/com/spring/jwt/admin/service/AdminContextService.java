package com.spring.jwt.admin.service;

import com.spring.jwt.exception.BaseException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Service to retrieve current admin user context.
 * Enforces authentication requirement for audit trail integrity.
 */
@Service
public class AdminContextService {

    /**
     * Get the email of the currently authenticated admin user.
     * 
     * @return the admin's email address
     * @throws BaseException if no authenticated admin user is found
     */
    public String getCurrentAdminEmail() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(Authentication::isAuthenticated)
                .map(Authentication::getName)
                .orElseThrow(() -> new BaseException(
                        String.valueOf(HttpStatus.UNAUTHORIZED.value()),
                        "Admin authentication required for this operation"));
    }
}
