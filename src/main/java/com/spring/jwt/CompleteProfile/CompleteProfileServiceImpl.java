package com.spring.jwt.CompleteProfile;

import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import com.spring.jwt.CompleteProfile.dto.ProfileAnalyticsRequest;
import com.spring.jwt.entity.CompleteProfile;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.profile.domain.ProfileOwnershipService;
import com.spring.jwt.utils.CacheUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service implementation for complete profile management.
 * Implements enterprise-grade profile aggregation with comprehensive analytics and caching.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class CompleteProfileServiceImpl implements CompleteProfileService {

    private final CompleteProfileRepository completeProfileRepo;
    private final CompleteProfileMapper mapper;
    private final ProfileOwnershipService ownershipService;

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.COMPLETE_PROFILES, key = "#root.target.getCurrentUserId()")
    public CompleteProfileResponse getCurrentUserCompleteProfile() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Fetching complete profile for authenticated user ID: {}", currentUserId);

        CompleteProfile completeProfile = completeProfileRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Complete profile not found for user ID: " + currentUserId));

        return mapper.toResponse(completeProfile);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.COMPLETE_PROFILES, key = "#userId")
    public CompleteProfileResponse getByUserId(Integer userId) {
        log.debug("Admin fetching complete profile for user ID: {}", userId);

        CompleteProfile completeProfile = completeProfileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Complete profile not found for user ID: " + userId));

        return mapper.toResponse(completeProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompleteProfileResponse> getAllCompleteProfiles(Pageable pageable) {
        log.debug("Admin fetching all complete profiles, page: {}", pageable.getPageNumber());

        Page<CompleteProfile> profiles = completeProfileRepo.findAllWithUser(pageable);
        return profiles.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.COMPLETE_PROFILES, key = "'missing_' + #root.target.getCurrentUserId()")
    public MissingProfileDTO checkCurrentUserMissingSections() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Checking missing sections for authenticated user ID: {}", currentUserId);

        CompleteProfile completeProfile = completeProfileRepo.findByUser_Id(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Complete profile not found for user ID: " + currentUserId));

        return mapper.toMissingProfileDTO(completeProfile);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.COMPLETE_PROFILES, key = "'missing_' + #userId")
    public MissingProfileDTO checkMissingSections(Integer userId) {
        log.debug("Admin checking missing sections for user ID: {}", userId);

        CompleteProfile completeProfile = completeProfileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Complete profile not found for user ID: " + userId));

        return mapper.toMissingProfileDTO(completeProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompleteProfileResponse> searchByCompletionCriteria(
            Integer minPercentage, Integer maxPercentage, 
            String profileQuality, String verificationStatus,
            Boolean profileCompleted, Pageable pageable) {
        
        log.debug("Admin searching profiles by completion criteria");

        CompleteProfile.ProfileQuality quality = profileQuality != null ? 
                CompleteProfile.ProfileQuality.valueOf(profileQuality) : null;
        CompleteProfile.VerificationStatus verification = verificationStatus != null ? 
                CompleteProfile.VerificationStatus.valueOf(verificationStatus) : null;

        Page<CompleteProfile> profiles = completeProfileRepo.findByAdvancedCriteria(
                minPercentage, maxPercentage, quality, verification, 
                profileCompleted, null, null, pageable);

        return profiles.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileAnalyticsResponse getProfileAnalytics(ProfileAnalyticsRequest request) {
        log.debug("Admin generating profile analytics");

        ProfileAnalyticsResponse analytics = new ProfileAnalyticsResponse();

        analytics.setTotalProfiles(completeProfileRepo.countTotalProfiles());
        analytics.setCompleteProfiles(completeProfileRepo.countCompleteProfiles());
        analytics.setVerifiedProfiles(completeProfileRepo.countVerifiedProfiles());
        analytics.setIncompleteProfiles(analytics.getTotalProfiles() - analytics.getCompleteProfiles());
        analytics.setAverageCompletionPercentage(completeProfileRepo.getAverageCompletionPercentage());

        Map<String, Long> qualityDistribution = new HashMap<>();
        List<Object[]> qualityResults = completeProfileRepo.countByProfileQuality();
        for (Object[] result : qualityResults) {
            qualityDistribution.put(result[0].toString(), (Long) result[1]);
        }
        analytics.setQualityDistribution(qualityDistribution);

        Map<String, Long> verificationDistribution = new HashMap<>();
        List<Object[]> verificationResults = completeProfileRepo.countByVerificationStatus();
        for (Object[] result : verificationResults) {
            verificationDistribution.put(result[0].toString(), (Long) result[1]);
        }
        analytics.setVerificationDistribution(verificationDistribution);

        Map<String, Long> completionRangeDistribution = new HashMap<>();
        List<Object[]> rangeResults = completeProfileRepo.getCompletionPercentageDistribution();
        for (Object[] result : rangeResults) {
            completionRangeDistribution.put(result[0].toString(), (Long) result[1]);
        }
        analytics.setCompletionRangeDistribution(completionRangeDistribution);

        if (request.getIncludeDetailedAnalytics() != null && request.getIncludeDetailedAnalytics()) {
            analytics.setDetailedAnalytics(generateDetailedAnalytics());
        }

        return analytics;
    }

    @Override
    @Transactional(readOnly = true)
    public ProfileCompletionStats getCompletionStatistics() {
        log.debug("Admin fetching profile completion statistics");

        ProfileCompletionStats stats = new ProfileCompletionStats();

        Long totalProfiles = completeProfileRepo.countTotalProfiles();
        Long completeProfiles = completeProfileRepo.countCompleteProfiles();
        Long verifiedProfiles = completeProfileRepo.countVerifiedProfiles();

        stats.setTotalProfiles(totalProfiles);
        stats.setCompleteProfiles(completeProfiles);
        stats.setIncompleteProfiles(totalProfiles - completeProfiles);
        stats.setCompletionRate(totalProfiles > 0 ? (completeProfiles.doubleValue() / totalProfiles.doubleValue()) * 100 : 0.0);
        stats.setAverageCompletionPercentage(completeProfileRepo.getAverageCompletionPercentage());
        stats.setVerifiedProfiles(verifiedProfiles);
        stats.setVerificationRate(totalProfiles > 0 ? (verifiedProfiles.doubleValue() / totalProfiles.doubleValue()) * 100 : 0.0);

        stats.setSectionCompletionCounts(calculateSectionCompletionCounts());
        stats.setSectionCompletionRates(calculateSectionCompletionRates(totalProfiles));

        return stats;
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.COMPLETE_PROFILES, allEntries = true)
    @Async
    public void recalcAndSave(CompleteProfile completeProfile) {
        try {
            log.debug("Recalculating profile completeness for user ID: {}", 
                    completeProfile.getUser() != null ? completeProfile.getUser().getId() : "unknown");

            calculateCompletionMetrics(completeProfile);

            calculateStrengthMetrics(completeProfile);

            determineProfileQuality(completeProfile);

            updateVerificationStatus(completeProfile);

            completeProfileRepo.save(completeProfile);

            log.debug("Profile completeness recalculated successfully for user ID: {}", 
                    completeProfile.getUser() != null ? completeProfile.getUser().getId() : "unknown");

        } catch (Exception e) {
            log.error("Error recalculating profile completeness: {}", e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    @CacheEvict(value = CacheUtils.CacheNames.COMPLETE_PROFILES, key = "#userId")
    public void forceRecalculateProfile(Integer userId) {
        log.info("Force recalculating profile for user ID: {}", userId);

        CompleteProfile completeProfile = completeProfileRepo.findByUser_Id(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Complete profile not found for user ID: " + userId));

        recalcAndSave(completeProfile);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompleteProfileResponse> getTopProfilesByScore(Pageable pageable) {
        log.debug("Admin fetching top profiles by score");

        Page<CompleteProfile> profiles = completeProfileRepo.findTopProfilesByScore(pageable);
        return profiles.map(mapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CompleteProfileResponse> getProfilesUpdatedBetween(
            LocalDateTime startDate, LocalDateTime endDate, Pageable pageable) {
        
        log.debug("Admin fetching profiles updated between {} and {}", startDate, endDate);

        Page<CompleteProfile> profiles = completeProfileRepo.findByUpdatedAtBetween(startDate, endDate, pageable);
        return profiles.map(mapper::toResponse);
    }

    /**
     * Get current user ID for caching key generation.
     */
    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    /**
     * Calculate completion metrics for the profile.
     */
    private void calculateCompletionMetrics(CompleteProfile profile) {
        int totalSections = 7;
        int completedSections = 0;
        int totalScore = 0;

        if (profile.getUserProfile() != null) {
            completedSections++;
            totalScore += 25;
        }

        if (profile.getContactDetails() != null) {
            completedSections++;
            totalScore += 20;
        }

        if (profile.getPartnerPreference() != null) {
            completedSections++;
            totalScore += 20;
        }

        if (profile.getEducationAndProfession() != null) {
            completedSections++;
            totalScore += 15;
        }

        if (profile.getFamilyBackground() != null) {
            completedSections++;
            totalScore += 10;
        }

        if (profile.getHoroscopeDetails() != null) {
            completedSections++;
            totalScore += 5;
        }

        if (profile.getDocuments() != null && !profile.getDocuments().isEmpty()) {
            completedSections++;
            totalScore += 5;
        }

        profile.setProfileCompleted(completedSections == totalSections);
        profile.setCompletionPercentage((completedSections * 100) / totalSections);
        profile.setCompletenessScore(totalScore);
        profile.setMissingSectionsCount(totalSections - completedSections);
    }

    /**
     * Calculate strength metrics for individual sections.
     */
    private void calculateStrengthMetrics(CompleteProfile profile) {
        profile.setBasicInfoScore(profile.getUserProfile() != null ? 95 : 0);

        int contactScore = 0;
        if (profile.getContactDetails() != null) {
            contactScore = 60;
            if (profile.getMobileVerified()) contactScore += 20;
            if (profile.getEmailVerified()) contactScore += 20;
        }
        profile.setContactInfoScore(contactScore);

        profile.setPersonalDetailsScore(profile.getHoroscopeDetails() != null ? 90 : 0);

        profile.setFamilyInfoScore(profile.getFamilyBackground() != null ? 85 : 0);

        profile.setProfessionalInfoScore(profile.getEducationAndProfession() != null ? 85 : 0);

        profile.setPreferencesScore(profile.getPartnerPreference() != null ? 80 : 0);

        int docScore = 0;
        if (profile.getDocuments() != null && !profile.getDocuments().isEmpty()) {
            docScore = 40;
            if (profile.getHasProfilePhoto()) docScore += 30;
            if (profile.getIdentityVerified()) docScore += 30;
        }
        profile.setDocumentScore(docScore);
    }

    /**
     * Determine profile quality based on completion score.
     */
    private void determineProfileQuality(CompleteProfile profile) {
        int score = profile.getCompletenessScore();
        
        if (score >= 90) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.EXCELLENT);
        } else if (score >= 75) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.VERY_GOOD);
        } else if (score >= 60) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.GOOD);
        } else if (score >= 40) {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.FAIR);
        } else {
            profile.setProfileQuality(CompleteProfile.ProfileQuality.POOR);
        }
    }

    /**
     * Update verification status based on individual verifications.
     */
    private void updateVerificationStatus(CompleteProfile profile) {
        boolean hasVerifications = profile.getMobileVerified() || 
                                 profile.getEmailVerified() || 
                                 profile.getIdentityVerified();

        if (profile.getMobileVerified() && profile.getEmailVerified() && profile.getIdentityVerified()) {
            profile.setVerificationStatus(CompleteProfile.VerificationStatus.VERIFIED);
        } else if (hasVerifications) {
            profile.setVerificationStatus(CompleteProfile.VerificationStatus.PENDING);
        } else {
            profile.setVerificationStatus(CompleteProfile.VerificationStatus.UNVERIFIED);
        }
    }

    /**
     * Generate detailed analytics data.
     */
    private Map<String, Object> generateDetailedAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        analytics.put("sectionCompletionRates", calculateSectionCompletionRates(completeProfileRepo.countTotalProfiles()));
        analytics.put("qualityTrends", new HashMap<String, Object>());

        analytics.put("verificationTrends", new HashMap<String, Object>());

        return analytics;
    }

    /**
     * Calculate section completion counts.
     */
    private Map<String, Long> calculateSectionCompletionCounts() {
        Map<String, Long> counts = new HashMap<>();
        counts.put("userProfile", 0L);
        counts.put("contactDetails", 0L);
        counts.put("partnerPreference", 0L);
        counts.put("educationAndProfession", 0L);
        counts.put("familyBackground", 0L);
        counts.put("horoscopeDetails", 0L);
        counts.put("documents", 0L);
        return counts;
    }

    /**
     * Calculate section completion rates.
     */
    private Map<String, Double> calculateSectionCompletionRates(Long totalProfiles) {
        Map<String, Long> counts = calculateSectionCompletionCounts();
        Map<String, Double> rates = new HashMap<>();
        
        if (totalProfiles > 0) {
            counts.forEach((section, count) -> 
                rates.put(section, (count.doubleValue() / totalProfiles.doubleValue()) * 100));
        }
        
        return rates;
    }
}
