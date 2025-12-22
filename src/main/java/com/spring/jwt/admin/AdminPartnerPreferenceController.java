package com.spring.jwt.admin;

import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceUpdateRequest;
import com.spring.jwt.PartnerPreference.PartnerPreferenceService;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/partner-preference")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Partner Preference Management", description = "Admin operations for user partner preference management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminPartnerPreferenceController {

    private final PartnerPreferenceService partnerPreferenceService;

    @Operation(
        summary = "Create partner preference for user (Admin)",
        description = "Admin can create partner preference details for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Partner preference created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Partner preference already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<PartnerPreferenceResponse> createPartnerPreference(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody PartnerPreferenceCreateRequest request) {
        
        log.info("Admin creating partner preference for user ID: {}", userId);
        return ResponseEntity.status(501).body(null);
    }

    @Operation(
        summary = "Get partner preference by user ID (Admin)",
        description = "Admin can retrieve any user's partner preference details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preference retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Partner preference not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<PartnerPreferenceResponse> getPartnerPreferenceByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving partner preference for user ID: {}", userId);
        PartnerPreferenceResponse response = partnerPreferenceService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update partner preference (Admin)",
        description = "Admin can update any user's partner preference details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preference updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Partner preference not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<PartnerPreferenceResponse> updatePartnerPreference(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody PartnerPreferenceUpdateRequest request) {
        
        log.info("Admin updating partner preference for user ID: {}", userId);
        return ResponseEntity.status(501).body(null); // Not implemented
    }

    @Operation(
        summary = "Get all partner preferences (Admin)",
        description = "Admin can retrieve all users' partner preference details with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preferences retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/all")
    public ResponseEntity<List<PartnerPreferenceResponse>> getAllPartnerPreferences(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "partnerPreferenceId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Admin retrieving all partner preferences - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? org.springframework.data.domain.Sort.by(sortBy).descending()
            : org.springframework.data.domain.Sort.by(sortBy).ascending();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        org.springframework.data.domain.Page<PartnerPreferenceResponse> preferencePage = 
            partnerPreferenceService.getAllPartnerPreferences(pageable);
        return ResponseEntity.ok(preferencePage.getContent());
    }

    @Operation(
        summary = "Delete partner preference (Admin)",
        description = "Admin can soft delete any user's partner preference details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Partner preference deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Partner preference not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<String>> deletePartnerPreference(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting partner preference for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Partner preference deletion not supported", 
            "Partner preference deletion requires user authentication context");
        return ResponseEntity.status(501).body(response);
    }

    @PatchMapping("/user/{userId}/restore")
    public ResponseEntity<ResponseDto<String>> restorePartnerPreference(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin restoring partner preference for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Partner preference restoration not supported", 
            "Partner preference restoration functionality not available");
        return ResponseEntity.status(501).body(response);
    }

    @Operation(
        summary = "Get partner preference statistics (Admin)",
        description = "Admin can get partner preference statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/statistics")
    public ResponseEntity<PartnerPreferenceService.PartnerPreferenceStats> getPartnerPreferenceStatistics() {
        
        log.info("Admin retrieving partner preference statistics");
        PartnerPreferenceService.PartnerPreferenceStats stats = partnerPreferenceService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "Search partner preferences (Admin)",
        description = "Admin can search partner preferences by various criteria"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/search")
    public ResponseEntity<List<PartnerPreferenceResponse>> searchPartnerPreferences(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Religion filter")
            @RequestParam(required = false) String religion,
            @Parameter(description = "Caste filter")
            @RequestParam(required = false) String caste,
            @Parameter(description = "Education filter")
            @RequestParam(required = false) String education,
            @Parameter(description = "Minimum income filter")
            @RequestParam(required = false) Integer minIncome,
            @Parameter(description = "Maximum income filter")
            @RequestParam(required = false) Integer maxIncome,
            @Parameter(description = "Location filter")
            @RequestParam(required = false) String location,
            @Parameter(description = "Marital status filter")
            @RequestParam(required = false) String maritalStatus) {
        
        log.info("Admin searching partner preferences with filters - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<PartnerPreferenceResponse> searchResults = 
            partnerPreferenceService.searchPartnerPreferences(religion, caste, education, minIncome, maxIncome, location, maritalStatus, pageable);
        return ResponseEntity.ok(searchResults.getContent());
    }

    @Operation(
        summary = "Search by lifestyle choices (Admin)",
        description = "Admin can search partner preferences by lifestyle choices"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/search/lifestyle")
    public ResponseEntity<List<PartnerPreferenceResponse>> searchByLifestyleChoices(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Eating habits filter")
            @RequestParam(required = false) String eatingHabits,
            @Parameter(description = "Drinking habits filter")
            @RequestParam(required = false) String drinkingHabits,
            @Parameter(description = "Smoking habits filter")
            @RequestParam(required = false) String smokingHabits) {
        
        log.info("Admin searching partner preferences by lifestyle choices - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<PartnerPreferenceResponse> searchResults = 
            partnerPreferenceService.searchByLifestyleChoices(eatingHabits, drinkingHabits, smokingHabits, pageable);
        return ResponseEntity.ok(searchResults.getContent());
    }

    @Operation(
        summary = "Get compatibility score (Admin)",
        description = "Admin can get compatibility score between two users"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Compatibility score retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/compatibility/{userId1}/{userId2}")
    public ResponseEntity<ResponseDto<Integer>> getCompatibilityScore(
            @Parameter(description = "First user ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId1,
            @Parameter(description = "Second user ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId2) {
        
        log.info("Admin retrieving compatibility score between users {} and {}", userId1, userId2);
        Integer score = partnerPreferenceService.getCompatibilityScore(userId1, userId2);
        ResponseDto<Integer> response = ResponseDto.success("Compatibility score retrieved successfully", score);
        return ResponseEntity.ok(response);
    }
}