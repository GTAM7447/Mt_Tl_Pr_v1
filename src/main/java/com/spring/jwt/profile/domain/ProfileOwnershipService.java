package com.spring.jwt.profile.domain;

import com.spring.jwt.entity.UserProfile;
import com.spring.jwt.profile.exception.ProfileAccessDeniedException;
import com.spring.jwt.profile.exception.ProfileNotFoundException;
import com.spring.jwt.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Service responsible for profile ownership verification and authorization.
 * Centralizes all authorization logic for profile access.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileOwnershipService {

    private final UserProfileRepository userProfileRepository;

    /**
     * Get the currently authenticated user's ID from SecurityContext.
     *
     * @return the current user's ID
     * @throws ProfileAccessDeniedException if no user is authenticated
     */
    public Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new ProfileAccessDeniedException("User is not authenticated");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof Integer) {
            return (Integer) principal;
        } else if (principal instanceof com.spring.jwt.service.security.UserDetailsCustom) {
            return ((com.spring.jwt.service.security.UserDetailsCustom) principal).getUserId();
        } else if (principal instanceof org.springframework.security.core.userdetails.UserDetails) {

            String username = ((org.springframework.security.core.userdetails.UserDetails) principal).getUsername();
            try {
                return Integer.parseInt(username);
            } catch (NumberFormatException e) {
                log.error("Username is not a valid integer ID: {}", username);
                throw new ProfileAccessDeniedException("Invalid user ID format");
            }
        } else if (principal instanceof String) {
            try {
                return Integer.parseInt((String) principal);
            } catch (NumberFormatException e) {
                log.error("Principal string is not a valid integer ID: {}", principal);
                throw new ProfileAccessDeniedException("Invalid user ID format");
            }
        }

        log.error("Unknown principal type: {}", principal.getClass().getName());
        throw new ProfileAccessDeniedException("Invalid authentication principal");
    }

    /**
     * Check if the current user has administrative privileges.
     *
     * @return true if user has ADMIN role
     */
    public boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ADMIN"));
    }

    /**
     * Check if the current user can access the specified profile.
     * Users can access their own profiles, admins can access all profiles.
     *
     * @param profileId the profile ID to check access for
     * @return true if access is allowed
     */
    public boolean canAccessProfile(Integer profileId) {
        if (isAdmin()) {
            return true;
        }

        try {
            Integer currentUserId = getCurrentUserId();
            UserProfile profile = userProfileRepository.findById(profileId)
                    .orElse(null);

            if (profile == null) {
                return false;
            }

            return profile.getUser().getId().equals(currentUserId);
        } catch (ProfileAccessDeniedException e) {
            return false;
        }
    }

    /**
     * Check if the current user can modify the specified profile.
     * Users can modify their own profiles, admins can modify all profiles.
     *
     * @param profileId the profile ID to check modification rights for
     * @return true if modification is allowed
     */
    public boolean canModifyProfile(Integer profileId) {

        return canAccessProfile(profileId);
    }

    /**
     * Verify that the current user owns the specified profile.
     * Throws exception if verification fails.
     *
     * @param profileId the profile ID to verify ownership for
     * @throws ProfileAccessDeniedException if user doesn't own the profile
     * @throws ProfileNotFoundException     if profile doesn't exist
     */
    public void verifyOwnership(Integer profileId) {
        if (isAdmin()) {
            return; // Admins bypass ownership checks
        }

        Integer currentUserId = getCurrentUserId();

        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));

        if (!profile.getUser().getId().equals(currentUserId)) {
            log.warn("User {} attempted to access profile {} owned by user {}",
                    currentUserId, profileId, profile.getUser().getId());
            throw new ProfileAccessDeniedException(profileId, currentUserId);
        }
    }

    /**
     * Verify that the current user can create a profile for the specified user ID.
     * Users can only create profiles for themselves, admins can create for anyone.
     *
     * @param targetUserId the user ID to create profile for
     * @throws ProfileAccessDeniedException if user cannot create profile for target
     */
    public void verifyCanCreateForUser(Integer targetUserId) {
        if (isAdmin()) {
            return;
        }

        Integer currentUserId = getCurrentUserId();

        if (!currentUserId.equals(targetUserId)) {
            log.warn("User {} attempted to create profile for user {}", currentUserId, targetUserId);
            throw new ProfileAccessDeniedException(
                    "You can only create a profile for yourself");
        }
    }

    /**
     * Get the user ID who owns the specified profile.
     *
     * @param profileId the profile ID
     * @return the user ID who owns the profile
     * @throws ProfileNotFoundException if profile doesn't exist
     */
    public Integer getProfileOwnerId(Integer profileId) {
        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));

        return profile.getUser().getId();
    }

    /**
     * Check if user is authenticated (not anonymous).
     *
     * @return true if user is authenticated
     */
    public boolean isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
                && authentication.isAuthenticated()
                && !"anonymousUser".equals(authentication.getPrincipal());
    }
}
