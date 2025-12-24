package com.spring.jwt.ExpressInterest.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * Response DTO for express interest operations.
 * Contains core interest information without detailed profile data.
 */
@Data
@Schema(description = "Express interest response with core information")
public class ExpressInterestResponse {

    @Schema(description = "Unique interest ID", example = "12345")
    private Long interestId;

    @Schema(description = "ID of user who sent the interest", example = "123")
    private Integer fromUserId;

    @Schema(description = "Name of user who sent the interest", example = "John Doe")
    private String fromUserName;

    @Schema(description = "ID of user who received the interest", example = "456")
    private Integer toUserId;

    @Schema(description = "Name of user who received the interest", example = "Jane Smith")
    private String toUserName;

    @Schema(description = "Current status of the interest", 
            example = "PENDING", 
            allowableValues = {"PENDING", "ACCEPTED", "DECLINED", "WITHDRAWN", "EXPIRED"})
    private String status;

    @Schema(description = "Message sent with the interest", 
            example = "Hi! I found your profile interesting.")
    private String message;

    @Schema(description = "Response message from receiver", 
            example = "Thank you for your interest!")
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

    @Schema(description = "Complete profile ID of user who sent the interest", example = "789")
    private Integer fromUserCompleteProfileId;

    @Schema(description = "Profile ID of user who sent the interest", example = "101")
    private Integer fromUserProfileId;

    @Schema(description = "Complete profile ID of user who received the interest", example = "790")
    private Integer toUserCompleteProfileId;

    @Schema(description = "Profile ID of user who received the interest", example = "102")
    private Integer toUserProfileId;

    public ExpressInterestResponse() {}

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

    public boolean isMediumCompatibility() {
        return compatibilityScore != null && compatibilityScore >= 60 && compatibilityScore < 80;
    }

    public boolean isLowCompatibility() {
        return compatibilityScore != null && compatibilityScore < 60;
    }
}