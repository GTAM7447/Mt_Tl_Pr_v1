package com.spring.jwt.HoroscopeDetails;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;
import com.spring.jwt.dto.horoscope.HoroscopeUpdateRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.HoroscopeDetails;
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
 * Service implementation for horoscope details management.
 * Implements secure operations with proper authorization and IDOR protection.
 * All operations are scoped to the authenticated user's data unless explicitly admin-only.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HoroscopeDetailsServiceImpl implements HoroscopeDetailsService {

    private final HoroscopeDetailsRepository horoscopeRepo;
    private final UserRepository userRepo;
    private final CompleteProfileRepository completeProfileRepo;
    private final CompleteProfileService completeProfileService;
    private final ProfileOwnershipService ownershipService;
    private final HoroscopeDetailsMapper mapper;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.HOROSCOPES, key = "#root.target.getCurrentUserId()")
    public HoroscopeResponse createForCurrentUser(HoroscopeCreateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Creating horoscope for authenticated user ID: {}", currentUserId);

        return createForUser(currentUserId, request);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.HOROSCOPES, key = "#userId")
    public HoroscopeResponse createForUser(Integer userId, HoroscopeCreateRequest request) {
        log.info("Creating horoscope for user ID: {}", userId);

        User user = userRepo.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + userId));

        if (horoscopeRepo.existsByUser_Id(userId)) {
            throw new ResourceAlreadyExistsException("Horoscope details already exist for user: " + userId);
        }

        HoroscopeDetails entity = mapper.toEntity(request, user);

        entity.setCreatedBy(userId);
        entity.setUpdatedBy(userId);
        
        HoroscopeDetails saved = horoscopeRepo.save(entity);
        log.info("Horoscope created successfully with ID: {} for user: {}", saved.getHoroscopeDetailsId(), userId);

        synchronizeCompleteProfileAsync(user, saved);

        return mapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.HOROSCOPES, key = "#root.target.getCurrentUserId()")
    public HoroscopeResponse getCurrentUserHoroscope() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching horoscope for authenticated user ID: {}", currentUserId);

        HoroscopeDetails horoscope = horoscopeRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Horoscope not found for user ID: " + currentUserId));

        return mapper.toResponse(horoscope);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.HOROSCOPES, key = "#userId")
    public HoroscopeResponse getByUserId(Integer userId) {
        log.debug("Admin fetching horoscope for user ID: {}", userId);

        HoroscopeDetails horoscope = horoscopeRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Horoscope not found for user ID: " + userId));

        return mapper.toResponse(horoscope);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<HoroscopeResponse> getAllHoroscopes(Pageable pageable) {
        log.debug("Admin fetching all horoscopes, page: {}", pageable.getPageNumber());

        Page<HoroscopeDetails> horoscopes = horoscopeRepo.findAll(pageable);
        
        return horoscopes.map(mapper::toResponse);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.HOROSCOPES, key = "#root.target.getCurrentUserId()")
    public HoroscopeResponse updateCurrentUserHoroscope(HoroscopeUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Updating horoscope for authenticated user ID: {}", currentUserId);

        HoroscopeDetails existing = horoscopeRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Horoscope not found for user ID: " + currentUserId));

        if (!existing.getVersion().equals(request.getVersion())) {
            log.warn("Version conflict for horoscope user {}: expected {}, got {}",
                    currentUserId, existing.getVersion(), request.getVersion());
            throw new OptimisticLockingFailureException(
                    "Horoscope has been modified by another transaction. Please refresh and try again.");
        }

        mapper.applyUpdate(request, existing);

        existing.setUpdatedBy(currentUserId);

        try {
            HoroscopeDetails saved = horoscopeRepo.save(existing);
            log.info("Horoscope updated successfully for user: {} to version: {}", currentUserId, saved.getVersion());

            recalculateCompleteProfileAsync(existing.getUser(), saved);

            return mapper.toResponse(saved);
        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception during horoscope update: {}", e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Horoscope has been modified by another transaction. Please refresh and try again.", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.CacheNames.HOROSCOPES, key = "#root.target.getCurrentUserId()")
    public void deleteCurrentUserHoroscope() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Soft deleting horoscope for authenticated user ID: {}", currentUserId);

        HoroscopeDetails existing = horoscopeRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Horoscope not found for user ID: " + currentUserId));

        existing.setDeleted(true);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        existing.setDeletedBy(currentUserId);

        horoscopeRepo.save(existing);
        
        log.info("Horoscope soft deleted successfully for user: {}", currentUserId);

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
     * Asynchronously synchronize CompleteProfile when horoscope is created.
     */
    private void synchronizeCompleteProfileAsync(User user, HoroscopeDetails horoscope) {
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
            
            cp.setHoroscopeDetails(horoscope);
            CompleteProfile savedCp = completeProfileRepo.save(cp);

            completeProfileService.recalcAndSave(savedCp);
            
            log.debug("CompleteProfile synchronized for user ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error synchronizing CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Asynchronously recalculate CompleteProfile when horoscope is updated.
     */
    private void recalculateCompleteProfileAsync(User user, HoroscopeDetails horoscope) {
        try {
            completeProfileRepo.findByUser_Id(user.getId())
                    .ifPresent(cp -> {
                        cp.setHoroscopeDetails(horoscope);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error recalculating CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Update CompleteProfile after horoscope deletion.
     */
    private void updateCompleteProfileAfterDeletion(Integer userId) {
        try {
            completeProfileRepo.findByUser_Id(userId)
                    .ifPresent(cp -> {
                        cp.setHoroscopeDetails(null);
                        cp.setProfileCompleted(false); // Mark as incomplete
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error updating CompleteProfile after deletion for user {}: {}", userId, e.getMessage(), e);
        }
    }
}