package com.spring.jwt.ExpressInterest.service;

import com.spring.jwt.ExpressInterest.dto.request.*;
import com.spring.jwt.ExpressInterest.dto.response.*;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for express interest management.
 * Defines enterprise-grade operations with proper security and business logic.
 */
public interface ExpressInterestService {

    /**
     * Send interest to another user.
     *
     * @param request the create request
     * @return the created interest response
     */
    ExpressInterestResponse sendInterest(ExpressInterestCreateRequest request);

    /**
     * Get interest by ID for current user (sender or receiver only).
     *
     * @param interestId the interest ID
     * @return the interest detail response
     */
    ExpressInterestDetailResponse getInterestById(Long interestId);

    /**
     * Get interests sent by current user.
     *
     * @param status optional status filter
     * @param pageable pagination information
     * @return page of sent interests
     */
    Page<ExpressInterestResponse> getSentInterests(String status, Pageable pageable);

    /**
     * Get interests received by current user.
     *
     * @param status optional status filter
     * @param pageable pagination information
     * @return page of received interests with profile details
     */
    Page<ExpressInterestDetailResponse> getReceivedInterests(String status, Pageable pageable);

    /**
     * Accept a received interest.
     *
     * @param interestId the interest ID
     * @param request the update request with response message
     * @return updated interest response
     */
    ExpressInterestResponse acceptInterest(Long interestId, ExpressInterestUpdateRequest request);

    /**
     * Decline a received interest.
     *
     * @param interestId the interest ID
     * @param request the update request with response message
     * @return updated interest response
     */
    ExpressInterestResponse declineInterest(Long interestId, ExpressInterestUpdateRequest request);

    /**
     * Withdraw a sent interest.
     *
     * @param interestId the interest ID
     * @param request the update request
     * @return updated interest response
     */
    ExpressInterestResponse withdrawInterest(Long interestId, ExpressInterestUpdateRequest request);

    /**
     * Get personal interest statistics for current user.
     *
     * @return personal statistics
     */
    ExpressInterestStatsResponse getPersonalStatistics();

    /**
     * Check compatibility score with another user.
     *
     * @param userId the other user ID
     * @return compatibility score (0-100)
     */
    Integer checkCompatibility(Integer userId);

    /**
     * Get suggested matches for current user.
     *
     * @param limit maximum number of suggestions
     * @return list of suggested user IDs with compatibility scores
     */
    Page<ProfileResponse> getSuggestedMatches(int limit, Pageable pageable);

    /**
     * Get interest by ID (Admin only).
     *
     * @param interestId the interest ID
     * @return the interest detail response
     */
    ExpressInterestDetailResponse getInterestByIdAdmin(Long interestId);

    /**
     * Get all interests with pagination and filters (Admin only).
     *
     * @param status optional status filter
     * @param fromUserId optional sender filter
     * @param toUserId optional receiver filter
     * @param minCompatibility optional minimum compatibility score
     * @param maxCompatibility optional maximum compatibility score
     * @param autoMatched optional auto-matched filter
     * @param sourcePlatform optional platform filter
     * @param pageable pagination information
     * @return page of interests
     */
    Page<ExpressInterestDetailResponse> getAllInterestsAdmin(
            String status, Integer fromUserId, Integer toUserId,
            Integer minCompatibility, Integer maxCompatibility,
            Boolean autoMatched, String sourcePlatform, Pageable pageable);

    /**
     * Get all interests for a specific user (Admin only).
     *
     * @param userId the user ID
     * @param pageable pagination information
     * @return page of user's interests
     */
    Page<ExpressInterestDetailResponse> getUserInterestsAdmin(Integer userId, Pageable pageable);

    /**
     * Get system-wide statistics (Admin only).
     *
     * @return system statistics
     */
    ExpressInterestStatsResponse getSystemStatistics();

    /**
     * Get interest analytics (Admin only).
     *
     * @return detailed analytics data
     */
    ExpressInterestStatsResponse getInterestAnalytics();

    /**
     * Expire old pending interests (Admin only).
     *
     * @return number of interests expired
     */
    int expireOldInterests();

    /**
     * Soft delete old interests (Admin only).
     *
     * @param daysOld interests older than this many days
     * @return number of interests deleted
     */
    int deleteOldInterests(int daysOld);

    /**
     * Hard delete an interest (Admin only).
     *
     * @param interestId the interest ID
     */
    void hardDeleteInterest(Long interestId);

    /**
     * Legacy method - Create interest (for backward compatibility).
     */
    @Deprecated
    ExpressInterestDTO create(ExpressInterestDTO dto);

    /**
     * Legacy method - Get by ID (for backward compatibility).
     */
    @Deprecated
    ExpressInterestDTO getById(Long id);

    /**
     * Legacy method - Get sent interests (for backward compatibility).
     */
    @Deprecated
    java.util.List<ExpressInterestDTO> getSent(Integer userId);

    /**
     * Legacy method - Get received interests (for backward compatibility).
     */
    @Deprecated
    java.util.List<ExpressInterestDTO> getReceived1(Integer userId);

    /**
     * Legacy method - Update status (for backward compatibility).
     */
    @Deprecated
    ExpressInterestDTO updateStatus(Long id, String status);

    /**
     * Legacy method - Delete interest (for backward compatibility).
     */
    @Deprecated
    void delete(Long id);

    /**
     * Legacy method - Get sent with status (for backward compatibility).
     */
    @Deprecated
    java.util.List<ExpressInterestDTO> getSent(Integer userId, String status);

    /**
     * Legacy method - Get received with profile (for backward compatibility).
     */
    @Deprecated
    java.util.List<ExpressInterestWithProfileDTO> getReceived(Integer userId, String status);

    /**
     * Legacy method - Get interest summary (for backward compatibility).
     */
    @Deprecated
    InterestCountDTO getInterestSummary(Integer userId);
}

