package com.spring.jwt.FamilyBackground;

import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for family background management.
 * All operations are secured and scoped to the authenticated user's data.
 */
public interface FamilyBackgroundService {
    
    /**
     * Create family background for the current authenticated user.
     * 
     * @param request the family background creation request
     * @return created family background response
     * @throws ResourceAlreadyExistsException if family background already exists for user
     */
    FamilyBackgroundResponse createForCurrentUser(FamilyBackgroundCreateRequest request);

    /**
     * Create family background for a specific user (Admin only).
     * 
     * @param userId the target user ID
     * @param request the family background creation request
     * @return created family background response
     * @throws ResourceAlreadyExistsException if family background already exists for user
     */
    FamilyBackgroundResponse createForUser(Integer userId, FamilyBackgroundCreateRequest request);

    /**
     * Get family background for the current authenticated user.
     * 
     * @return current user's family background
     * @throws ResourceNotFoundException if family background not found
     */
    FamilyBackgroundResponse getCurrentUserFamilyBackground();

    /**
     * Get family background by user ID (Admin only).
     * 
     * @param userId the user ID
     * @return family background for the specified user
     * @throws ResourceNotFoundException if family background not found
     */
    FamilyBackgroundResponse getByUserId(Integer userId);


    /**
     * Update family background for the current authenticated user.
     * 
     * @param request the update request
     * @return updated family background response
     * @throws ResourceNotFoundException if family background not found
     */
    FamilyBackgroundResponse updateCurrentUserFamilyBackground(FamilyBackgroundUpdateRequest request);

    /**
     * Delete family background for the current authenticated user.
     * 
     * @throws ResourceNotFoundException if family background not found
     */
    void deleteCurrentUserFamilyBackground();
}
