package com.spring.jwt.profile;

import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import com.spring.jwt.profile.dto.request.ProfileSearchCriteria;
import com.spring.jwt.profile.dto.request.UpdateProfileRequest;
import com.spring.jwt.profile.dto.response.ProfileListView;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.profile.dto.response.PublicProfileView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for profile management.
 * All operations are secured and scoped to the authenticated user's data.
 */
public interface ProfileService {
    
    /**
     * Create profile for the current authenticated user.
     * 
     * @param request the profile creation request
     * @return created profile response
     * @throws DuplicateProfileException if profile already exists for user
     */
    ProfileResponse createProfile(CreateProfileRequest request);

    /**
     * Get profile for the current authenticated user.
     * 
     * @return current user's profile
     * @throws ProfileNotFoundException if profile not found
     */
    ProfileResponse getCurrentUserProfile();

    /**
     * Get profile by user ID (Admin only).
     * 
     * @param userId the user ID
     * @return profile for the specified user
     * @throws ProfileNotFoundException if profile not found
     */
    ProfileResponse getProfileByUserId(Integer userId);

    /**
     * Get public profile view by profile ID (no authentication required).
     * 
     * @param profileId the profile ID
     * @return limited public profile view
     * @throws ProfileNotFoundException if profile not found
     */
    PublicProfileView getPublicProfileById(Integer profileId);

    /**
     * Get all profiles with pagination (Admin only).
     * 
     * @param pageable pagination information
     * @return paginated profiles
     */
    Page<ProfileListView> getAllProfiles(Pageable pageable);

    /**
     * Search profiles with criteria.
     * 
     * @param criteria search criteria
     * @param pageable pagination information
     * @return paginated search results
     */
    Page<ProfileListView> searchProfiles(ProfileSearchCriteria criteria, Pageable pageable);

    /**
     * Browse profiles by gender.
     * 
     * @param gender the gender
     * @param pageable pagination information
     * @return paginated profiles
     */
    Page<?> browseProfilesByGender(String gender, Pageable pageable);

    /**
     * Update profile for the current authenticated user.
     * 
     * @param request the update request
     * @return updated profile response
     * @throws ProfileNotFoundException if profile not found
     * @throws OptimisticLockingFailureException if version conflict
     */
    ProfileResponse updateCurrentUserProfile(UpdateProfileRequest request);

    /**
     * Delete profile for the current authenticated user.
     * 
     * @throws ProfileNotFoundException if profile not found
     */
    void deleteCurrentUserProfile();

    /**
     * Get profile count by gender for statistics.
     * 
     * @param gender the gender
     * @return count of active profiles
     */
    long getProfileCountByGender(String gender);
}