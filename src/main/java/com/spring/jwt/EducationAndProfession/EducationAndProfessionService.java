package com.spring.jwt.EducationAndProfession;

import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for education and profession management.
 * All operations are secured and scoped to the authenticated user's data.
 */
public interface EducationAndProfessionService {
    
    /**
     * Create education and profession for the current authenticated user.
     * 
     * @param request the education and profession creation request
     * @return created education and profession response
     * @throws ResourceAlreadyExistsException if education and profession already exists for user
     */
    EducationAndProfessionResponse createForCurrentUser(EducationAndProfessionCreateRequest request);

    /**
     * Create education and profession for a specific user (Admin only).
     * 
     * @param userId the target user ID
     * @param request the education and profession creation request
     * @return created education and profession response
     * @throws ResourceAlreadyExistsException if education and profession already exists for user
     */
    EducationAndProfessionResponse createForUser(Integer userId, EducationAndProfessionCreateRequest request);

    /**
     * Get education and profession for the current authenticated user.
     * 
     * @return current user's education and profession
     * @throws ResourceNotFoundException if education and profession not found
     */
    EducationAndProfessionResponse getCurrentUserEducationAndProfession();

    /**
     * Get education and profession by user ID (Admin only).
     * 
     * @param userId the user ID
     * @return education and profession for the specified user
     * @throws ResourceNotFoundException if education and profession not found
     */
    EducationAndProfessionResponse getByUserId(Integer userId);

    /**
     * Search education and profession records by criteria (Admin only).
     * 
     * @param occupation    occupation filter (optional)
     * @param education     education level filter (optional)
     * @param minIncome     minimum income filter (optional)
     * @param maxIncome     maximum income filter (optional)
     * @param workLocation  work location filter (optional)
     * @param pageable      pagination information
     * @return paginated search results
     */
    Page<EducationAndProfessionResponse> searchEducationAndProfession(
            String occupation, String education, Integer minIncome, 
            Integer maxIncome, String workLocation, Pageable pageable);

    /**
     * Update education and profession for the current authenticated user.
     * 
     * @param request the update request
     * @return updated education and profession response
     * @throws ResourceNotFoundException if education and profession not found
     * @throws OptimisticLockingFailureException if version conflict
     */
    EducationAndProfessionResponse updateCurrentUserEducationAndProfession(EducationAndProfessionUpdateRequest request);

    /**
     * Delete education and profession for the current authenticated user.
     * 
     * @throws ResourceNotFoundException if education and profession not found
     */
    void deleteCurrentUserEducationAndProfession();

    /**
     * Get statistics for education and profession data.
     * 
     * @return statistics object with counts and distributions
     */
    EducationAndProfessionStats getStatistics();

    /**
     * Statistics inner class for education and profession data.
     */
    class EducationAndProfessionStats {
        private long totalRecords;
        private long engineerCount;
        private long graduateCount;
        private double averageIncome;
        
        // Getters and setters
        public long getTotalRecords() { return totalRecords; }
        public void setTotalRecords(long totalRecords) { this.totalRecords = totalRecords; }
        
        public long getEngineerCount() { return engineerCount; }
        public void setEngineerCount(long engineerCount) { this.engineerCount = engineerCount; }
        
        public long getGraduateCount() { return graduateCount; }
        public void setGraduateCount(long graduateCount) { this.graduateCount = graduateCount; }
        
        public double getAverageIncome() { return averageIncome; }
        public void setAverageIncome(double averageIncome) { this.averageIncome = averageIncome; }
    }
}
