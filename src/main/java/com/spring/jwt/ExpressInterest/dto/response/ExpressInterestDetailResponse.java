package com.spring.jwt.ExpressInterest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Detailed response DTO for express interest with profile information.
 * Used when detailed user profile data is needed along with interest data.
 */
@Data
@Schema(description = "Detailed express interest response with profile information")
public class ExpressInterestDetailResponse {

    @Schema(description = "Unique interest ID", example = "12345")
    private Long interestId;

    @Schema(description = "Current status of the interest", 
            example = "PENDING", 
            allowableValues = {"PENDING", "ACCEPTED", "DECLINED", "WITHDRAWN", "EXPIRED"})
    private String status;

    @Schema(description = "Message sent with the interest")
    private String message;

    @Schema(description = "Response message from receiver")
    private String responseMessage;

    @Schema(description = "Compatibility score between users (0-100)", example = "85")
    private Integer compatibilityScore;

    @Schema(description = "Whether this was an auto-suggested match", example = "false")
    private Boolean autoMatched;

    @Schema(description = "Platform where interest was sent", example = "WEB")
    private String sourcePlatform;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "When the interest was created", example = "2024-01-15 10:30:00")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "When the interest was last updated", example = "2024-01-15 14:20:00")
    private LocalDateTime updatedAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "When the interest expires", example = "2024-02-15 10:30:00")
    private LocalDateTime expiresAt;

    @Schema(description = "Current version for optimistic locking", example = "1")
    private Integer version;

    @Schema(description = "Profile information of user who sent the interest")
    private ProfileResponse fromUserProfile;

    @Schema(description = "Profile information of user who received the interest")
    private ProfileResponse toUserProfile;

    public boolean isPending() {
        return "PENDING".equals(status);
    }

    public boolean isAccepted() {
        return "ACCEPTED".equals(status);
    }

    public boolean isDeclined() {
        return "DECLINED".equals(status);
    }

    public boolean isWithdrawn() {
        return "WITHDRAWN".equals(status);
    }

    public boolean isExpired() {
        return "EXPIRED".equals(status) || 
               (expiresAt != null && expiresAt.isBefore(LocalDateTime.now()));
    }

    public boolean canBeAccepted() {
        return isPending() && !isExpired();
    }

    public boolean canBeDeclined() {
        return isPending() && !isExpired();
    }

    public boolean canBeWithdrawn() {
        return isPending() && !isExpired();
    }

    public boolean hasMessage() {
        return message != null && !message.trim().isEmpty();
    }

    public boolean hasResponseMessage() {
        return responseMessage != null && !responseMessage.trim().isEmpty();
    }

    public boolean isHighCompatibility() {
        return compatibilityScore != null && compatibilityScore >= 80;
    }

    public String getCompatibilityLevel() {
        if (compatibilityScore == null) return "Unknown";
        if (compatibilityScore >= 80) return "High";
        if (compatibilityScore >= 60) return "Medium";
        return "Low";
    }
}