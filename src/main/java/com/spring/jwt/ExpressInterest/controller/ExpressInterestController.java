package com.spring.jwt.ExpressInterest.controller;

import com.spring.jwt.ExpressInterest.dto.request.*;
import com.spring.jwt.ExpressInterest.dto.response.*;
import com.spring.jwt.ExpressInterest.service.ExpressInterestService;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Express Interest operations.
 * Provides comprehensive API for matrimonial interest management with enterprise-level features.
 */
@RestController
@RequestMapping("/api/v1/interests")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Express Interest", description = "APIs for managing matrimonial interests between users")
@SecurityRequirement(name = "bearerAuth")
public class ExpressInterestController {

    private final ExpressInterestService service;

    @PostMapping
    @Operation(summary = "Send interest to another user", 
               description = "Express interest in another user's profile with optional message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Interest sent successfully",
                    content = @Content(schema = @Schema(implementation = ExpressInterestResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid request or validation failed"),
        @ApiResponse(responseCode = "409", description = "Interest already exists or daily limit exceeded"),
        @ApiResponse(responseCode = "402", description = "Active subscription required"),
        @ApiResponse(responseCode = "412", description = "Profile completion insufficient")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<ExpressInterestResponse>> sendInterest(
            @Valid @RequestBody ExpressInterestCreateRequest request) {
        
        log.info("Sending interest to user: {}", request.getToUserId());
        
        ExpressInterestResponse response = service.sendInterest(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Interest sent successfully", response));
    }

    @GetMapping("/{interestId}")
    @Operation(summary = "Get interest details", 
               description = "Get detailed information about a specific interest (sender or receiver only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interest details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExpressInterestDetailResponse.class))),
        @ApiResponse(responseCode = "404", description = "Interest not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - not sender or receiver")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<ExpressInterestDetailResponse>> getInterestById(
            @Parameter(description = "Interest ID", required = true)
            @PathVariable Long interestId) {
        
        log.debug("Getting interest details: {}", interestId);
        
        try {
            ExpressInterestDetailResponse response = service.getInterestById(interestId);
            return ResponseEntity.ok(ResponseDto.success("Interest details retrieved", response));
        } catch (Exception ex) {
            log.error("Error getting interest {}: {}", interestId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get interest details", ex.getMessage()));
        }
    }

    @GetMapping("/sent")
    @Operation(summary = "Get sent interests", 
               description = "Get paginated list of interests sent by current user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sent interests retrieved successfully")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<Page<ExpressInterestResponse>>> getSentInterests(
            @Parameter(description = "Filter by status (PENDING, ACCEPTED, DECLINED, WITHDRAWN, EXPIRED)")
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting sent interests with status: {}", status);
        
        try {
            Page<ExpressInterestResponse> response = service.getSentInterests(status, pageable);
            return ResponseEntity.ok(ResponseDto.success("Sent interests retrieved", response));
        } catch (Exception ex) {
            log.error("Error getting sent interests: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get sent interests", ex.getMessage()));
        }
    }

    @GetMapping("/received")
    @Operation(summary = "Get received interests", 
               description = "Get paginated list of interests received by current user with profile details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Received interests retrieved successfully")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<Page<ExpressInterestDetailResponse>>> getReceivedInterests(
            @Parameter(description = "Filter by status (PENDING, ACCEPTED, DECLINED, EXPIRED)")
            @RequestParam(required = false) String status,
            @PageableDefault(size = 20) Pageable pageable) {
        
        log.debug("Getting received interests with status: {}", status);
        
        try {
            Page<ExpressInterestDetailResponse> response = service.getReceivedInterests(status, pageable);
            return ResponseEntity.ok(ResponseDto.success("Received interests retrieved", response));
        } catch (Exception ex) {
            log.error("Error getting received interests: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get received interests", ex.getMessage()));
        }
    }

    @PatchMapping("/{interestId}/accept")
    @Operation(summary = "Accept received interest", 
               description = "Accept an interest received from another user with optional response message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interest accepted successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or interest cannot be accepted"),
        @ApiResponse(responseCode = "404", description = "Interest not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent modification detected")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<ExpressInterestResponse>> acceptInterest(
            @Parameter(description = "Interest ID", required = true)
            @PathVariable Long interestId,
            @Valid @RequestBody ExpressInterestUpdateRequest request) {
        
        log.info("Accepting interest: {}", interestId);
        
        try {
            ExpressInterestResponse response = service.acceptInterest(interestId, request);
            return ResponseEntity.ok(ResponseDto.success("Interest accepted successfully", response));
        } catch (Exception ex) {
            log.error("Error accepting interest {}: {}", interestId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to accept interest", ex.getMessage()));
        }
    }

    @PatchMapping("/{interestId}/decline")
    @Operation(summary = "Decline received interest", 
               description = "Decline an interest received from another user with optional response message")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interest declined successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or interest cannot be declined"),
        @ApiResponse(responseCode = "404", description = "Interest not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent modification detected")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<ExpressInterestResponse>> declineInterest(
            @Parameter(description = "Interest ID", required = true)
            @PathVariable Long interestId,
            @Valid @RequestBody ExpressInterestUpdateRequest request) {
        
        log.info("Declining interest: {}", interestId);
        
        try {
            ExpressInterestResponse response = service.declineInterest(interestId, request);
            return ResponseEntity.ok(ResponseDto.success("Interest declined successfully", response));
        } catch (Exception ex) {
            log.error("Error declining interest {}: {}", interestId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to decline interest", ex.getMessage()));
        }
    }

    @PatchMapping("/{interestId}/withdraw")
    @Operation(summary = "Withdraw sent interest", 
               description = "Withdraw an interest that was previously sent to another user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Interest withdrawn successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid request or interest cannot be withdrawn"),
        @ApiResponse(responseCode = "404", description = "Interest not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent modification detected")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<ExpressInterestResponse>> withdrawInterest(
            @Parameter(description = "Interest ID", required = true)
            @PathVariable Long interestId,
            @Valid @RequestBody ExpressInterestUpdateRequest request) {
        
        log.info("Withdrawing interest: {}", interestId);
        
        try {
            ExpressInterestResponse response = service.withdrawInterest(interestId, request);
            return ResponseEntity.ok(ResponseDto.success("Interest withdrawn successfully", response));
        } catch (Exception ex) {
            log.error("Error withdrawing interest {}: {}", interestId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to withdraw interest", ex.getMessage()));
        }
    }

    @GetMapping("/statistics")
    @Operation(summary = "Get personal interest statistics", 
               description = "Get comprehensive statistics about current user's interest activity")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ExpressInterestStatsResponse.class)))
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<ExpressInterestStatsResponse>> getPersonalStatistics() {
        
        log.debug("Getting personal interest statistics");
        
        try {
            ExpressInterestStatsResponse response = service.getPersonalStatistics();
            return ResponseEntity.ok(ResponseDto.success("Statistics retrieved successfully", response));
        } catch (Exception ex) {
            log.error("Error getting personal statistics: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get statistics", ex.getMessage()));
        }
    }

    @GetMapping("/compatibility/{userId}")
    @Operation(summary = "Check compatibility with another user", 
               description = "Calculate compatibility score with another user based on profiles and preferences")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compatibility score calculated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID or calculation failed"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<Integer>> checkCompatibility(
            @Parameter(description = "User ID to check compatibility with", required = true)
            @PathVariable Integer userId) {
        
        log.debug("Checking compatibility with user: {}", userId);
        
        try {
            Integer compatibilityScore = service.checkCompatibility(userId);
            return ResponseEntity.ok(ResponseDto.success("Compatibility calculated", compatibilityScore));
        } catch (Exception ex) {
            log.error("Error checking compatibility with user {}: {}", userId, ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to check compatibility", ex.getMessage()));
        }
    }

    @GetMapping("/suggestions")
    @Operation(summary = "Get suggested matches", 
               description = "Get AI-powered suggested matches based on compatibility and preferences")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Suggestions retrieved successfully")
    })
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<Page<ProfileResponse>>> getSuggestedMatches(
            @Parameter(description = "Maximum number of suggestions")
            @RequestParam(defaultValue = "20") int limit,
            @PageableDefault(size = 10) Pageable pageable) {
        
        log.debug("Getting suggested matches with limit: {}", limit);
        
        try {
            Page<ProfileResponse> response = 
                service.getSuggestedMatches(limit, pageable);
            return ResponseEntity.ok(ResponseDto.success("Suggestions retrieved successfully", response));
        } catch (Exception ex) {
            log.error("Error getting suggested matches: {}", ex.getMessage());
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to get suggestions", ex.getMessage()));
        }
    }

    // ==================== LEGACY ENDPOINTS (DEPRECATED) ====================

    @PostMapping("/add")
    @Operation(summary = "Create interest (Legacy)", description = "Legacy endpoint - use POST /api/v1/interests instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<?>> create(@RequestBody ExpressInterestDTO dto) {
        log.warn("Using deprecated /add endpoint - please migrate to POST /api/v1/interests");
        try {
            return ResponseEntity.ok(ResponseDto.success("Interest sent", service.create(dto)));
        } catch (Exception ex) {
            return ResponseEntity.badRequest()
                    .body(ResponseDto.error("Failed to send interest", ex.getMessage()));
        }
    }

    @GetMapping("/sent/{userId}")
    @Operation(summary = "Get sent interests (Legacy)", description = "Legacy endpoint - use GET /api/v1/interests/sent instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<?>> getSent(@PathVariable Integer userId) {
        log.warn("Using deprecated /sent/{userId} endpoint - please migrate to GET /api/v1/interests/sent");
        return ResponseEntity.ok(ResponseDto.success("Sent interests", service.getSent(userId)));
    }

    @GetMapping("/received/{userId}")
    @Operation(summary = "Get received interests (Legacy)", description = "Legacy endpoint - use GET /api/v1/interests/received instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<?>> getReceived(@PathVariable Integer userId) {
        log.warn("Using deprecated /received/{userId} endpoint - please migrate to GET /api/v1/interests/received");
        return ResponseEntity.ok(ResponseDto.success("Received interests", service.getReceived1(userId)));
    }

    @PatchMapping("/{id}/status/{status}")
    @Operation(summary = "Update status (Legacy)", description = "Legacy endpoint - use specific action endpoints instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<?>> updateStatus(@PathVariable Long id, @PathVariable String status) {
        log.warn("Using deprecated /status endpoint - please migrate to specific action endpoints");
        return ResponseEntity.ok(ResponseDto.success("Status updated", service.updateStatus(id, status)));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete interest (Legacy)", description = "Legacy endpoint - use PATCH /api/v1/interests/{id}/withdraw instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<?>> delete(@PathVariable Long id) {
        log.warn("Using deprecated DELETE endpoint - please migrate to PATCH /api/v1/interests/{id}/withdraw");
        service.delete(id);
        return ResponseEntity.ok(ResponseDto.success("Deleted", null));
    }

    @GetMapping("/summary/{userId}")
    @Operation(summary = "Get summary (Legacy)", description = "Legacy endpoint - use GET /api/v1/interests/statistics instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<InterestCountDTO>> getSummary(@PathVariable Integer userId) {
        log.warn("Using deprecated /summary endpoint - please migrate to GET /api/v1/interests/statistics");
        try {
            InterestCountDTO dto = service.getInterestSummary(userId);
            return ResponseEntity.ok(ResponseDto.success("Summary fetched", dto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ResponseDto.error("Failed to fetch summary", e.getMessage()));
        }
    }

    @GetMapping("/receivedProfiles/{userId}")
    @Operation(summary = "Get received with profiles (Legacy)", description = "Legacy endpoint - use GET /api/v1/interests/received instead")
    @Deprecated
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ResponseDto<List<ExpressInterestWithProfileDTO>>> getReceivedInterests(
            @PathVariable Integer userId,
            @RequestParam(required = false, defaultValue = "PENDING") String status) {
        
        log.warn("Using deprecated /receivedProfiles endpoint - please migrate to GET /api/v1/interests/received");
        List<ExpressInterestWithProfileDTO> data = service.getReceived(userId, status);
        return ResponseEntity.ok(ResponseDto.success("Received interest fetched successfully", data));
    }
}
