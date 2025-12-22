package com.spring.jwt.CompleteProfile;

import com.spring.jwt.CompleteProfile.dto.CompleteProfileResponse;
import com.spring.jwt.CompleteProfile.dto.ProfileAnalyticsRequest;
import com.spring.jwt.entity.CompleteProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Map;

/**
 * Service interface for complete profile management.
 * Provides comprehensive profile aggregation, analytics, and completeness tracking.
 */
public interface CompleteProfileService {

    /**
     * Get complete profile for current authenticated user.
     */
    CompleteProfileResponse getCurrentUserCompleteProfile();

    /**
     * Get complete profile by user ID (admin only).
     */
    CompleteProfileResponse getByUserId(Integer userId);

    /**
     * Get all complete profiles with pagination (admin only).
     */
    Page<CompleteProfileResponse> getAllCompleteProfiles(Pageable pageable);

    /**
     * Check missing sections for current authenticated user.
     */
    MissingProfileDTO checkCurrentUserMissingSections();

    /**
     * Check missing sections by user ID (admin only).
     */
    MissingProfileDTO checkMissingSections(Integer userId);

    /**
     * Search complete profiles by completion criteria (admin only).
     */
    Page<CompleteProfileResponse> searchByCompletionCriteria(
            Integer minPercentage, Integer maxPercentage, 
            String profileQuality, String verificationStatus,
            Boolean profileCompleted, Pageable pageable);

    /**
     * Get profile analytics and statistics (admin only).
     */
    ProfileAnalyticsResponse getProfileAnalytics(ProfileAnalyticsRequest request);

    /**
     * Get profile completion statistics (admin only).
     */
    ProfileCompletionStats getCompletionStatistics();

    /**
     * Recalculate and save profile completeness metrics.
     * This method is called by other modules when profile sections are updated.
     */
    void recalcAndSave(CompleteProfile completeProfile);

    /**
     * Force recalculation of profile completeness for a user.
     */
    void forceRecalculateProfile(Integer userId);

    /**
     * Get top profiles by completion score (admin only).
     */
    Page<CompleteProfileResponse> getTopProfilesByScore(Pageable pageable);

    /**
     * Get profiles updated within date range (admin only).
     */
    Page<CompleteProfileResponse> getProfilesUpdatedBetween(
            java.time.LocalDateTime startDate, 
            java.time.LocalDateTime endDate, 
            Pageable pageable);

    /**
     * Statistics classes for analytics
     */
    class ProfileAnalyticsResponse {
        private Long totalProfiles;
        private Long completeProfiles;
        private Long incompleteProfiles;
        private Long verifiedProfiles;
        private Double averageCompletionPercentage;
        private Map<String, Long> qualityDistribution;
        private Map<String, Long> verificationDistribution;
        private Map<String, Long> completionRangeDistribution;
        private Map<String, Object> detailedAnalytics;

        public Long getTotalProfiles() { return totalProfiles; }
        public void setTotalProfiles(Long totalProfiles) { this.totalProfiles = totalProfiles; }
        
        public Long getCompleteProfiles() { return completeProfiles; }
        public void setCompleteProfiles(Long completeProfiles) { this.completeProfiles = completeProfiles; }
        
        public Long getIncompleteProfiles() { return incompleteProfiles; }
        public void setIncompleteProfiles(Long incompleteProfiles) { this.incompleteProfiles = incompleteProfiles; }
        
        public Long getVerifiedProfiles() { return verifiedProfiles; }
        public void setVerifiedProfiles(Long verifiedProfiles) { this.verifiedProfiles = verifiedProfiles; }
        
        public Double getAverageCompletionPercentage() { return averageCompletionPercentage; }
        public void setAverageCompletionPercentage(Double averageCompletionPercentage) { this.averageCompletionPercentage = averageCompletionPercentage; }
        
        public Map<String, Long> getQualityDistribution() { return qualityDistribution; }
        public void setQualityDistribution(Map<String, Long> qualityDistribution) { this.qualityDistribution = qualityDistribution; }
        
        public Map<String, Long> getVerificationDistribution() { return verificationDistribution; }
        public void setVerificationDistribution(Map<String, Long> verificationDistribution) { this.verificationDistribution = verificationDistribution; }
        
        public Map<String, Long> getCompletionRangeDistribution() { return completionRangeDistribution; }
        public void setCompletionRangeDistribution(Map<String, Long> completionRangeDistribution) { this.completionRangeDistribution = completionRangeDistribution; }
        
        public Map<String, Object> getDetailedAnalytics() { return detailedAnalytics; }
        public void setDetailedAnalytics(Map<String, Object> detailedAnalytics) { this.detailedAnalytics = detailedAnalytics; }
    }

    class ProfileCompletionStats {
        private Long totalProfiles;
        private Long completeProfiles;
        private Long incompleteProfiles;
        private Double completionRate;
        private Double averageCompletionPercentage;
        private Long verifiedProfiles;
        private Double verificationRate;
        private Map<String, Long> sectionCompletionCounts;
        private Map<String, Double> sectionCompletionRates;

        public Long getTotalProfiles() { return totalProfiles; }
        public void setTotalProfiles(Long totalProfiles) { this.totalProfiles = totalProfiles; }
        
        public Long getCompleteProfiles() { return completeProfiles; }
        public void setCompleteProfiles(Long completeProfiles) { this.completeProfiles = completeProfiles; }
        
        public Long getIncompleteProfiles() { return incompleteProfiles; }
        public void setIncompleteProfiles(Long incompleteProfiles) { this.incompleteProfiles = incompleteProfiles; }
        
        public Double getCompletionRate() { return completionRate; }
        public void setCompletionRate(Double completionRate) { this.completionRate = completionRate; }
        
        public Double getAverageCompletionPercentage() { return averageCompletionPercentage; }
        public void setAverageCompletionPercentage(Double averageCompletionPercentage) { this.averageCompletionPercentage = averageCompletionPercentage; }
        
        public Long getVerifiedProfiles() { return verifiedProfiles; }
        public void setVerifiedProfiles(Long verifiedProfiles) { this.verifiedProfiles = verifiedProfiles; }
        
        public Double getVerificationRate() { return verificationRate; }
        public void setVerificationRate(Double verificationRate) { this.verificationRate = verificationRate; }
        
        public Map<String, Long> getSectionCompletionCounts() { return sectionCompletionCounts; }
        public void setSectionCompletionCounts(Map<String, Long> sectionCompletionCounts) { this.sectionCompletionCounts = sectionCompletionCounts; }
        
        public Map<String, Double> getSectionCompletionRates() { return sectionCompletionRates; }
        public void setSectionCompletionRates(Map<String, Double> sectionCompletionRates) { this.sectionCompletionRates = sectionCompletionRates; }
    }
}
