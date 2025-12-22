package com.spring.jwt.ExpressInterest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;


@Data
@Schema(description = "Request to express interest in another user's profile")
public class ExpressInterestCreateRequest {

    @NotNull(message = "Target user ID is required")
    @Positive(message = "Target user ID must be positive")
    @Schema(description = "ID of the user to express interest in", example = "123", required = true)
    private Integer toUserId;

    @Size(max = 500, message = "Message cannot exceed 500 characters")
    @Schema(description = "Optional message to send with the interest", 
            example = "Hi! I found your profile interesting and would love to connect.", 
            maxLength = 500)
    private String message;

    @Schema(description = "Source platform where interest is being sent", 
            example = "WEB", 
            allowableValues = {"WEB", "MOBILE", "API"})
    private String sourcePlatform = "WEB";

    @Schema(description = "Whether this interest was auto-suggested by the system", 
            example = "false")
    private Boolean autoMatched = false;

    public ExpressInterestCreateRequest() {}

    public ExpressInterestCreateRequest(Integer toUserId, String message) {
        this.toUserId = toUserId;
        this.message = message;
        this.sourcePlatform = "WEB";
        this.autoMatched = false;
    }

    public boolean hasMessage() {
        return message != null && !message.trim().isEmpty();
    }

    public String getTrimmedMessage() {
        return message != null ? message.trim() : null;
    }

    public boolean isValidSourcePlatform() {
        return sourcePlatform != null && 
               ("WEB".equals(sourcePlatform) || "MOBILE".equals(sourcePlatform) || "API".equals(sourcePlatform));
    }
}