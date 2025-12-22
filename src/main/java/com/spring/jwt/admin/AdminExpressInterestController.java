package com.spring.jwt.admin;

import com.spring.jwt.ExpressInterest.service.ExpressInterestService;
import com.spring.jwt.ExpressInterest.dto.response.ExpressInterestDetailResponse;
import com.spring.jwt.ExpressInterest.dto.response.ExpressInterestStatsResponse;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Admin REST Controller for Express Interest management.
 * Provides administrative operations for interest monitoring, analytics, and bulk operations.
 */
@RestController
@RequestMapping("/api/v1/admin/interests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin - Express Interest", description = "Administrative APIs for managing express interests")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExpressInterestController {

    private final ExpressInterestService service;

    // ==================== INTEREST MANAGEMENT ====================

    @GetMapping("/{interestId}")
    @Operation(summary = "Get interest by ID (Admin)", 
               description = "Get detailed information about any interest in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interest details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExpressInterestDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Interest not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<ExpressInterestDetailResponse>> getInterestById(
            @Parameter(description = "Interest ID", required = true)
            @PathVariable Long interestId) {
        
        log.debug("Admin getting interest details: {}", interestId);
        
        try {
            ExpressInterestDetailResponse response = service.getInterestByIdAdmin(interestId);
            return ResponseEntity.ok(ResponseDto.success("Interest details retrieved", response));
        } catch (Exception ex) {
            log.error("Admin error getting interest {}: {}", interestId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get interest details", ex.getMessage()));
        }
    }

    @GetMapping
    @Operation(summary = "Get all interests with filters (Admin)", 
               description = "Get paginated list of all interests with comprehensive filtering options")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interests retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<Page<ExpressInterestDetailResponse>>> getAllInterests(
            @Parameter(description = "Filter by status")
            @RequestParam(required = false) String status,
            @Parameter(description = "Filter by sender user ID")
            @RequestParam(required = false) Integer fromUserId,
            @Parameter(description = "Filter by receiver user ID")
            @RequestParam(required = false) Integer toUserId,
            @Parameter(description = "Minimum compatibility score")
            @RequestParam(required = false) Integer minCompatibility,
            @Parameter(description = "Maximum compatibility score")
            @RequestParam(required = false) Integer maxCompatibility,
            @Parameter(description = "Filter by auto-matched interests")
            @RequestParam(required = false) Boolean autoMatched,
            @Parameter(description = "Filter by source platform")
            @RequestParam(required = false) String sourcePlatform,
            @PageableDefault(size = 50) Pageable pageable) {
        
        log.debug("Admin getting all interests with filters");
        
        try {
            Page<ExpressInterestDetailResponse> response = service.getAllInterestsAdmin(
                    status, fromUserId, toUserId, minCompatibility, maxCompatibility,
                    autoMatched, sourcePlatform, pageable);
            
            return ResponseEntity.ok(ResponseDto.success("Interests retrieved successfully", response));
        } catch (Exception ex) {
            log.error("Admin error getting all interests: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get interests", ex.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get user's interests (Admin)", 
               description = "Get all interests (sent and received) for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User interests retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<Page<ExpressInterestDetailResponse>>> getUserInterests(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Admin getting interests for user: {}", userId);
        
        try {
            Page<ExpressInterestDetailResponse> response = service.getUserInterestsAdmin(userId, pageable);
            return ResponseEntity.ok(ResponseDto.success("User interests retrieved successfully", response));
        } catch (Exception ex) {
            log.error("Admin error getting interests for user {}: {}", userId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get user interests", ex.getMessage()));
        }
    }

    // ==================== STATISTICS AND ANALYTICS ====================

    @GetMapping("/statistics")
    @Operation(summary = "Get system statistics (Admin)", 
               description = "Get comprehensive system-wide interest statistics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExpressInterestStatsResponse.class))),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<ExpressInterestStatsResponse>> getSystemStatistics() {
        
        log.debug("Admin getting system statistics");
        
        try {
            ExpressInterestStatsResponse response = service.getSystemStatistics();
            return ResponseEntity.ok(ResponseDto.success("Statistics retrieved successfully", response));
        } catch (Exception ex) {
            log.error("Admin error getting system statistics: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get statistics", ex.getMessage()));
        }
    }

    @GetMapping("/analytics")
    @Operation(summary = "Get interest analytics (Admin)", 
               description = "Get detailed analytics including trends, patterns, and distributions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Analytics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExpressInterestStatsResponse.class))),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<ExpressInterestStatsResponse>> getInterestAnalytics() {
        
        log.debug("Admin getting interest analytics");
        
        try {
            ExpressInterestStatsResponse response = service.getInterestAnalytics();
            return ResponseEntity.ok(ResponseDto.success("Analytics retrieved successfully", response));
        } catch (Exception ex) {
            log.error("Admin error getting interest analytics: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get analytics", ex.getMessage()));
        }
    }

    // ==================== BULK OPERATIONS ====================

    @PostMapping("/bulk/expire")
    @Operation(summary = "Expire old interests (Admin)", 
               description = "Bulk expire all pending interests that have passed their expiry date")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interests expired successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<Integer>> expireOldInterests() {
        
        log.info("Admin expiring old interests");
        
        try {
            int expiredCount = service.expireOldInterests();
            return ResponseEntity.ok(ResponseDto.success(
                    "Old interests expired successfully", expiredCount));
        } catch (Exception ex) {
            log.error("Admin error expiring old interests: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to expire old interests", ex.getMessage()));
        }
    }

    @PostMapping("/bulk/delete")
    @Operation(summary = "Delete old interests (Admin)", 
               description = "Bulk soft delete interests older than specified number of days")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Old interests deleted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid days parameter"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<Integer>> deleteOldInterests(
            @Parameter(description = "Delete interests older than this many days", required = true)
            @RequestParam int daysOld) {
        
        log.info("Admin deleting interests older than {} days", daysOld);
        
        if (daysOld < 30) {
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Invalid parameter", "Cannot delete interests less than 30 days old"));
        }
        
        try {
            int deletedCount = service.deleteOldInterests(daysOld);
            return ResponseEntity.ok(ResponseDto.success(
                    "Old interests deleted successfully", deletedCount));
        } catch (Exception ex) {
            log.error("Admin error deleting old interests: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to delete old interests", ex.getMessage()));
        }
    }

    @DeleteMapping("/{interestId}")
    @Operation(summary = "Hard delete interest (Admin)", 
               description = "Permanently delete an interest from the system (use with extreme caution)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interest deleted permanently"),
        @ApiResponse(responseCode = "404", description = "Interest not found"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<Void>> hardDeleteInterest(
            @Parameter(description = "Interest ID to delete permanently", required = true)
            @PathVariable Long interestId) {
        
        log.warn("Admin hard deleting interest: {}", interestId);
        
        try {
            service.hardDeleteInterest(interestId);
            return ResponseEntity.ok(ResponseDto.success("Interest deleted permanently", null));
        } catch (Exception ex) {
            log.error("Admin error hard deleting interest {}: {}", interestId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to delete interest", ex.getMessage()));
        }
    }

    // ==================== MONITORING AND HEALTH ====================

    @GetMapping("/health")
    @Operation(summary = "Get interest system health (Admin)", 
               description = "Get health status of the interest system including performance metrics")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Health status retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<SystemHealthResponse>> getSystemHealth() {
        
        log.debug("Admin getting system health");
        
        try {
            ExpressInterestStatsResponse stats = service.getSystemStatistics();
            
            SystemHealthResponse health = new SystemHealthResponse();
            if (stats.getSystemStats() != null) {
                health.setStatus(stats.getSystemStats().getSystemHealth());
                health.setTotalInterests(stats.getSystemStats().getTotalInterests());
                health.setActiveInterests(stats.getSystemStats().getActiveInterests());
                health.setSuccessRate(stats.getSystemStats().getOverallSuccessRate());
                health.setResponseRate(stats.getSystemStats().getOverallResponseRate());
                health.setCreatedToday(stats.getSystemStats().getCreatedToday());
            }
            
            return ResponseEntity.ok(ResponseDto.success("System health retrieved", health));
        } catch (Exception ex) {
            log.error("Admin error getting system health: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get system health", ex.getMessage()));
        }
    }

    /**
     * System health response DTO.
     */
    @Schema(description = "System health information")
    public static class SystemHealthResponse {
        
        @Schema(description = "Overall system health status", example = "Good")
        private String status;
        
        @Schema(description = "Total interests in system", example = "15420")
        private Long totalInterests;
        
        @Schema(description = "Currently active interests", example = "2340")
        private Long activeInterests;
        
        @Schema(description = "Overall success rate percentage", example = "28.5")
        private Double successRate;
        
        @Schema(description = "Overall response rate percentage", example = "68.2")
        private Double responseRate;
        
        @Schema(description = "Interests created today", example = "156")
        private Long createdToday;

        // Getters and setters
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        
        public Long getTotalInterests() { return totalInterests; }
        public void setTotalInterests(Long totalInterests) { this.totalInterests = totalInterests; }
        
        public Long getActiveInterests() { return activeInterests; }
        public void setActiveInterests(Long activeInterests) { this.activeInterests = activeInterests; }
        
        public Double getSuccessRate() { return successRate; }
        public void setSuccessRate(Double successRate) { this.successRate = successRate; }
        
        public Double getResponseRate() { return responseRate; }
        public void setResponseRate(Double responseRate) { this.responseRate = responseRate; }
        
        public Long getCreatedToday() { return createdToday; }
        public void setCreatedToday(Long createdToday) { this.createdToday = createdToday; }
    }
}