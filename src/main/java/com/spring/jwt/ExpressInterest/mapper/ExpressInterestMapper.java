package com.spring.jwt.ExpressInterest.mapper;

import com.spring.jwt.ExpressInterest.dto.request.*;
import com.spring.jwt.ExpressInterest.dto.response.*;
import com.spring.jwt.entity.ExpressInterest;
import com.spring.jwt.entity.User;
import com.spring.jwt.entity.Enums.InterestStatus;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class ExpressInterestMapper {

    private final ProfileService profileService;

    public ExpressInterest toEntity(ExpressInterestCreateRequest request, User fromUser, User toUser) {
        if (request == null) {
            return null;
        }

        ExpressInterest entity = new ExpressInterest();

        entity.setFromUser(fromUser);
        entity.setToUser(toUser);
        entity.setStatus(InterestStatus.PENDING);
        entity.setMessage(request.getTrimmedMessage());

        entity.setSourcePlatform(request.getSourcePlatform() != null ? request.getSourcePlatform() : "WEB");
        entity.setAutoMatched(request.getAutoMatched() != null ? request.getAutoMatched() : false);

        entity.setExpiresAt(LocalDateTime.now().plusDays(30));

        entity.setCreatedBy(fromUser.getId());
        entity.setUpdatedBy(fromUser.getId());

        entity.setDeleted(false);
        entity.setVersion(0);
        entity.setDailyLimitCount(1);

        return entity;
    }

    public ExpressInterestResponse toResponse(ExpressInterest entity) {
        if (entity == null) {
            return null;
        }

        ExpressInterestResponse response = new ExpressInterestResponse();

        response.setInterestId(entity.getInterestId());
        response.setFromUserId(entity.getFromUserId());
        response.setToUserId(entity.getToUserId());
        response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        response.setMessage(entity.getMessage());
        response.setResponseMessage(entity.getResponseMessage());
        response.setCompatibilityScore(entity.getCompatibilityScore());
        response.setAutoMatched(entity.getAutoMatched());
        response.setSourcePlatform(entity.getSourcePlatform());

        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setExpiresAt(entity.getExpiresAt());

        response.setVersion(entity.getVersion());

        if (entity.getFromUser() != null) {
            response.setFromUserName(getUserDisplayName(entity.getFromUser()));
        }
        if (entity.getToUser() != null) {
            response.setToUserName(getUserDisplayName(entity.getToUser()));
        }

        return response;
    }

    public ExpressInterestDetailResponse toDetailResponse(ExpressInterest entity) {
        if (entity == null) {
            return null;
        }

        ExpressInterestDetailResponse response = new ExpressInterestDetailResponse();

        response.setInterestId(entity.getInterestId());
        response.setStatus(entity.getStatus() != null ? entity.getStatus().name() : null);
        response.setMessage(entity.getMessage());
        response.setResponseMessage(entity.getResponseMessage());
        response.setCompatibilityScore(entity.getCompatibilityScore());
        response.setAutoMatched(entity.getAutoMatched());
        response.setSourcePlatform(entity.getSourcePlatform());

        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setExpiresAt(entity.getExpiresAt());

        response.setVersion(entity.getVersion());

        if (entity.getFromUser() != null) {
            try {
                response.setFromUserProfile(profileService.getProfileByUserId(entity.getFromUser().getId()));
            } catch (Exception e) {
                log.warn("Error loading from user profile for user {}: {}", entity.getFromUser().getId(), e.getMessage());
                response.setFromUserProfile(null);
            }
        }
        if (entity.getToUser() != null) {
            try {
                response.setToUserProfile(profileService.getProfileByUserId(entity.getToUser().getId()));
            } catch (Exception e) {
                log.warn("Error loading to user profile for user {}: {}", entity.getToUser().getId(), e.getMessage());
                response.setToUserProfile(null);
            }
        }

        return response;
    }

    public List<ExpressInterestResponse> toResponseList(List<ExpressInterest> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ExpressInterestDetailResponse> toDetailResponseList(List<ExpressInterest> entities) {
        if (entities == null) {
            return null;
        }
        
        return entities.stream()
                .map(this::toDetailResponse)
                .collect(Collectors.toList());
    }

    public void applyUpdate(ExpressInterestUpdateRequest request, ExpressInterest entity, Integer updatedBy) {
        if (request == null || entity == null) {
            return;
        }

        if (request.getStatus() != null && !request.getStatus().trim().isEmpty()) {
            try {
                InterestStatus newStatus = InterestStatus.valueOf(request.getStatus().toUpperCase());
                entity.setStatus(newStatus);
            } catch (IllegalArgumentException e) {
                log.warn("Invalid status in update request: {}", request.getStatus());
                throw new IllegalArgumentException("Invalid status: " + request.getStatus());
            }
        }

        if (request.hasResponseMessage()) {
            entity.setResponseMessage(request.getTrimmedResponseMessage());
        }

        entity.setUpdatedBy(updatedBy);
    }

    public ExpressInterestStatsResponse.PersonalStats toPersonalStats(
            Long totalSent, Long totalReceived, Long pendingSent, Long pendingReceived,
            Long acceptedSent, Long acceptedReceived, Long declinedSent, Long declinedReceived,
            Long withdrawn, Long expired, Double averageCompatibilityScore,
            Long sentToday, Integer dailyLimit, LocalDateTime lastInterestSent, LocalDateTime lastInterestReceived) {

        ExpressInterestStatsResponse.PersonalStats stats = new ExpressInterestStatsResponse.PersonalStats();
        
        stats.setTotalSent(totalSent != null ? totalSent : 0L);
        stats.setTotalReceived(totalReceived != null ? totalReceived : 0L);
        stats.setPendingSent(pendingSent != null ? pendingSent : 0L);
        stats.setPendingReceived(pendingReceived != null ? pendingReceived : 0L);
        stats.setAcceptedSent(acceptedSent != null ? acceptedSent : 0L);
        stats.setAcceptedReceived(acceptedReceived != null ? acceptedReceived : 0L);
        stats.setDeclinedSent(declinedSent != null ? declinedSent : 0L);
        stats.setDeclinedReceived(declinedReceived != null ? declinedReceived : 0L);
        stats.setWithdrawn(withdrawn != null ? withdrawn : 0L);
        stats.setExpired(expired != null ? expired : 0L);
        stats.setAverageCompatibilityScore(averageCompatibilityScore);
        stats.setSentToday(sentToday != null ? sentToday : 0L);
        stats.setDailyLimit(dailyLimit != null ? dailyLimit : 10);
        stats.setLastInterestSent(lastInterestSent);
        stats.setLastInterestReceived(lastInterestReceived);

        long totalResponded = stats.getAcceptedSent() + stats.getDeclinedSent();
        if (totalResponded > 0) {
            stats.setSuccessRate((double) stats.getAcceptedSent() / totalResponded * 100);
        } else {
            stats.setSuccessRate(0.0);
        }

        if (stats.getTotalSent() > 0) {
            long totalNonPending = stats.getTotalSent() - stats.getPendingSent();
            stats.setResponseRate((double) totalNonPending / stats.getTotalSent() * 100);
        } else {
            stats.setResponseRate(0.0);
        }

        stats.setRemainingToday(Math.max(0, stats.getDailyLimit() - stats.getSentToday().intValue()));

        return stats;
    }

    public ExpressInterestStatsResponse.SystemStats toSystemStats(
            Long totalInterests, Long activeInterests, Long completedInterests,
            Long createdToday, Long createdThisWeek, Long createdThisMonth,
            Double overallSuccessRate, Double overallResponseRate, Double averageCompatibilityScore) {

        ExpressInterestStatsResponse.SystemStats stats = new ExpressInterestStatsResponse.SystemStats();
        
        stats.setTotalInterests(totalInterests != null ? totalInterests : 0L);
        stats.setActiveInterests(activeInterests != null ? activeInterests : 0L);
        stats.setCompletedInterests(completedInterests != null ? completedInterests : 0L);
        stats.setCreatedToday(createdToday != null ? createdToday : 0L);
        stats.setCreatedThisWeek(createdThisWeek != null ? createdThisWeek : 0L);
        stats.setCreatedThisMonth(createdThisMonth != null ? createdThisMonth : 0L);
        stats.setOverallSuccessRate(overallSuccessRate);
        stats.setOverallResponseRate(overallResponseRate);
        stats.setAverageCompatibilityScore(averageCompatibilityScore);
        stats.setGeneratedAt(LocalDateTime.now());

        stats.setAverageResponseTime(24.0);

        return stats;
    }

    public ExpressInterestStatsResponse.CompatibilityDistribution toCompatibilityDistribution(
            Long highCompatibility, Long mediumCompatibility, Long lowCompatibility, Long unknownCompatibility) {

        ExpressInterestStatsResponse.CompatibilityDistribution distribution = 
            new ExpressInterestStatsResponse.CompatibilityDistribution();
        
        distribution.setHighCompatibility(highCompatibility != null ? highCompatibility : 0L);
        distribution.setMediumCompatibility(mediumCompatibility != null ? mediumCompatibility : 0L);
        distribution.setLowCompatibility(lowCompatibility != null ? lowCompatibility : 0L);
        distribution.setUnknownCompatibility(unknownCompatibility != null ? unknownCompatibility : 0L);

        long total = distribution.getTotalWithCompatibility() + distribution.getUnknownCompatibility();
        if (total > 0) {
            distribution.setHighCompatibilityPercentage((double) distribution.getHighCompatibility() / total * 100);
            distribution.setMediumCompatibilityPercentage((double) distribution.getMediumCompatibility() / total * 100);
            distribution.setLowCompatibilityPercentage((double) distribution.getLowCompatibility() / total * 100);
        } else {
            distribution.setHighCompatibilityPercentage(0.0);
            distribution.setMediumCompatibilityPercentage(0.0);
            distribution.setLowCompatibilityPercentage(0.0);
        }

        return distribution;
    }


    private String getUserDisplayName(User user) {
        if (user == null) {
            return "Unknown User";
        }

        if (user.getEmail() != null && !user.getEmail().trim().isEmpty()) {
            return user.getEmail().split("@")[0];
        }
        
        return "User " + user.getId();
    }

    public InterestStatus toStatusEnum(String status) {
        if (status == null || status.trim().isEmpty()) {
            return null;
        }

        try {
            return InterestStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException e) {
            log.warn("Invalid status string: {}", status);
            return null;
        }
    }


    public boolean canMapToResponse(ExpressInterest entity) {
        return entity != null && !Boolean.TRUE.equals(entity.getDeleted());
    }
}