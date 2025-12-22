package com.spring.jwt.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Response DTO for workflow step information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkflowStepResponse {

    private String stepName;

    private String stepTitle;

    private String description;

    private Integer stepOrder;

    private StepStatus status;

    private boolean required;

    private boolean skippable;

    private Integer completionPercentage;

    private LocalDateTime startedAt;

    private LocalDateTime completedAt;

    private LocalDateTime updatedAt;

    private String errorMessage;

    private Map<String, Object> stepData;

    private String notes;

    private Integer performedBy;

    private Integer estimatedTimeMinutes;

    private Integer actualTimeMinutes;

    private String[] dependencies;

    private String[] nextSteps;

    public enum StepStatus {
        PENDING("Pending"),
        IN_PROGRESS("In Progress"),
        COMPLETED("Completed"),
        FAILED("Failed"),
        SKIPPED("Skipped"),
        BLOCKED("Blocked"),
        CANCELLED("Cancelled");

        private final String displayName;

        StepStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }
}