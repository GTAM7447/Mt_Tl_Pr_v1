package com.spring.jwt.profile;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.Enums.Gender;
import com.spring.jwt.entity.User;
import com.spring.jwt.entity.UserProfile;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.profile.domain.ProfileOwnershipService;
import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import com.spring.jwt.profile.dto.request.ProfileSearchCriteria;
import com.spring.jwt.profile.dto.request.UpdateProfileRequest;
import com.spring.jwt.profile.dto.response.ProfileListView;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.profile.dto.response.PublicProfileView;
import com.spring.jwt.profile.exception.DuplicateProfileException;
import com.spring.jwt.profile.exception.ProfileNotFoundException;
import com.spring.jwt.profile.mapper.ProfileDtoMapper;
import com.spring.jwt.repository.UserProfileRepository;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.utils.CacheUtils;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.sql.SQLException;

/**
 * Implementation of ProfileService interface.
 * Combines command and query operations with proper caching, security, and transaction management.
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ProfileServiceImpl implements ProfileService {

    private final UserProfileRepository userProfileRepository;
    private final UserRepository userRepository;
    private final CompleteProfileRepository completeProfileRepository;
    private final CompleteProfileService completeProfileService;
    private final ProfileOwnershipService ownershipService;
    private final ProfileDtoMapper mapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(retryFor = { SQLException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Caching(evict = {
        @CacheEvict(value = CacheUtils.CacheNames.PROFILES, key = "#root.target.getCurrentUserId()"),
        @CacheEvict(value = CacheUtils.CacheNames.PROFILE_STATS, allEntries = true)
    })
    public ProfileResponse createProfile(CreateProfileRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Creating profile for authenticated user ID: {}", currentUserId);

        return createProfileForUser(currentUserId, request);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(retryFor = { SQLException.class }, maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Caching(evict = {
        @CacheEvict(value = CacheUtils.CacheNames.PROFILES, key = "#userId"),
        @CacheEvict(value = CacheUtils.CacheNames.PROFILE_STATS, allEntries = true)
    })
    public ProfileResponse createProfileForUser(Integer userId, CreateProfileRequest request) {
        log.info("Creating profile for user ID: {}", userId);

        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with ID: " + userId);
        }

        if (userProfileRepository.existsByUser_Id(userId)) {
            throw new DuplicateProfileException("Profile already exists for user ID: " + userId);
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));

        UserProfile profile = mapper.toEntity(request, user);

        // For admin operations, use the target user ID as creator
        profile.setCreatedBy(userId);
        profile.setUpdatedBy(userId);

        UserProfile savedProfile = userProfileRepository.save(profile);
        log.info("Profile created successfully with ID: {}", savedProfile.getUserProfileId());

        synchronizeCompleteProfile(user, savedProfile);

        return mapper.toResponse(savedProfile);
    }

    @Override
    @Cacheable(value = CacheUtils.CacheNames.PROFILES, key = "#root.target.getCurrentUserId()")
    public ProfileResponse getCurrentUserProfile() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching profile for current user ID: {}", currentUserId);

        UserProfile profile = userProfileRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ProfileNotFoundException("userId", currentUserId));

        return mapper.toResponse(profile);
    }

    @Override
    @Cacheable(value = CacheUtils.CacheNames.PROFILES, key = "#userId")
    public ProfileResponse getProfileByUserId(Integer userId) {
        log.debug("Fetching profile for user ID: {}", userId);

        UserProfile profile = userProfileRepository.findByUser_Id(userId)
                .orElseThrow(() -> new ProfileNotFoundException("userId", userId));

        return mapper.toResponse(profile);
    }

    @Override
    @Cacheable(value = CacheUtils.CacheNames.PUBLIC_PROFILES, key = "#profileId")
    public PublicProfileView getPublicProfileById(Integer profileId) {
        log.debug("Fetching public view of profile ID: {}", profileId);

        UserProfile profile = userProfileRepository.findById(profileId)
                .orElseThrow(() -> new ProfileNotFoundException(profileId));

        return mapper.toPublicView(profile);
    }

    @Override
    public Page<ProfileListView> getAllProfiles(Pageable pageable) {
        log.debug("Fetching all profiles, page: {}", pageable.getPageNumber());

        Page<UserProfile> profiles = userProfileRepository.findAllWithUser(pageable);
        return profiles.map(mapper::toListView);
    }

    @Override
    public Page<ProfileListView> searchProfiles(ProfileSearchCriteria criteria, Pageable pageable) {
        log.debug("Searching profiles with criteria: {}", criteria);

        Gender gender = null;
        if (criteria.getGender() != null && !criteria.getGender().trim().isEmpty()) {
            gender = validateAndConvertGender(criteria.getGender());
        }

        Page<UserProfile> profiles = userProfileRepository.searchProfiles(
                gender,
                criteria.getReligion(),
                criteria.getCaste(),
                criteria.getDistrict(),
                criteria.getMinAge(),
                criteria.getMaxAge(),
                pageable);

        return profiles.map(mapper::toListView);
    }

    @Override
    public Page<?> browseProfilesByGender(String gender, Pageable pageable) {
        log.debug("Browsing profiles by gender: {}, page: {}", gender, pageable.getPageNumber());

        Gender genderEnum = validateAndConvertGender(gender);
        Page<UserProfile> profiles = userProfileRepository.findByGender(genderEnum, pageable);

        if (ownershipService.isAuthenticated()) {
            return profiles.map(mapper::toListView);
        } else {
            return profiles.map(mapper::toPublicView);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @Retryable(retryFor = { SQLException.class, OptimisticLockingFailureException.class }, 
               maxAttempts = 3, backoff = @Backoff(delay = 1000))
    @Caching(evict = {
        @CacheEvict(value = CacheUtils.CacheNames.PROFILES, key = "#root.target.getCurrentUserId()"),
        @CacheEvict(value = CacheUtils.CacheNames.PUBLIC_PROFILES, key = "#result.userProfileId"),
        @CacheEvict(value = CacheUtils.CacheNames.PROFILE_STATS, allEntries = true)
    })
    public ProfileResponse updateCurrentUserProfile(UpdateProfileRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Updating profile for current user ID: {}", currentUserId);

        UserProfile existing = userProfileRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ProfileNotFoundException("userId", currentUserId));

        if (!existing.getVersion().equals(request.getVersion())) {
            log.warn("Version conflict for user {}: expected {}, got {}",
                    currentUserId, existing.getVersion(), request.getVersion());
            throw new OptimisticLockingFailureException(
                    "Profile has been modified by another transaction. Please refresh and try again.");
        }

        mapper.applyUpdate(request, existing);

        existing.setUpdatedBy(currentUserId);

        try {
            UserProfile updated = userProfileRepository.save(existing);
            log.info("Profile for user {} updated successfully to version {}", currentUserId, updated.getVersion());

            recalculateCompleteProfileAsync(updated);

            return mapper.toResponse(updated);
        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception during profile update: {}", e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Profile has been modified by another transaction. Please refresh and try again.", e);
        }
    }

    @Override
    @Transactional
    @Caching(evict = {
        @CacheEvict(value = CacheUtils.CacheNames.PROFILES, key = "#root.target.getCurrentUserId()"),
        @CacheEvict(value = CacheUtils.CacheNames.PUBLIC_PROFILES, allEntries = true),
        @CacheEvict(value = CacheUtils.CacheNames.PROFILE_STATS, allEntries = true)
    })
    public void deleteCurrentUserProfile() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Deleting profile for current user ID: {}", currentUserId);

        UserProfile existing = userProfileRepository.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ProfileNotFoundException("userId", currentUserId));

        userProfileRepository.delete(existing);

        log.info("Profile for user {} soft deleted successfully", currentUserId);
    }

    @Override
    @Cacheable(value = CacheUtils.CacheNames.PROFILE_STATS, key = "#gender")
    public long getProfileCountByGender(String gender) {
        Gender genderEnum = validateAndConvertGender(gender);
        return userProfileRepository.countByGenderAndActiveStatus(genderEnum);
    }

    /**
     * Helper method to get current user ID for caching.
     * Used in SpEL expressions for cache keys.
     *
     * @return current user ID
     */
    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    /**
     * Validate and convert gender string to enum.
     */
    private Gender validateAndConvertGender(String genderStr) {
        if (genderStr == null || genderStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Gender cannot be null or empty");
        }

        try {
            return Gender.valueOf(genderStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid gender value. Must be MALE, FEMALE, or OTHER");
        }
    }

    /**
     * Synchronize CompleteProfile entity when profile is created.
     */
    private void synchronizeCompleteProfile(User user, UserProfile profile) {
        CompleteProfile completeProfile = completeProfileRepository.findByUser_Id(user.getId())
                .orElseGet(() -> {
                    CompleteProfile newCp = new CompleteProfile();
                    newCp.setUser(user);
                    newCp.setProfileCompleted(false);
                    return newCp;
                });

        completeProfile.setUserProfile(profile);
        CompleteProfile saved = completeProfileRepository.save(completeProfile);

        completeProfileService.recalcAndSave(saved);

        log.info("CompleteProfile synchronized for user ID: {}", user.getId());
    }

    /**
     * Trigger asynchronous recalculation of CompleteProfile.
     */
    private void recalculateCompleteProfileAsync(UserProfile profile) {
        try {
            completeProfileRepository.findByUser_Id(profile.getUser().getId())
                    .ifPresent(cp -> {
                        cp.setUserProfile(profile);
                        CompleteProfile saved = completeProfileRepository.save(cp);
                        completeProfileService.recalcAndSave(saved);
                    });
        } catch (Exception e) {
            log.error("Error recalculating CompleteProfile for user {}: {}",
                    profile.getUser().getId(), e.getMessage(), e);
        }
    }
}