package com.spring.jwt.utils;

import com.spring.jwt.service.security.UserDetailsCustom;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

public final class SecurityUtil
{
    private SecurityUtil()
    { }

    private static UserDetailsCustom getCurrentUser()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null)
        {
            throw new IllegalStateException("No authentication found in SecurityContext.");
        }

        if (!authentication.isAuthenticated())
        {
            throw new IllegalStateException("User is not authenticated.");
        }

        Object principal = authentication.getPrincipal();

        if (principal == null || "anonymousUser".equals(principal))
        {
            throw new IllegalStateException("Anonymous user is not allowed.");
        }

        if (principal instanceof UserDetailsCustom customUser)
        {
            return customUser;
        }

        if (principal instanceof String username)
        {
            throw new IllegalStateException(
                    "Principal is String (" + username + "). JWT configuration is broken."
            );
        }

        if (principal instanceof UserDetails userDetails)
        {
            throw new IllegalStateException(
                    "Principal is " + userDetails.getClass().getSimpleName() +
                            " instead of UserDetailsCustom."
            );
        }

        throw new IllegalStateException(
                "Unexpected principal type: " + principal.getClass().getName()
        );

    }
    public static Integer getCurrentUserId()
    {
        Integer id = getCurrentUser().getUserId();
        if (id == null) {
            throw new IllegalStateException("Authenticated user does not have a valid user ID.");
        }
        return id;
    }

    public static String getCurrentUserEmail()
    {
        String email = getCurrentUser().getUsername();

        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Authenticated user does not have a valid email.");
        }
        return email;
    }

}
