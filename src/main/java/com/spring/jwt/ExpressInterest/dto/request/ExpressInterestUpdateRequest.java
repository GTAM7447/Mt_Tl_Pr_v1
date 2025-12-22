package com.spring.jwt.ExpressInterest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request DTO for updating express interest status.
 * Used for accepting, declining, or withdrawing interests.
 */
@Data
@Schema(description = "Request to update express interest status")
public class ExpressInterestUpdateRequest {

    @NotNull(message = "Version is required for optimistic locking")
    @Schema(description = "Current version for optimistic locking", example = "1", required = true)
    private Integer version;

    @Schema(description = "New status for the interest", 
            example = "ACCEPTED", 
            allowableValues = {"ACCEPTED", "DECLINED", "WITHDRAWN"})
    private String status;

    @Size(max = 500, message = "Response message cannot exceed 500 characters")
    @Schema(description = "Optional response message", 
            example = "Thank you for your interest! I would love to connect.", 
            maxLength = 500)
    private String responseMessage;

    public ExpressInterestUpdateRequest() {}

    public ExpressInterestUpdateRequest(Integer version, String status) {
        this.version = version;
        this.status = status;
    }

    public ExpressInterestUpdateRequest(Integer version, String status, String responseMessage) {
        this.version = version;
        this.status = status;
        this.responseMessage = responseMessage;
    }

    public boolean hasResponseMessage() {
        return responseMessage != null && !responseMessage.trim().isEmpty();
    }

    public String getTrimmedResponseMessage() {
        return responseMessage != null ? responseMessage.trim() : null;
    }

    public boolean isValidStatus() {
        return status != null && 
               ("ACCEPTED".equals(status) || "DECLINED".equals(status) || "WITHDRAWN".equals(status));
    }

    public boolean isAcceptAction() {
        return "ACCEPTED".equals(status);
    }

    public boolean isDeclineAction() {
        return "DECLINED".equals(status);
    }

    public boolean isWithdrawAction() {
        return "WITHDRAWN".equals(status);
    }
}