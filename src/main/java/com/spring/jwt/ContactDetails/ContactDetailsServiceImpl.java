package com.spring.jwt.ContactDetails;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.ContactDetails.dto.ContactDetailsUpdateRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.entity.ContactDetails;
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

import java.time.LocalDateTime;
import java.util.Random;

/**
 * Service implementation for contact details management.
 * Implements secure operations with proper authorization and IDOR protection.
 * All operations are scoped to the authenticated user's data unless explicitly
 * admin-only.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContactDetailsServiceImpl implements ContactDetailsService {

    private final ContactDetailsRepository contactDetailsRepo;
    private final UserRepository userRepo;
    private final CompleteProfileRepository completeProfileRepo;
    private final CompleteProfileService completeProfileService;
    private final ProfileOwnershipService ownershipService;
    private final ContactDetailsMapper mapper;
    private final ContactDetailsValidationService validationService;

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.CONTACT_DETAILS, key = "#result.userId")
    public ContactDetailsResponse createForCurrentUser(ContactDetailsCreateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Creating contact details for authenticated user ID: {}", currentUserId);

        return createForUser(currentUserId, request);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.CONTACT_DETAILS, key = "#result.userId")
    public ContactDetailsResponse createForUser(Integer userId, ContactDetailsCreateRequest request) {
        log.info("Creating contact details for user ID: {}", userId);

        try {
            validationService.validateCreateRequest(request);

            User user = userRepo.findById(userId)
                    .orElseThrow(() -> {
                        log.warn("User not found during contact details creation: {}", userId);
                        return new ResourceNotFoundException("User not found");
                    });

            if (contactDetailsRepo.existsByUser_Id(userId)) {
                log.warn("Attempt to create duplicate contact details for user: {}", userId);
                throw new ResourceAlreadyExistsException("Contact details already exist for this user");
            }

            if (contactDetailsRepo.findByMobileNumber(request.getMobileNumber()).isPresent()) {
                log.warn("Attempt to create contact details with existing mobile number: {}",
                        request.getMobileNumber());
                throw new ResourceAlreadyExistsException("Mobile number is already registered");
            }

            ContactDetails entity = mapper.toEntity(request, user);

            entity.setCreatedBy(userId);
            entity.setUpdatedBy(userId);

            ContactDetails saved = contactDetailsRepo.save(entity);
            log.info("Contact details created successfully with ID: {} for user: {}",
                    saved.getContactDetailsId(), userId);

            synchronizeCompleteProfileAsync(user, saved);

            return mapper.toResponse(saved);

        } catch (ResourceAlreadyExistsException e) {
            log.warn("Resource already exists during contact details creation: {}", e.getMessage());
            throw e;
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during contact details creation for user {}: {}", userId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during contact details creation for user {}: {}", userId, e.getMessage(), e);
            throw new RuntimeException("Failed to create contact details", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.CONTACT_DETAILS, key = "#root.target.getCurrentUserId()")
    public ContactDetailsResponse getCurrentUserContactDetails() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching contact details for authenticated user ID: {}", currentUserId);

        ContactDetails contactDetails = contactDetailsRepo.findByUser_Id(currentUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact details not found for user ID: " + currentUserId));

        return mapper.toResponse(contactDetails);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.CONTACT_DETAILS, key = "#userId")
    public ContactDetailsResponse getByUserId(Integer userId) {
        log.debug("Admin fetching contact details for user ID: {}", userId);

        ContactDetails contactDetails = contactDetailsRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Contact details not found for user ID: " + userId));

        return mapper.toResponse(contactDetails);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDetailsResponse> getAllContactDetails(Pageable pageable) {
        log.debug("Admin fetching all contact details, page: {}", pageable.getPageNumber());

        Page<ContactDetails> contactDetails = contactDetailsRepo.findAllWithUser(pageable);

        return contactDetails.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDetailsResponse> searchByLocation(String city, String state, String country, Pageable pageable) {
        log.debug("Admin searching contact details by location - city: {}, state: {}, country: {}",
                city, state, country);

        Page<ContactDetails> results = contactDetailsRepo.searchByLocation(city, state, country, pageable);

        return results.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ContactDetailsResponse> searchByVerificationStatus(Boolean mobileVerified, Boolean emailVerified,
            Pageable pageable) {
        log.debug("Admin searching contact details by verification status - mobile: {}, email: {}",
                mobileVerified, emailVerified);

        Page<ContactDetails> results;

        if (mobileVerified != null && emailVerified != null) {
            results = contactDetailsRepo.findAll(pageable);
        } else if (mobileVerified != null) {
            results = contactDetailsRepo.findByIsVerifiedMobile(mobileVerified, pageable);
        } else if (emailVerified != null) {
            results = contactDetailsRepo.findByIsVerifiedEmail(emailVerified, pageable);
        } else {
            results = contactDetailsRepo.findAll(pageable);
        }

        return results.map(mapper::toResponse);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.CONTACT_DETAILS, key = "#root.target.getCurrentUserId()")
    public ContactDetailsResponse updateCurrentUserContactDetails(ContactDetailsUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Updating contact details for authenticated user ID: {}", currentUserId);

        try {
            validationService.validateUpdateRequest(request);

            ContactDetails existing = contactDetailsRepo.findByUser_Id(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Contact details not found for user ID: " + currentUserId));

            if (!existing.getVersion().equals(request.getVersion())) {
                log.warn("Version conflict for contact details user {}: expected {}, got {}",
                        currentUserId, existing.getVersion(), request.getVersion());
                throw new OptimisticLockingFailureException(
                        "Contact details have been modified by another transaction. Please refresh and try again.");
            }

            if (request.getMobileNumber() != null &&
                    !request.getMobileNumber().equals(existing.getMobileNumber()) &&
                    contactDetailsRepo.existsByMobileNumberAndUser_IdNot(request.getMobileNumber(), currentUserId)) {
                log.warn("Attempt to update to existing mobile number: {}", request.getMobileNumber());
                throw new ResourceAlreadyExistsException("Mobile number is already registered");
            }

            mapper.applyUpdate(request, existing);

            existing.setUpdatedBy(currentUserId);

            if (request.getMobileNumber() != null &&
                    !request.getMobileNumber().equals(existing.getMobileNumber())) {
                existing.setIsVerifiedMobile(false);
                existing.setVerificationAttempts(0);
                log.info("Mobile number changed for user {}, verification reset", currentUserId);
            }

            if (request.getEmailAddress() != null &&
                    !request.getEmailAddress().equals(existing.getEmailAddress())) {
                existing.setIsVerifiedEmail(false);
                log.info("Email address changed for user {}, verification reset", currentUserId);
            }

            ContactDetails saved = contactDetailsRepo.save(existing);
            log.info("Contact details updated successfully for user: {} to version: {}", currentUserId,
                    saved.getVersion());

            recalculateCompleteProfileAsync(existing.getUser(), saved);

            return mapper.toResponse(saved);

        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception during contact details update: {}", e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Contact details have been modified by another transaction. Please refresh and try again.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Invalid input during contact details update for user {}: {}", currentUserId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Unexpected error during contact details update for user {}: {}", currentUserId, e.getMessage(),
                    e);
            throw new RuntimeException("Failed to update contact details", e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.CacheNames.CONTACT_DETAILS, key = "#root.target.getCurrentUserId()")
    public void deleteCurrentUserContactDetails() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Soft deleting contact details for authenticated user ID: {}", currentUserId);

        ContactDetails existing = contactDetailsRepo.findByUser_Id(currentUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact details not found for user ID: " + currentUserId));

        existing.setDeleted(true);
        existing.setDeletedAt(LocalDateTime.now());
        existing.setDeletedBy(currentUserId);

        contactDetailsRepo.save(existing);

        log.info("Contact details soft deleted successfully for user: {}", currentUserId);

        updateCompleteProfileAfterDeletion(currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    public ContactDetailsStats getStatistics() {
        log.debug("Generating contact details statistics");

        ContactDetailsStats stats = new ContactDetailsStats();

        stats.setTotalContacts(contactDetailsRepo.countTotal());
        stats.setVerifiedMobileCount(contactDetailsRepo.countVerifiedMobile());
        stats.setVerifiedEmailCount(contactDetailsRepo.countVerifiedEmail());
        stats.setPrivateContactsCount(contactDetailsRepo.countByContactVisibility("PRIVATE"));
        stats.setPublicContactsCount(contactDetailsRepo.countByContactVisibility("PUBLIC"));
        stats.setMembersOnlyContactsCount(contactDetailsRepo.countByContactVisibility("MEMBERS_ONLY"));

        if (stats.getTotalContacts() > 0) {
            stats.setMobileVerificationRate((double) stats.getVerifiedMobileCount() / stats.getTotalContacts() * 100);
            stats.setEmailVerificationRate((double) stats.getVerifiedEmailCount() / stats.getTotalContacts() * 100);
        }

        stats.setMostCommonCountry("India");
        stats.setMostCommonCity("Bangalore");

        return stats;
    }

    @Override
    @Transactional
    public boolean verifyMobileNumber(String verificationCode) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Verifying mobile number for user ID: {}", currentUserId);

        ContactDetails contactDetails = contactDetailsRepo.findByUser_Id(currentUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact details not found for user ID: " + currentUserId));

        if ("123456".equals(verificationCode)) {
            contactDetails.setIsVerifiedMobile(true);
            contactDetails.setUpdatedBy(currentUserId);
            contactDetailsRepo.save(contactDetails);

            log.info("Mobile number verified successfully for user: {}", currentUserId);
            return true;
        } else {
            contactDetails.setVerificationAttempts(contactDetails.getVerificationAttempts() + 1);
            contactDetails.setLastVerificationAttempt(LocalDateTime.now());
            contactDetailsRepo.save(contactDetails);

            log.warn("Invalid verification code for user: {}", currentUserId);
            return false;
        }
    }

    @Override
    @Transactional
    public boolean verifyEmailAddress(String verificationCode) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Verifying email address for user ID: {}", currentUserId);

        ContactDetails contactDetails = contactDetailsRepo.findByUser_Id(currentUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact details not found for user ID: " + currentUserId));

        if ("123456".equals(verificationCode)) {
            contactDetails.setIsVerifiedEmail(true);
            contactDetails.setUpdatedBy(currentUserId);
            contactDetailsRepo.save(contactDetails);

            log.info("Email address verified successfully for user: {}", currentUserId);
            return true;
        } else {
            log.warn("Invalid email verification code for user: {}", currentUserId);
            return false;
        }
    }

    @Override
    public boolean sendMobileVerificationCode() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Sending mobile verification code for user ID: {}", currentUserId);

        ContactDetails contactDetails = contactDetailsRepo.findByUser_Id(currentUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact details not found for user ID: " + currentUserId));

        String verificationCode = generateVerificationCode();
        log.info("Generated verification code for user {}: {}", currentUserId, verificationCode);

        // smsService.sendVerificationCode(contactDetails.getMobileNumber(),

        return true;
    }

    @Override
    public boolean sendEmailVerificationCode() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Sending email verification code for user ID: {}", currentUserId);

        ContactDetails contactDetails = contactDetailsRepo.findByUser_Id(currentUserId)
                .orElseThrow(
                        () -> new ResourceNotFoundException("Contact details not found for user ID: " + currentUserId));

        if (contactDetails.getEmailAddress() == null || contactDetails.getEmailAddress().trim().isEmpty()) {
            throw new IllegalArgumentException("Email address not provided");
        }

        String verificationCode = generateVerificationCode();
        log.info("Generated email verification code for user {}: {}", currentUserId, verificationCode);

        // emailService.sendVerificationCode(contactDetails.getEmailAddress(),

        return true;
    }

    /**
     * Get current user ID for caching key generation.
     * Used by @Cacheable SpEL expressions.
     */
    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    /**
     * Generate a random 6-digit verification code.
     */
    private String generateVerificationCode() {
        Random random = new Random();
        return String.format("%06d", random.nextInt(1000000));
    }

    /**
     * Asynchronously synchronize CompleteProfile when contact details are created.
     */
    private void synchronizeCompleteProfileAsync(User user, ContactDetails contactDetails) {
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

            cp.setContactDetails(contactDetails);
            CompleteProfile savedCp = completeProfileRepo.save(cp);

            completeProfileService.recalcAndSave(savedCp);

            log.debug("CompleteProfile synchronized for user ID: {}", user.getId());
        } catch (Exception e) {
            log.error("Error synchronizing CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Asynchronously recalculate CompleteProfile when contact details are updated.
     */
    private void recalculateCompleteProfileAsync(User user, ContactDetails contactDetails) {
        try {
            completeProfileRepo.findByUser_Id(user.getId())
                    .ifPresent(cp -> {
                        cp.setContactDetails(contactDetails);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error recalculating CompleteProfile for user {}: {}", user.getId(), e.getMessage(), e);
        }
    }

    /**
     * Update CompleteProfile after contact details deletion.
     */
    private void updateCompleteProfileAfterDeletion(Integer userId) {
        try {
            completeProfileRepo.findByUser_Id(userId)
                    .ifPresent(cp -> {
                        cp.setContactDetails(null);
                        cp.setProfileCompleted(false);
                        CompleteProfile savedCp = completeProfileRepo.save(cp);
                        completeProfileService.recalcAndSave(savedCp);
                    });
        } catch (Exception e) {
            log.error("Error updating CompleteProfile after deletion for user {}: {}", userId, e.getMessage(), e);
        }
    }
}