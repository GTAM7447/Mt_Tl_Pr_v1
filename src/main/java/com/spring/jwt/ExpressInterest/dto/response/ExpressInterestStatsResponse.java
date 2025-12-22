package com.spring.jwt.ExpressInterest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Statistics response DTO for express interest analytics.
 * Provides comprehensive statistics for both user and admin views.
 */
@Data
@Schema(description = "Express interest statistics and analytics")
public class ExpressInterestStatsResponse {

    @Schema(description = "Personal interest statistics")
    private PersonalStats personalStats;

    @Schema(description = "System-wide interest statistics")
    private SystemStats systemStats;

    @Data
    @Schema(description = "Personal interest statistics for a user")
    public static class PersonalStats {
        
        @Schema(description = "Total interests sent by user", example = "25")
        private Long totalSent;

        @Schema(description = "Total interests received by user", example = "18")
        private Long totalReceived;

        @Schema(description = "Pending interests sent", example = "5")
        private Long pendingSent;

        @Schema(description = "Pending interests received", example = "3")
        private Long pendingReceived;

        @Schema(description = "Accepted interests sent", example = "8")
        private Long acceptedSent;

        @Schema(description = "Accepted interests received", example = "6")
        private Long acceptedReceived;

        @Schema(description = "Declined interests sent", example = "10")
        private Long declinedSent;

        @Schema(description = "Declined interests received", example = "7")
        private Long declinedReceived;

        @Schema(description = "Withdrawn interests", example = "2")
        private Long withdrawn;

        @Schema(description = "Expired interests", example = "1")
        private Long expired;

        @Schema(description = "Success rate for sent interests (percentage)", example = "32.0")
        private Double successRate;

        @Schema(description = "Response rate for sent interests (percentage)", example = "72.0")
        private Double responseRate;

        @Schema(description = "Average compatibility score of sent interests", example = "78.5")
        private Double averageCompatibilityScore;

        @Schema(description = "Interests sent today", example = "2")
        private Long sentToday;

        @Schema(description = "Daily limit for sending interests", example = "10")
        private Integer dailyLimit;

        @Schema(description = "Remaining interests for today", example = "8")
        private Integer remainingToday;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Last interest sent timestamp")
        private LocalDateTime lastInterestSent;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Last interest received timestamp")
        private LocalDateTime lastInterestReceived;

        public boolean canSendMoreToday() {
            return remainingToday != null && remainingToday > 0;
        }

        public boolean hasGoodSuccessRate() {
            return successRate != null && successRate >= 25.0;
        }

        public boolean hasGoodResponseRate() {
            return responseRate != null && responseRate >= 50.0;
        }
    }

    @Data
    @Schema(description = "System-wide interest statistics")
    public static class SystemStats {
        
        @Schema(description = "Total interests in system", example = "15420")
        private Long totalInterests;

        @Schema(description = "Active interests (pending)", example = "2340")
        private Long activeInterests;

        @Schema(description = "Completed interests (accepted/declined)", example = "12580")
        private Long completedInterests;

        @Schema(description = "Interests created today", example = "156")
        private Long createdToday;

        @Schema(description = "Interests created this week", example = "892")
        private Long createdThisWeek;

        @Schema(description = "Interests created this month", example = "3456")
        private Long createdThisMonth;

        @Schema(description = "Overall success rate (percentage)", example = "28.5")
        private Double overallSuccessRate;

        @Schema(description = "Overall response rate (percentage)", example = "68.2")
        private Double overallResponseRate;

        @Schema(description = "Average time to response (hours)", example = "18.5")
        private Double averageResponseTime;

        @Schema(description = "Average compatibility score", example = "72.3")
        private Double averageCompatibilityScore;

        @Schema(description = "Most active users (top senders)")
        private Map<String, Long> topSenders;

        @Schema(description = "Most popular users (top receivers)")
        private Map<String, Long> topReceivers;

        @Schema(description = "Interest distribution by status")
        private Map<String, Long> statusDistribution;

        @Schema(description = "Interest distribution by platform")
        private Map<String, Long> platformDistribution;

        @Schema(description = "Hourly interest creation pattern")
        private Map<Integer, Long> hourlyPattern;

        @Schema(description = "Daily interest creation pattern (last 7 days)")
        private Map<String, Long> dailyPattern;

        @Schema(description = "Monthly interest creation pattern (last 12 months)")
        private Map<String, Long> monthlyPattern;

        @Schema(description = "Compatibility score distribution")
        private CompatibilityDistribution compatibilityDistribution;

        @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
        @Schema(description = "Statistics generated timestamp")
        private LocalDateTime generatedAt;

        public boolean isHealthySystem() {
            return overallSuccessRate != null && overallSuccessRate >= 20.0 &&
                   overallResponseRate != null && overallResponseRate >= 60.0;
        }

        public String getSystemHealth() {
            if (overallSuccessRate == null || overallResponseRate == null) {
                return "Unknown";
            }
            if (overallSuccessRate >= 30.0 && overallResponseRate >= 70.0) {
                return "Excellent";
            } else if (overallSuccessRate >= 20.0 && overallResponseRate >= 60.0) {
                return "Good";
            } else if (overallSuccessRate >= 15.0 && overallResponseRate >= 50.0) {
                return "Fair";
            } else {
                return "Needs Improvement";
            }
        }
    }

    @Data
    @Schema(description = "Distribution of compatibility scores")
    public static class CompatibilityDistribution {
        
        @Schema(description = "High compatibility (80-100)", example = "1250")
        private Long highCompatibility;

        @Schema(description = "Medium compatibility (60-79)", example = "3420")
        private Long mediumCompatibility;

        @Schema(description = "Low compatibility (0-59)", example = "2180")
        private Long lowCompatibility;

        @Schema(description = "Unknown compatibility (null)", example = "890")
        private Long unknownCompatibility;

        @Schema(description = "Percentage of high compatibility matches", example = "17.8")
        private Double highCompatibilityPercentage;

        @Schema(description = "Percentage of medium compatibility matches", example = "48.7")
        private Double mediumCompatibilityPercentage;

        @Schema(description = "Percentage of low compatibility matches", example = "31.0")
        private Double lowCompatibilityPercentage;

        public Long getTotalWithCompatibility() {
            return (highCompatibility != null ? highCompatibility : 0L) +
                   (mediumCompatibility != null ? mediumCompatibility : 0L) +
                   (lowCompatibility != null ? lowCompatibility : 0L);
        }

        public boolean hasGoodCompatibilityDistribution() {
            return highCompatibilityPercentage != null && 
                   mediumCompatibilityPercentage != null &&
                   highCompatibilityPercentage >= 15.0 &&
                   mediumCompatibilityPercentage >= 40.0;
        }
    }

    public ExpressInterestStatsResponse() {}

    public ExpressInterestStatsResponse(PersonalStats personalStats) {
        this.personalStats = personalStats;
    }

    public ExpressInterestStatsResponse(SystemStats systemStats) {
        this.systemStats = systemStats;
    }

    public ExpressInterestStatsResponse(PersonalStats personalStats, SystemStats systemStats) {
        this.personalStats = personalStats;
        this.systemStats = systemStats;
    }

    public boolean isPersonalStatsOnly() {
        return personalStats != null && systemStats == null;
    }

    public boolean isSystemStatsOnly() {
        return personalStats == null && systemStats != null;
    }

    public boolean hasCompleteStats() {
        return personalStats != null && systemStats != null;
    }
}