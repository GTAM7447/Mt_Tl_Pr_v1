package com.spring.jwt.FamilyBackground;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundUpdateRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.FamilyBackground;
import com.spring.jwt.entity.User;
import com.spring.jwt.exception.ResourceAlreadyExistsException;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.profile.domain.ProfileOwnershipService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.utils.CacheUtils;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for family background management.
 * Implements secure operations with proper authorization and IDOR protection.
 * All operations are scoped to the authenticated user's data unless explicitly admin-only.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class FamilyBackgroundServiceImpl implements FamilyBackgroundService {

    private final FamilyBackgroundRepository familyBackgroundRepo;
    private final UserRepository userRepo;
    private final CompleteProfileRepository completeProfileRepo;
    private final CompleteProfileService completeProfileService;
    private final ProfileOwnershipService ownershipService;
    private final FamilyBackgroundMapper mapper;
    private final FamilyBackgroundValidationService validationService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.FAMILY_BACKGROUNDS, key = "#result.userId")
    public FamilyBackgroundResponse createForCurrentUser(FamilyBackgroundCreateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Creating family background for authenticated user ID: {}", currentUserId);

        return createForUser(currentUserId, request);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.FAMILY_BACKGROUNDS, key = "#result.userId")
    public FamilyBackgroundResponse createForUser(Integer userId, FamilyBackgroundCreateRequest request) {
        log.info("Creating family background for user ID: {}", userId);

        try {
            validationService.validateCreateRequest(request);
            
            User user = userRepo.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found during family background creation: {}", userId);
                        return new ResourceNotFoundException("User not found");
                    });

            if (familyBackgroundRepo.existsByUser_Id(userId)) {
                log.warn("Attempt to create duplicate family background for user: {}", userId);
                throw new ResourceAlreadyExistsException("Family background already exists for this user");
            }

            FamilyBackground entity = mapper.toEntity(request, user);

            entity.setCreatedBy(userId);
            entity.setUpdatedBy(userId);
            
            FamilyBackground saved = familyBackgroundRepo.save(entity);
            log.info("Family background created successfully with ID: {} for user: {}", 
                    saved.getFamilyBackgroundId(), userId);

            synchronizeCompleteProfileAsync(user, saved);

            return mapper.toResponse(saved);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during family background creation for user {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during family background creation for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to create family background", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.FAMILY_BACKGROUNDS, key = "#root.target.getCurrentUserId()")
    public FamilyBackgroundResponse getCurrentUserFamilyBackground() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching family background for authenticated user ID: {}", currentUserId);

        FamilyBackground familyBackground = familyBackgroundRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Family background not found for user ID: " + currentUserId));

        return mapper.toResponse(familyBackground);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.FAMILY_BACKGROUNDS, key = "#userId")
    public FamilyBackgroundResponse getByUserId(Integer userId) {
        log.debug("Admin fetching family background for user ID: {}", userId);

        FamilyBackground familyBackground = familyBackgroundRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Family background not found for user ID: " + userId));

        return mapper.toResponse(familyBackground);
    }


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.FAMILY_BACKGROUNDS, key = "#root.target.getCurrentUserId()")
    public FamilyBackgroundResponse updateCurrentUserFamilyBackground(FamilyBackgroundUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Updating family background for authenticated user ID: {}", currentUserId);

        try {
            validationService.validateUpdateRequest(request);
            FamilyBackground existing = familyBackgroundRepo.findByUser_Id(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Family background not found for user ID: " + currentUserId));

            if (!existing.getVersion().equals(request.getVersion())) {
                log.warn("Version conflict for family background user {}: expected {}, got {}",
                        currentUserId, existing.getVersion(), request.getVersion());
                throw new OptimisticLockingFailureException(
                        "Family background has been modified by another transaction. Please refresh and try again.");
            }
            mapper.applyUpdate(request, existing);

            existing.setUpdatedBy(currentUserId);

            FamilyBackground saved = familyBackgroundRepo.save(existing);
            log.info("Family background updated successfully for user: {} to version: {}", currentUserId, saved.getVersion());

            recalculateCompleteProfileAsync(existing.getUser(), saved);

            return mapper.toResponse(saved);
            
        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception during family background update: {}", e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Family background has been modified by another transaction. Please refresh and try again.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during family background update for user {}: {}", currentUserId, e.getMessage());
            throw e; // Re-throw to be handled by exception handler
        } catch (Exception e) {
            log.error("Unexpected error during family background update for user {}: {}", currentUserId, e.getMessage(), e);
            throw new RuntimeException("Failed to update family background", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.CacheNames.FAMILY_BACKGROUNDS, key = "#root.target.getCurrentUserId()")
    public void deleteCurrentUserFamilyBackground() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Soft deleting family background for authenticated user ID: {}", currentUserId);

        FamilyBackground existing = familyBackgroundRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Family background not found for user ID: " + currentUserId));

        existing.setDeleted(true);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        existing.setDeletedBy(currentUserId);

        familyBackgroundRepo.save(existing);
        
        log.info("Family background soft deleted successfully for user: {}", currentUserId);

        updateCompleteProfileAfterDeletion(currentUserId);
    }

    /**
     * Get current user ID for caching key generation.
     * Used by @Cacheable SpEL expressions.
     */
    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    /**
     * Asynchronously synchronize CompleteProfile when family background is created.
     */
    private void synchronizeCompleteProfileAsync(User user, FamilyBackground familyBackground) {
        // Skip async updates during admin registration for performance
        if (com.spring.jwt.admin.service.AdminRegistrationContext.isAdminRegistration()) {
            log.debug("Skipping CompleteProfile sync during admin registration for user ID: {}", user.getId());
            return;
        }
        
        try {
            CompleteProfile cp = completeProfileRepo.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        CompleteProfile newCp = new CompleteProfile();
                        newCp.setUser(user);
                        newCp.setProfileCompleted(false);
                        return newCp;
                    });
            
            cp.setFamilyBackground(familyBackground);
            CompleteProfile savedCp = completeProfileRepo.save(cp);

            completeProfileService.recalcAndSave(savedCp);
            
            log.debug("CompleteProfile synchronized for user ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error synchronizing CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Asynchronously recalculate CompleteProfile when family background is updated.
     */
    private void recalculateCompleteProfileAsync(User user, FamilyBackground familyBackground) {
        try {
            completeProfileRepo.findByUser_Id(user.getId())
                    .ifPresent(cp -> {
                        cp.setFamilyBackground(familyBackground);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error recalculating CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Update CompleteProfile after family background deletion.
     */
    private void updateCompleteProfileAfterDeletion(Integer userId) {
        try {
            completeProfileRepo.findByUser_Id(userId)
                    .ifPresent(cp -> {
                        cp.setFamilyBackground(null);
                        cp.setProfileCompleted(false);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error updating CompleteProfile after deletion for user {}: {}", userId, e.getMessage(), e);
        }
    }
}
