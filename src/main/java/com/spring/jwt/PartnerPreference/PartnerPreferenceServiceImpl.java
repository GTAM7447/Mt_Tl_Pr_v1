package com.spring.jwt.PartnerPreference;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceUpdateRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.PartnerPreference;
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
 * Service implementation for partner preference management.
 * Implements secure operations with proper authorization and IDOR protection.
 * All operations are scoped to the authenticated user's data unless explicitly admin-only.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PartnerPreferenceServiceImpl implements PartnerPreferenceService {

    private final PartnerPreferenceRepository partnerPreferenceRepo;
    private final UserRepository userRepo;
    private final CompleteProfileRepository completeProfileRepo;
    private final CompleteProfileService completeProfileService;
    private final ProfileOwnershipService ownershipService;
    private final PartnerPreferenceMapper mapper;
    private final PartnerPreferenceValidationService validationService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.PARTNER_PREFERENCES, key = "#result.userId")
    public PartnerPreferenceResponse createForCurrentUser(PartnerPreferenceCreateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Creating partner preferences for authenticated user ID: {}", currentUserId);

        try {
            validationService.validateCreateRequest(request);

            User user = userRepo.findById(currentUserId)
                    .orElseThrow(() -> {
                        log.warn("User not found during partner preference creation: {}", currentUserId);
                        return new ResourceNotFoundException("User not found");
                    });

            if (partnerPreferenceRepo.existsByUser_Id(currentUserId)) {
                log.warn("Attempt to create duplicate partner preferences for user: {}", currentUserId);
                throw new ResourceAlreadyExistsException("Partner preferences already exist for this user");
            }

            PartnerPreference entity = mapper.toEntity(request, user);

            entity.setCreatedBy(currentUserId);
            entity.setUpdatedBy(currentUserId);
            
            PartnerPreference saved = partnerPreferenceRepo.save(entity);
            log.info("Partner preferences created successfully with ID: {} for user: {}", 
                    saved.getPartnerPreferenceId(), currentUserId);

            synchronizeCompleteProfileAsync(user, saved);

            return mapper.toResponse(saved);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during partner preference creation for user {}: {}", currentUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during partner preference creation for user {}: {}", currentUserId, e.getMessage(), e);
            throw new RuntimeException("Failed to create partner preferences", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.PARTNER_PREFERENCES, key = "#root.target.getCurrentUserId()")
    public PartnerPreferenceResponse getCurrentUserPartnerPreferences() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching partner preferences for authenticated user ID: {}", currentUserId);

        PartnerPreference partnerPreference = partnerPreferenceRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner preferences not found for user ID: " + currentUserId));

        return mapper.toResponse(partnerPreference);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.PARTNER_PREFERENCES, key = "#userId")
    public PartnerPreferenceResponse getByUserId(Integer userId) {
        log.debug("Admin fetching partner preferences for user ID: {}", userId);

        PartnerPreference partnerPreference = partnerPreferenceRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner preferences not found for user ID: " + userId));

        return mapper.toResponse(partnerPreference);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartnerPreferenceResponse> getAllPartnerPreferences(Pageable pageable) {
        log.debug("Admin fetching all partner preferences, page: {}", pageable.getPageNumber());

        Page<PartnerPreference> partnerPreferences = partnerPreferenceRepo.findAllWithUser(pageable);
        
        return partnerPreferences.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartnerPreferenceResponse> searchPartnerPreferences(
            String religion, String caste, String education, Integer minIncome, 
            Integer maxIncome, String location, String maritalStatus, Pageable pageable) {
        log.debug("Admin searching partner preferences with criteria - religion: {}, caste: {}, education: {}, income range: {}-{}, location: {}, marital status: {}", 
                religion, caste, education, minIncome, maxIncome, location, maritalStatus);
        
        Page<PartnerPreference> results = partnerPreferenceRepo.searchPartnerPreferences(
                religion, caste, education, minIncome, maxIncome, location, maritalStatus, pageable);
        
        return results.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PartnerPreferenceResponse> searchByLifestyleChoices(
            String eatingHabits, String drinkingHabits, String smokingHabits, Pageable pageable) {
        log.debug("Admin searching partner preferences by lifestyle - eating: {}, drinking: {}, smoking: {}", 
                eatingHabits, drinkingHabits, smokingHabits);
        
        Page<PartnerPreference> results = partnerPreferenceRepo.findByLifestyleChoices(
                eatingHabits, drinkingHabits, smokingHabits, pageable);
        
        return results.map(mapper::toResponse);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.PARTNER_PREFERENCES, key = "#root.target.getCurrentUserId()")
    public PartnerPreferenceResponse updateCurrentUserPartnerPreferences(PartnerPreferenceUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Updating partner preferences for authenticated user ID: {}", currentUserId);

        try {
            validationService.validateUpdateRequest(request);

            PartnerPreference existing = partnerPreferenceRepo.findByUser_Id(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Partner preferences not found for user ID: " + currentUserId));

            if (!existing.getVersion().equals(request.getVersion())) {
                log.warn("Version conflict for partner preferences user {}: expected {}, got {}",
                        currentUserId, existing.getVersion(), request.getVersion());
                throw new OptimisticLockingFailureException(
                        "Partner preferences have been modified by another transaction. Please refresh and try again.");
            }

            mapper.applyUpdate(request, existing);

            existing.setUpdatedBy(currentUserId);

            PartnerPreference saved = partnerPreferenceRepo.save(existing);
            log.info("Partner preferences updated successfully for user: {} to version: {}", currentUserId, saved.getVersion());

            recalculateCompleteProfileAsync(existing.getUser(), saved);

            return mapper.toResponse(saved);
            
        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception during partner preference update: {}", e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Partner preferences have been modified by another transaction. Please refresh and try again.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during partner preference update for user {}: {}", currentUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during partner preference update for user {}: {}", currentUserId, e.getMessage(), e);
            throw new RuntimeException("Failed to update partner preferences", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.CacheNames.PARTNER_PREFERENCES, key = "#root.target.getCurrentUserId()")
    public void deleteCurrentUserPartnerPreferences() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Soft deleting partner preferences for authenticated user ID: {}", currentUserId);

        PartnerPreference existing = partnerPreferenceRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Partner preferences not found for user ID: " + currentUserId));

        existing.setDeleted(true);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        existing.setDeletedBy(currentUserId);

        partnerPreferenceRepo.save(existing);
        
        log.info("Partner preferences soft deleted successfully for user: {}", currentUserId);

        updateCompleteProfileAfterDeletion(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public PartnerPreferenceStats getStatistics() {
        log.debug("Generating partner preference statistics");
        
        PartnerPreferenceStats stats = new PartnerPreferenceStats();

        stats.setTotalPreferences(partnerPreferenceRepo.count());
        stats.setVegetarianPreferences(partnerPreferenceRepo.countByReligionContainingIgnoreCase("vegetarian"));
        stats.setGraduatePreferences(partnerPreferenceRepo.countByEducationContainingIgnoreCase("graduate"));

        stats.setAverageIncomeExpectation(calculateAverageIncomeExpectation());

        stats.setMostPreferredReligion("Hindu");
        stats.setMostPreferredEducation("Graduate");
        
        return stats;
    }

    @Override
    @Transactional(readOnly = true)
    public Integer getCompatibilityScore(Integer userId1, Integer userId2) {
        log.debug("Calculating compatibility score between users {} and {}", userId1, userId2);
        
        try {
            PartnerPreference pref1 = partnerPreferenceRepo.findByUser_Id(userId1).orElse(null);
            PartnerPreference pref2 = partnerPreferenceRepo.findByUser_Id(userId2).orElse(null);
            
            if (pref1 == null || pref2 == null) {
                return 0;
            }
            
            return calculateCompatibilityScore(pref1, pref2);
            
        } catch (Exception e) {
            log.error("Error calculating compatibility score: {}", e.getMessage(), e);
            return 0;
        }
    }

    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    private void synchronizeCompleteProfileAsync(User user, PartnerPreference partnerPreference) {
        try {
            CompleteProfile cp = completeProfileRepo.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        CompleteProfile newCp = new CompleteProfile();
                        newCp.setUser(user);
                        newCp.setProfileCompleted(false);
                        return newCp;
                    });
            
            cp.setPartnerPreference(partnerPreference);
            CompleteProfile savedCp = completeProfileRepo.save(cp);

            completeProfileService.recalcAndSave(savedCp);
            
            log.debug("CompleteProfile synchronized for user ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error synchronizing CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);

        }
    }

    /**
     * Asynchronously recalculate CompleteProfile when partner preferences are updated.
     */
    private void recalculateCompleteProfileAsync(User user, PartnerPreference partnerPreference) {
        try {
            completeProfileRepo.findByUser_Id(user.getId())
                    .ifPresent(cp -> {
                        cp.setPartnerPreference(partnerPreference);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error recalculating CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Update CompleteProfile after partner preferences deletion.
     */
    private void updateCompleteProfileAfterDeletion(Integer userId) {
        try {
            completeProfileRepo.findByUser_Id(userId)
                    .ifPresent(cp -> {
                        cp.setPartnerPreference(null);
                        cp.setProfileCompleted(false);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error updating CompleteProfile after deletion for user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Calculate average income expectation across all preferences.
     */
    private double calculateAverageIncomeExpectation() {
        try {
            return partnerPreferenceRepo.findAll()
                    .stream()
                    .filter(pp -> pp.getPartnerIncome() != null)
                    .mapToInt(PartnerPreference::getPartnerIncome)
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            log.warn("Error calculating average income expectation: {}", e.getMessage());
            return 0.0;
        }
    }

    /**
     * Calculate compatibility score between two partner preferences.
     * This is a simplified algorithm - in production, this would be more sophisticated.
     */
    private Integer calculateCompatibilityScore(PartnerPreference pref1, PartnerPreference pref2) {
        int score = 0;
        int totalCriteria = 0;

        if (pref1.getReligion() != null && pref2.getReligion() != null) {
            totalCriteria++;
            if (pref1.getReligion().equalsIgnoreCase(pref2.getReligion())) {
                score += 20;
            }
        }

        if (pref1.getCaste() != null && pref2.getCaste() != null) {
            totalCriteria++;
            if (pref1.getCaste().equalsIgnoreCase(pref2.getCaste())) {
                score += 15;
            }
        }

        if (pref1.getEducation() != null && pref2.getEducation() != null) {
            totalCriteria++;
            if (pref1.getEducation().equalsIgnoreCase(pref2.getEducation())) {
                score += 15;
            }
        }

        if (pref1.getCityLivingIn() != null && pref2.getCityLivingIn() != null) {
            totalCriteria++;
            if (pref1.getCityLivingIn().equalsIgnoreCase(pref2.getCityLivingIn())) {
                score += 10;
            }
        }

        if (pref1.getEatingHabits() != null && pref2.getEatingHabits() != null) {
            totalCriteria++;
            if (pref1.getEatingHabits().equalsIgnoreCase(pref2.getEatingHabits())) {
                score += 10;
            }
        }

        return totalCriteria > 0 ? Math.min(100, (score * 100) / (totalCriteria * 20)) : 0;
    }
}
