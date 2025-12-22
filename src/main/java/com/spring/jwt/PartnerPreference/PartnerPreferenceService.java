package com.spring.jwt.PartnerPreference;

import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for partner preference management.
 * All operations are secured and scoped to the authenticated user's data.
 */
public interface PartnerPreferenceService {
    
    /**
     * Create partner preferences for the current authenticated user.
     * 
     * @param request the partner preference creation request
     * @return created partner preference response
     * @throws ResourceAlreadyExistsException if partner preferences already exist for user
     */
    PartnerPreferenceResponse createForCurrentUser(PartnerPreferenceCreateRequest request);

    /**
     * Get partner preferences for the current authenticated user.
     * 
     * @return current user's partner preferences
     * @throws ResourceNotFoundException if partner preferences not found
     */
    PartnerPreferenceResponse getCurrentUserPartnerPreferences();

    /**
     * Get partner preferences by user ID (Admin only).
     * 
     * @param userId the user ID
     * @return partner preferences for the specified user
     * @throws ResourceNotFoundException if partner preferences not found
     */
    PartnerPreferenceResponse getByUserId(Integer userId);

    /**
     * Get all partner preferences with pagination (Admin only).
     * 
     * @param pageable pagination information
     * @return paginated partner preferences
     */
    Page<PartnerPreferenceResponse> getAllPartnerPreferences(Pageable pageable);

    /**
     * Search partner preferences by criteria (Admin only).
     * 
     * @param religion      religion filter (optional)
     * @param caste         caste filter (optional)
     * @param education     education level filter (optional)
     * @param minIncome     minimum income filter (optional)
     * @param maxIncome     maximum income filter (optional)
     * @param location      location filter (optional)
     * @param maritalStatus marital status filter (optional)
     * @param pageable      pagination information
     * @return paginated search results
     */
    Page<PartnerPreferenceResponse> searchPartnerPreferences(
            String religion, String caste, String education, Integer minIncome, 
            Integer maxIncome, String location, String maritalStatus, Pageable pageable);

    /**
     * Search partner preferences by lifestyle choices (Admin only).
     * 
     * @param eatingHabits   eating habits filter (optional)
     * @param drinkingHabits drinking habits filter (optional)
     * @param smokingHabits  smoking habits filter (optional)
     * @param pageable       pagination information
     * @return paginated search results
     */
    Page<PartnerPreferenceResponse> searchByLifestyleChoices(
            String eatingHabits, String drinkingHabits, String smokingHabits, Pageable pageable);

    /**
     * Update partner preferences for the current authenticated user.
     * 
     * @param request the update request
     * @return updated partner preference response
     * @throws ResourceNotFoundException if partner preferences not found
     * @throws OptimisticLockingFailureException if version conflict
     */
    PartnerPreferenceResponse updateCurrentUserPartnerPreferences(PartnerPreferenceUpdateRequest request);

    /**
     * Delete partner preferences for the current authenticated user.
     * 
     * @throws ResourceNotFoundException if partner preferences not found
     */
    void deleteCurrentUserPartnerPreferences();

    /**
     * Get statistics for partner preference data.
     * 
     * @return statistics object with counts and distributions
     */
    PartnerPreferenceStats getStatistics();

    /**
     * Get partner preference compatibility score between two users.
     * 
     * @param userId1 first user ID
     * @param userId2 second user ID
     * @return compatibility score (0-100)
     */
    Integer getCompatibilityScore(Integer userId1, Integer userId2);

    /**
     * Statistics inner class for partner preference data.
     */
    class PartnerPreferenceStats {
        private long totalPreferences;
        private long vegetarianPreferences;
        private long graduatePreferences;
        private double averageIncomeExpectation;
        private String mostPreferredReligion;
        private String mostPreferredEducation;
        
        // Getters and setters
        public long getTotalPreferences() { return totalPreferences; }
        public void setTotalPreferences(long totalPreferences) { this.totalPreferences = totalPreferences; }
        
        public long getVegetarianPreferences() { return vegetarianPreferences; }
        public void setVegetarianPreferences(long vegetarianPreferences) { this.vegetarianPreferences = vegetarianPreferences; }
        
        public long getGraduatePreferences() { return graduatePreferences; }
        public void setGraduatePreferences(long graduatePreferences) { this.graduatePreferences = graduatePreferences; }
        
        public double getAverageIncomeExpectation() { return averageIncomeExpectation; }
        public void setAverageIncomeExpectation(double averageIncomeExpectation) { this.averageIncomeExpectation = averageIncomeExpectation; }
        
        public String getMostPreferredReligion() { return mostPreferredReligion; }
        public void setMostPreferredReligion(String mostPreferredReligion) { this.mostPreferredReligion = mostPreferredReligion; }
        
        public String getMostPreferredEducation() { return mostPreferredEducation; }
        public void setMostPreferredEducation(String mostPreferredEducation) { this.mostPreferredEducation = mostPreferredEducation; }
    }
}
