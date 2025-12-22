package com.spring.jwt.ExpressInterest.service;

import com.spring.jwt.ExpressInterest.dto.request.*;
import com.spring.jwt.ExpressInterest.dto.response.*;
import com.spring.jwt.ExpressInterest.mapper.ExpressInterestMapper;
import com.spring.jwt.ExpressInterest.repository.ExpressInterestRepository;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.entity.ExpressInterest;
import com.spring.jwt.entity.User;
import com.spring.jwt.entity.Enums.InterestStatus;
import com.spring.jwt.exception.ResourceNotFoundException;
import com.spring.jwt.exception.ResourceAlreadyExistsException;
import com.spring.jwt.profile.domain.ProfileOwnershipService;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.utils.CacheUtils;
import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * Service implementation for express interest management.
 * Implements secure operations with proper authorization and business logic.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpressInterestServiceImpl implements ExpressInterestService {

    private final ExpressInterestRepository interestRepository;
    private final UserRepository userRepository;
    private final ProfileOwnershipService ownershipService;
    private final ExpressInterestMapper mapper;
    private final ExpressInterestValidationService validationService;
    private final ExpressInterestMatchingService matchingService;
    private final ProfileService profileService;

    @Value("${app.express-interest.daily-limit:10}")
    private Integer dailyLimit;

    @Value("${app.express-interest.suggestion-limit:20}")
    private Integer suggestionLimit;


    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    public ExpressInterestResponse sendInterest(ExpressInterestCreateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("Sending interest from user {} to user {}", currentUserId, request.getToUserId());

        try {
            if (request.getToUserId() == null || request.getToUserId().equals(currentUserId)) {
                throw new IllegalArgumentException("Invalid target user");
            }

            User fromUser = userRepository.findById(currentUserId)
                    .orElseThrow(() -> new ResourceNotFoundException("Current user not found"));
            
            User toUser = userRepository.findById(request.getToUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Target user not found"));

            ExpressInterest entity = new ExpressInterest();
            entity.setFromUser(fromUser);
            entity.setToUser(toUser);
            entity.setMessage(request.getMessage());
            entity.setCompatibilityScore(25);
            entity.setSourcePlatform(request.getSourcePlatform() != null ? request.getSourcePlatform() : "WEB");
            entity.setIpAddress("127.0.0.1");
            entity.setUserAgent("Web Browser");

            ExpressInterest saved = interestRepository.save(entity);
            log.info("Interest sent successfully with ID: {} from user {} to user {}", 
                    saved.getInterestId(), currentUserId, request.getToUserId());

            ExpressInterestResponse response = new ExpressInterestResponse();
            response.setInterestId(saved.getInterestId());
            response.setFromUserId(saved.getFromUserId());
            response.setToUserId(saved.getToUserId());
            response.setStatus(saved.getStatus() != null ? saved.getStatus().name() : "PENDING");
            response.setMessage(saved.getMessage());
            response.setCompatibilityScore(saved.getCompatibilityScore());
            response.setCreatedAt(saved.getCreatedAt());
            response.setVersion(saved.getVersion());

            return response;

        } catch (Exception e) {
            log.error("Error in sendInterest: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.EXPRESS_INTEREST, key = "#interestId + '_' + #root.target.getCurrentUserId()")
    public ExpressInterestDetailResponse getInterestById(Long interestId) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Getting interest {} for user {}", interestId, currentUserId);

        try {
            ExpressInterest interest = interestRepository.findByIdWithUsers(interestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interest not found"));

            if (!currentUserId.equals(interest.getFromUserId()) && 
                !currentUserId.equals(interest.getToUserId())) {
                throw new ResourceNotFoundException("Interest not found");
            }

            return mapper.toDetailResponse(interest);
            
        } catch (ResourceNotFoundException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting interest {} for user {}: {}", interestId, currentUserId, e.getMessage());
            throw new RuntimeException("Failed to get interest details", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpressInterestResponse> getSentInterests(String status, Pageable pageable) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Getting sent interests for user {} with status {}", currentUserId, status);

        try {
            Page<ExpressInterest> interests;
            
            if (status != null && !status.trim().isEmpty()) {
                InterestStatus statusEnum = mapper.toStatusEnum(status);
                if (statusEnum == null) {
                    throw new IllegalArgumentException("Invalid status: " + status);
                }
                interests = interestRepository.findSentInterestsByUserIdAndStatus(currentUserId, statusEnum, pageable);
            } else {
                interests = interestRepository.findSentInterestsByUserId(currentUserId, pageable);
            }

            return interests.map(mapper::toResponse);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting sent interests for user {}: {}", currentUserId, e.getMessage());
            return new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpressInterestDetailResponse> getReceivedInterests(String status, Pageable pageable) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Getting received interests for user {} with status {}", currentUserId, status);

        try {
            Page<ExpressInterest> interests;
            
            if (status != null && !status.trim().isEmpty()) {
                InterestStatus statusEnum = mapper.toStatusEnum(status);
                if (statusEnum == null) {
                    throw new IllegalArgumentException("Invalid status: " + status);
                }
                interests = interestRepository.findReceivedInterestsByUserIdAndStatus(currentUserId, statusEnum, pageable);
            } else {
                interests = interestRepository.findReceivedInterestsByUserId(currentUserId, pageable);
            }

            return interests.map(mapper::toDetailResponse);
            
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.error("Error getting received interests for user {}: {}", currentUserId, e.getMessage());
            return new org.springframework.data.domain.PageImpl<>(Collections.emptyList(), pageable, 0);
        }
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.EXPRESS_INTEREST, allEntries = true)
    public ExpressInterestResponse acceptInterest(Long interestId, ExpressInterestUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("User {} accepting interest {}", currentUserId, interestId);

        return updateInterestStatus(interestId, request, "ACCEPTED", currentUserId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.EXPRESS_INTEREST, allEntries = true)
    public ExpressInterestResponse declineInterest(Long interestId, ExpressInterestUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("User {} declining interest {}", currentUserId, interestId);

        return updateInterestStatus(interestId, request, "DECLINED", currentUserId);
    }

    @Override
    @Transactional(isolation = Isolation.READ_COMMITTED)
    @CacheEvict(value = CacheUtils.CacheNames.EXPRESS_INTEREST, allEntries = true)
    public ExpressInterestResponse withdrawInterest(Long interestId, ExpressInterestUpdateRequest request) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.info("User {} withdrawing interest {}", currentUserId, interestId);

        return updateInterestStatus(interestId, request, "WITHDRAWN", currentUserId);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheUtils.CacheNames.EXPRESS_INTEREST, key = "'stats_' + #root.target.getCurrentUserId()")
    public ExpressInterestStatsResponse getPersonalStatistics() {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Getting personal statistics for user {}", currentUserId);

        try {
            Long totalSent = interestRepository.countTotalSentByUserId(currentUserId);
            Long totalReceived = interestRepository.countTotalReceivedByUserId(currentUserId);
            
            Long pendingSent = interestRepository.countSentByUserIdAndStatus(currentUserId, InterestStatus.PENDING);
            Long pendingReceived = interestRepository.countReceivedByUserIdAndStatus(currentUserId, InterestStatus.PENDING);
            
            Long acceptedSent = interestRepository.countSentByUserIdAndStatus(currentUserId, InterestStatus.ACCEPTED);
            Long acceptedReceived = interestRepository.countReceivedByUserIdAndStatus(currentUserId, InterestStatus.ACCEPTED);
            
            Long declinedSent = interestRepository.countSentByUserIdAndStatus(currentUserId, InterestStatus.DECLINED);
            Long declinedReceived = interestRepository.countReceivedByUserIdAndStatus(currentUserId, InterestStatus.DECLINED);
            
            Long withdrawn = interestRepository.countSentByUserIdAndStatus(currentUserId, InterestStatus.WITHDRAWN);
            Long expired = interestRepository.countSentByUserIdAndStatus(currentUserId, InterestStatus.EXPIRED);
            
            Double averageCompatibilityScore = interestRepository.getAverageCompatibilityScoreForUser(currentUserId);
            Long sentToday = interestRepository.countInterestsSentToday(currentUserId);

            LocalDateTime lastInterestSent = interestRepository.findLastInterestSentByUser(currentUserId, PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .map(ExpressInterest::getCreatedAt)
                    .orElse(null);
            
            LocalDateTime lastInterestReceived = interestRepository.findLastInterestReceivedByUser(currentUserId, PageRequest.of(0, 1))
                    .stream()
                    .findFirst()
                    .map(ExpressInterest::getCreatedAt)
                    .orElse(null);
            ExpressInterestStatsResponse.PersonalStats personalStats = mapper.toPersonalStats(
                    totalSent, totalReceived, pendingSent, pendingReceived,
                    acceptedSent, acceptedReceived, declinedSent, declinedReceived,
                    withdrawn, expired, averageCompatibilityScore,
                    sentToday, dailyLimit, lastInterestSent, lastInterestReceived
            );

            return new ExpressInterestStatsResponse(personalStats);

        } catch (Exception e) {
            log.error("Error getting personal statistics for user {}: {}", currentUserId, e.getMessage());
            ExpressInterestStatsResponse.PersonalStats defaultStats = mapper.toPersonalStats(
                    0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0L, 0.0, 0L, dailyLimit, null, null
            );
            return new ExpressInterestStatsResponse(defaultStats);
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Integer checkCompatibility(Integer userId) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Checking compatibility between users {} and {}", currentUserId, userId);

        if (userId == null || userId <= 0) {
            log.warn("Invalid user ID: {}", userId);
            return 25;
        }
        
        if (userId.equals(currentUserId)) {
            log.warn("User {} trying to check compatibility with themselves", currentUserId);
            return 25;
        }

        try {
            if (!userRepository.existsById(currentUserId) || !userRepository.existsById(userId)) {
                log.warn("One or both users not found: {} or {}", currentUserId, userId);
                return 25;
            }
        } catch (Exception e) {
            log.error("Error checking user existence: {}", e.getMessage());
            return 25;
        }

        return calculateCompatibilityScoreSafely(currentUserId, userId);
    }

    /**
     * Calculate compatibility score without transaction context to avoid rollback issues.
     */
    private Integer calculateCompatibilityScoreSafely(Integer userId1, Integer userId2) {
        try {
            return matchingService.calculateCompatibilityScore(userId1, userId2);
        } catch (Exception e) {
            log.error("Error calculating compatibility score between users {} and {}: {}", 
                    userId1, userId2, e.getMessage());
            return 25;
        }
    }

    @Override
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    public Page<ProfileResponse> getSuggestedMatches(int limit, Pageable pageable) {
        Integer currentUserId = ownershipService.getCurrentUserId();
        log.debug("Getting suggested matches for user {} with limit {}", currentUserId, limit);

        List<Integer> suggestedUserIds = getSuggestedUserIdsSafely(currentUserId, Math.min(limit, suggestionLimit));

        List<ProfileResponse> suggestions = loadProfilesSafely(suggestedUserIds);

        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), suggestions.size());
        List<ProfileResponse> pageContent = 
            start < suggestions.size() ? suggestions.subList(start, end) : Collections.emptyList();

        return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, suggestions.size());
    }

    /**
     * Get suggested user IDs without transaction context to avoid rollback issues.
     */
    private List<Integer> getSuggestedUserIdsSafely(Integer userId, int limit) {
        try {
            return matchingService.getSuggestedMatches(userId, limit);
        } catch (Exception e) {
            log.error("Error getting suggested user IDs for user {}: {}", userId, e.getMessage());
            return Collections.emptyList();
        }
    }

    /**
     * Load profiles safely without transaction context to avoid rollback issues.
     */
    private List<ProfileResponse> loadProfilesSafely(List<Integer> userIds) {
        List<ProfileResponse> profiles = new ArrayList<>();
        
        for (Integer userId : userIds) {
            try {
                ProfileResponse profile = profileService.getProfileByUserId(userId);
                profiles.add(profile);
            } catch (Exception e) {
                log.warn("Error loading profile for user {}: {}", userId, e.getMessage());
            }
        }
        
        return profiles;
    }

    @Override
    @Transactional(readOnly = true)
    public ExpressInterestDetailResponse getInterestByIdAdmin(Long interestId) {
        log.debug("Admin getting interest {}", interestId);

        ExpressInterest interest = interestRepository.findByIdWithUsers(interestId)
                .orElseThrow(() -> new ResourceNotFoundException("Interest not found"));

        return mapper.toDetailResponse(interest);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpressInterestDetailResponse> getAllInterestsAdmin(
            String status, Integer fromUserId, Integer toUserId,
            Integer minCompatibility, Integer maxCompatibility,
            Boolean autoMatched, String sourcePlatform, Pageable pageable) {
        
        log.debug("Admin getting all interests with filters");

        InterestStatus statusEnum = status != null ? mapper.toStatusEnum(status) : null;
        
        Page<ExpressInterest> interests = interestRepository.searchInterests(
                statusEnum, fromUserId, toUserId, minCompatibility, maxCompatibility,
                autoMatched, sourcePlatform, pageable);

        return interests.map(mapper::toDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ExpressInterestDetailResponse> getUserInterestsAdmin(Integer userId, Pageable pageable) {
        log.debug("Admin getting interests for user {}", userId);

        Page<ExpressInterest> sentInterests = interestRepository.findSentInterestsByUserId(userId, pageable);
        
        return sentInterests.map(mapper::toDetailResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ExpressInterestStatsResponse getSystemStatistics() {
        log.debug("Getting system statistics");

        try {
            Long totalInterests = interestRepository.countTotalInterests();
            Long activeInterests = interestRepository.countByStatus(InterestStatus.PENDING);
            Long acceptedInterests = interestRepository.countByStatus(InterestStatus.ACCEPTED);
            Long declinedInterests = interestRepository.countByStatus(InterestStatus.DECLINED);
            Long completedInterests = acceptedInterests + declinedInterests;
            
            Long createdToday = interestRepository.countCreatedToday();
            Long createdThisWeek = interestRepository.countCreatedThisWeek(LocalDateTime.now().minusDays(7));
            Long createdThisMonth = interestRepository.countCreatedThisMonth(LocalDateTime.now().minusDays(30));
            
            Double overallSuccessRate = interestRepository.getOverallSuccessRate();
            Double overallResponseRate = interestRepository.getOverallResponseRate();
            Double averageCompatibilityScore = interestRepository.getAverageCompatibilityScore();

            ExpressInterestStatsResponse.SystemStats systemStats = mapper.toSystemStats(
                    totalInterests, activeInterests, completedInterests,
                    createdToday, createdThisWeek, createdThisMonth,
                    overallSuccessRate, overallResponseRate, averageCompatibilityScore
            );

            return new ExpressInterestStatsResponse(systemStats);

        } catch (Exception e) {
            log.error("Error getting system statistics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get system statistics", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ExpressInterestStatsResponse getInterestAnalytics() {
        log.debug("Getting interest analytics");

        try {
            ExpressInterestStatsResponse systemStatsResponse = getSystemStatistics();
            ExpressInterestStatsResponse.SystemStats systemStats = systemStatsResponse.getSystemStats();

            Long highCompatibility = interestRepository.countHighCompatibility();
            Long mediumCompatibility = interestRepository.countMediumCompatibility();
            Long lowCompatibility = interestRepository.countLowCompatibility();
            Long unknownCompatibility = interestRepository.countUnknownCompatibility();

            ExpressInterestStatsResponse.CompatibilityDistribution compatibilityDistribution = 
                mapper.toCompatibilityDistribution(highCompatibility, mediumCompatibility, lowCompatibility, unknownCompatibility);

            systemStats.setCompatibilityDistribution(compatibilityDistribution);

            List<Object[]> platformData = interestRepository.countBySourcePlatform();
            Map<String, Long> platformDistribution = new HashMap<>();
            for (Object[] row : platformData) {
                platformDistribution.put((String) row[0], (Long) row[1]);
            }
            systemStats.setPlatformDistribution(platformDistribution);

            List<Object[]> hourlyData = interestRepository.getHourlyPattern();
            Map<Integer, Long> hourlyPattern = new HashMap<>();
            for (Object[] row : hourlyData) {
                hourlyPattern.put((Integer) row[0], (Long) row[1]);
            }
            systemStats.setHourlyPattern(hourlyPattern);

            List<Object[]> dailyData = interestRepository.getDailyPattern(LocalDateTime.now().minusDays(7));
            Map<String, Long> dailyPattern = new HashMap<>();
            for (Object[] row : dailyData) {
                dailyPattern.put(row[0].toString(), (Long) row[1]);
            }
            systemStats.setDailyPattern(dailyPattern);

            return new ExpressInterestStatsResponse(systemStats);

        } catch (Exception e) {
            log.error("Error getting interest analytics: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to get interest analytics", e);
        }
    }

    @Override
    @Transactional
    public int expireOldInterests() {
        log.info("Expiring old pending interests");
        
        Integer adminUserId = ownershipService.getCurrentUserId();
        LocalDateTime cutoffTime = LocalDateTime.now();
        
        int expiredCount = interestRepository.expireOldInterests(cutoffTime, adminUserId);
        log.info("Expired {} old interests", expiredCount);
        
        return expiredCount;
    }

    @Override
    @Transactional
    public int deleteOldInterests(int daysOld) {
        log.info("Soft deleting interests older than {} days", daysOld);
        
        Integer adminUserId = ownershipService.getCurrentUserId();
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(daysOld);
        LocalDateTime deletedAt = LocalDateTime.now();
        
        int deletedCount = interestRepository.softDeleteOldInterests(cutoffDate, deletedAt, adminUserId);
        log.info("Soft deleted {} old interests", deletedCount);
        
        return deletedCount;
    }

    @Override
    @Transactional
    public void hardDeleteInterest(Long interestId) {
        log.warn("Hard deleting interest {}", interestId);
        
        if (!interestRepository.existsById(interestId)) {
            throw new ResourceNotFoundException("Interest not found");
        }
        
        interestRepository.deleteById(interestId);
        log.info("Hard deleted interest {}", interestId);
    }

    private ExpressInterestResponse updateInterestStatus(Long interestId, ExpressInterestUpdateRequest request, 
                                                       String expectedStatus, Integer currentUserId) {
        try {
            request.setStatus(expectedStatus);

            validationService.validateUpdateRequest(request, currentUserId, interestId);

            ExpressInterest existing = interestRepository.findByIdWithUsers(interestId)
                    .orElseThrow(() -> new ResourceNotFoundException("Interest not found"));

            if (!existing.getVersion().equals(request.getVersion())) {
                log.warn("Version conflict for interest {}: expected {}, got {}", 
                        interestId, existing.getVersion(), request.getVersion());
                throw new OptimisticLockingFailureException(
                        "Interest has been modified by another transaction. Please refresh and try again.");
            }

            mapper.applyUpdate(request, existing, currentUserId);

            ExpressInterest saved = interestRepository.save(existing);
            log.info("Interest {} status updated to {} by user {}", interestId, expectedStatus, currentUserId);

            sendStatusUpdateNotificationAsync(saved);

            return mapper.toResponse(saved);

        } catch (OptimisticLockException e) {
            log.error("Optimistic lock exception updating interest {}: {}", interestId, e.getMessage());
            throw new OptimisticLockingFailureException(
                    "Interest has been modified by another transaction. Please refresh and try again.", e);
        } catch (IllegalArgumentException e) {
            log.warn("Validation failed updating interest {}: {}", interestId, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Error updating interest {} status to {}: {}", interestId, expectedStatus, e.getMessage(), e);
            throw new RuntimeException("Failed to update interest status", e);
        }
    }

    public Integer getCurrentUserId() {
        return ownershipService.getCurrentUserId();
    }

    private String getCurrentUserIpAddress() {
        return "127.0.0.1";
    }

    private String getCurrentUserAgent() {
        return "Web Browser";
    }

    /**
     * Send interest notification asynchronously.
     */
    private void sendInterestNotificationAsync(ExpressInterest interest) {
        try {
            log.debug("Sending interest notification for interest {}", interest.getInterestId());
            // notificationService.sendInterestNotification(interest);
        } catch (Exception e) {
            log.error("Error sending interest notification: {}", e.getMessage());
        }
    }

    /**
     * Send status update notification asynchronously.
     */
    private void sendStatusUpdateNotificationAsync(ExpressInterest interest) {
        try {
            log.debug("Sending status update notification for interest {}", interest.getInterestId());
        } catch (Exception e) {
            log.error("Error sending status update notification: {}", e.getMessage());
        }
    }

    @Override
    @Deprecated
    public ExpressInterestDTO create(ExpressInterestDTO dto) {
        log.warn("Using deprecated create method - please migrate to sendInterest");
        throw new UnsupportedOperationException("Please use sendInterest method instead");
    }

    @Override
    @Deprecated
    public ExpressInterestDTO getById(Long id) {
        log.warn("Using deprecated getById method - please migrate to getInterestById");
        throw new UnsupportedOperationException("Please use getInterestById method instead");
    }

    @Override
    @Deprecated
    public List<ExpressInterestDTO> getSent(Integer userId) {
        log.warn("Using deprecated getSent method - please migrate to getSentInterests");
        throw new UnsupportedOperationException("Please use getSentInterests method instead");
    }

    @Override
    @Deprecated
    public List<ExpressInterestDTO> getReceived1(Integer userId) {
        log.warn("Using deprecated getReceived1 method - please migrate to getReceivedInterests");
        throw new UnsupportedOperationException("Please use getReceivedInterests method instead");
    }

    @Override
    @Deprecated
    public ExpressInterestDTO updateStatus(Long id, String status) {
        log.warn("Using deprecated updateStatus method - please migrate to specific action methods");
        throw new UnsupportedOperationException("Please use acceptInterest, declineInterest, or withdrawInterest methods instead");
    }

    @Override
    @Deprecated
    public void delete(Long id) {
        log.warn("Using deprecated delete method - please migrate to withdrawInterest");
        throw new UnsupportedOperationException("Please use withdrawInterest method instead");
    }

    @Override
    @Deprecated
    public List<ExpressInterestDTO> getSent(Integer userId, String status) {
        log.warn("Using deprecated getSent with status method - please migrate to getSentInterests");
        throw new UnsupportedOperationException("Please use getSentInterests method instead");
    }

    @Override
    @Deprecated
    public List<ExpressInterestWithProfileDTO> getReceived(Integer userId, String status) {
        log.warn("Using deprecated getReceived method - please migrate to getReceivedInterests");
        throw new UnsupportedOperationException("Please use getReceivedInterests method instead");
    }

    @Override
    @Deprecated
    public InterestCountDTO getInterestSummary(Integer userId) {
        log.warn("Using deprecated getInterestSummary method - please migrate to getPersonalStatistics");
        throw new UnsupportedOperationException("Please use getPersonalStatistics method instead");
    }
}