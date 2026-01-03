package com.spring.jwt.EducationAndProfession;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionUpdateRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.EducationAndProfession;
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
 * Service implementation for education and profession management.
 * Implements secure operations with proper authorization and IDOR protection.
 * All operations are scoped to the authenticated user's data unless explicitly admin-only.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EducationAndProfessionServiceImpl implements EducationAndProfessionService {

    private final EducationAndProfessionRepository educationAndProfessionRepo;
    private final UserRepository userRepo;
    private final CompleteProfileRepository completeProfileRepo;
    private final CompleteProfileService completeProfileService;
    private final ProfileOwnershipService ownershipService;
    private final EducationAndProfessionMapper mapper;
    private final EducationAndProfessionValidationService validationService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.EDUCATION_PROFILES, key = "#result.userId")
    public EducationAndProfessionResponse createForCurrentUser(EducationAndProfessionCreateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Creating education and profession for authenticated user ID: {}", currentUserId);

        try {
            validationService.validateCreateRequest(request);

            User user = userRepo.findById(currentUserId)
                    .orElseThrow(() -> {
                        log.warn("User not found during education and profession creation: {}", currentUserId);
                        return new ResourceNotFoundException("User not found");
                    });

            if (educationAndProfessionRepo.existsByUser_Id(currentUserId)) {
                log.warn("Attempt to create duplicate education and profession for user: {}", currentUserId);
                throw new ResourceAlreadyExistsException("Education and profession already exists for this user");
            }

            EducationAndProfession entity = mapper.toEntity(request, user);

            entity.setCreatedBy(currentUserId);
            entity.setUpdatedBy(currentUserId);
            
            EducationAndProfession saved = educationAndProfessionRepo.save(entity);
            log.info("Education and profession created successfully with ID: {} for user: {}", 
                    saved.getEducationId(), currentUserId);

            synchronizeCompleteProfileAsync(user, saved);

            return mapper.toResponse(saved);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during education and profession creation for user {}: {}", currentUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during education and profession creation for user {}: {}", currentUserId, e.getMessage(), e);
            throw new RuntimeException("Failed to create education and profession", e);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.EDUCATION_PROFILES, key = "#userId")
    public EducationAndProfessionResponse createForUser(Integer userId, EducationAndProfessionCreateRequest request) {
        Integer adminUserId = ownershipService.getCurrentUserId();
        log.info("Admin (ID: {}) creating education and profession for user ID: {}", adminUserId, userId);

        try {
            validationService.validateCreateRequest(request);

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found during education and profession creation: {}", userId);
                        return new ResourceNotFoundException("User not found with ID: " + userId);
                    });

            if (educationAndProfessionRepo.existsByUser_Id(userId)) {
                log.warn("Attempt to create duplicate education and profession for user: {}", userId);
                throw new ResourceAlreadyExistsException("Education and profession already exists for user ID: " + userId);
            }

            EducationAndProfession entity = mapper.toEntity(request, user);

            entity.setCreatedBy(adminUserId);
            entity.setUpdatedBy(adminUserId);
            
            EducationAndProfession saved = educationAndProfessionRepo.save(entity);
            log.info("Education and profession created successfully with ID: {} for user: {} by admin: {}", 
                    saved.getEducationId(), userId, adminUserId);

            synchronizeCompleteProfileAsync(user, saved);

            return mapper.toResponse(saved);
            
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during education and profession creation for user {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during education and profession creation for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to create education and profession", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.EDUCATION_PROFILES, key = "#root.target.getCurrentUserId()")
    public EducationAndProfessionResponse getCurrentUserEducationAndProfession() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching education and profession for authenticated user ID: {}", currentUserId);

        EducationAndProfession educationAndProfession = educationAndProfessionRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Education and profession not found for user ID: " + currentUserId));

        return mapper.toResponse(educationAndProfession);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.EDUCATION_PROFILES, key = "#userId")
    public EducationAndProfessionResponse getByUserId(Integer userId) {
        log.debug("Admin fetching education and profession for user ID: {}", userId);

        EducationAndProfession educationAndProfession = educationAndProfessionRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Education and profession not found for user ID: " + userId));

        return mapper.toResponse(educationAndProfession);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EducationAndProfessionResponse> getAllEducationAndProfession(Pageable pageable) {
        log.debug("Admin fetching all education and profession records, page: {}", pageable.getPageNumber());

        Page<EducationAndProfession> educationAndProfessionRecords = educationAndProfessionRepo.findAllWithUser(pageable);
        
        return educationAndProfessionRecords.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<EducationAndProfessionResponse> searchEducationAndProfession(
            String occupation, String education, Integer minIncome, 
            Integer maxIncome, String workLocation, Pageable pageable) {
        log.debug("Admin searching education and profession records with criteria - occupation: {}, education: {}, income range: {}-{}, location: {}", 
                occupation, education, minIncome, maxIncome, workLocation);
        
        Page<EducationAndProfession> results = educationAndProfessionRepo.searchEducationAndProfession(
                occupation, education, minIncome, maxIncome, workLocation, pageable);
        
        return results.map(mapper::toResponse);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.EDUCATION_PROFILES, key = "#root.target.getCurrentUserId()")
    public EducationAndProfessionResponse updateCurrentUserEducationAndProfession(EducationAndProfessionUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Updating education and profession for authenticated user ID: {}", currentUserId);

        try {
            validationService.validateUpdateRequest(request);

            EducationAndProfession existing = educationAndProfessionRepo.findByUser_Id(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Education and profession not found for user ID: " + currentUserId));

            if (!existing.getVersion().equals(request.getVersion())) {
                log.warn("Version conflict for education and profession user {}: expected {}, got {}",
                        currentUserId, existing.getVersion(), request.getVersion());
                throw new OptimisticLockingFailureException(
                        "Education and profession has been modified by another transaction. Please refresh and try again.");
            }

            mapper.applyUpdate(request, existing);

            existing.setUpdatedBy(currentUserId);

            EducationAndProfession saved = educationAndProfessionRepo.save(existing);
            log.info("Education and profession updated successfully for user: {} to version: {}", currentUserId, saved.getVersion());

            recalculateCompleteProfileAsync(existing.getUser(), saved);

            return mapper.toResponse(saved);
            
        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception during education and profession update: {}", e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Education and profession has been modified by another transaction. Please refresh and try again.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during education and profession update for user {}: {}", currentUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during education and profession update for user {}: {}", currentUserId, e.getMessage(), e);
            throw new RuntimeException("Failed to update education and profession", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.CacheNames.EDUCATION_PROFILES, key = "#root.target.getCurrentUserId()")
    public void deleteCurrentUserEducationAndProfession() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Soft deleting education and profession for authenticated user ID: {}", currentUserId);

        EducationAndProfession existing = educationAndProfessionRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Education and profession not found for user ID: " + currentUserId));

        existing.setDeleted(true);
        existing.setDeletedAt(java.time.LocalDateTime.now());
        existing.setDeletedBy(currentUserId);

        educationAndProfessionRepo.save(existing);
        
        log.info("Education and profession soft deleted successfully for user: {}", currentUserId);

        updateCompleteProfileAfterDeletion(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public EducationAndProfessionStats getStatistics() {
        log.debug("Generating education and profession statistics");
        
        EducationAndProfessionStats stats = new EducationAndProfessionStats();

        stats.setTotalRecords(educationAndProfessionRepo.count());
        stats.setEngineerCount(educationAndProfessionRepo.countByOccupationContainingIgnoreCase("engineer"));
        stats.setGraduateCount(educationAndProfessionRepo.countByEducationContainingIgnoreCase("bachelor"));

        stats.setAverageIncome(calculateAverageIncome());
        
        return stats;
    }

    /**
     * Get current user ID for caching key generation.
     * Used by @Cacheable SpEL expressions.
     */
    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    /**
     * Asynchronously synchronize CompleteProfile when education and profession is created.
     */
    private void synchronizeCompleteProfileAsync(User user, EducationAndProfession educationAndProfession) {
        try {
            CompleteProfile cp = completeProfileRepo.findByUser_Id(user.getId())
                    .orElseGet(() -> {
                        CompleteProfile newCp = new CompleteProfile();
                        newCp.setUser(user);
                        newCp.setProfileCompleted(false);
                        return newCp;
                    });
            
            cp.setEducationAndProfession(educationAndProfession);
            CompleteProfile savedCp = completeProfileRepo.save(cp);

            completeProfileService.recalcAndSave(savedCp);
            
            log.debug("CompleteProfile synchronized for user ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error synchronizing CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Asynchronously recalculate CompleteProfile when education and profession is updated.
     */
    private void recalculateCompleteProfileAsync(User user, EducationAndProfession educationAndProfession) {
        try {
            completeProfileRepo.findByUser_Id(user.getId())
                    .ifPresent(cp -> {
                        cp.setEducationAndProfession(educationAndProfession);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error recalculating CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Update CompleteProfile after education and profession deletion.
     */
    private void updateCompleteProfileAfterDeletion(Integer userId) {
        try {
            completeProfileRepo.findByUser_Id(userId)
                    .ifPresent(cp -> {
                        cp.setEducationAndProfession(null);
                        cp.setProfileCompleted(false); // Mark as incomplete
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error updating CompleteProfile after deletion for user {}: {}", userId, e.getMessage(), e);
        }
    }

    /**
     * Calculate average income across all records.
     * This is a simplified implementation - in production, this should be done with a database query.
     */
    private double calculateAverageIncome() {
        try {
            return educationAndProfessionRepo.findAll()
                    .stream()
                    .mapToInt(EducationAndProfession::getIncomePerYear)
                    .average()
                    .orElse(0.0);
        } catch (Exception e) {
            log.warn("Error calculating average income: {}", e.getMessage());
            return 0.0;
        }
    }
}
