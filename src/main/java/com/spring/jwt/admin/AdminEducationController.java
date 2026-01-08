package com.spring.jwt.admin;

import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionUpdateRequest;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionService;
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
@RequestMapping("/api/v1/admin/education")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Education Management", description = "Admin operations for user education and profession management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminEducationController {

    private final EducationAndProfessionService educationAndProfessionService;

    @Operation(
        summary = "Create education details for user (Admin)",
        description = "Admin can create education and profession details for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Education details created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Education details already exist"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<EducationAndProfessionResponse>> createEducationDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody EducationAndProfessionCreateRequest request) {
        
        log.info("Admin creating education details for user ID: {}", userId);

        EducationAndProfessionResponse response = educationAndProfessionService.createForUser(userId, request);
        
        return ResponseEntity.status(201)
                .body(ResponseDto.success("Education details created successfully", response));
    }

    @Operation(
        summary = "Get education details by user ID (Admin)",
        description = "Admin can retrieve any user's education and profession details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Education details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<EducationAndProfessionResponse> getEducationByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving education details for user ID: {}", userId);
        EducationAndProfessionResponse response = educationAndProfessionService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update education details (Admin)",
        description = "Admin can update any user's education and profession details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education details updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Education details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<EducationAndProfessionResponse> updateEducationDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody EducationAndProfessionUpdateRequest request) {
        
        log.info("Admin updating education details for user ID: {}", userId);
        return ResponseEntity.status(501).body(null);
    }

    @Operation(
        summary = "Delete education details (Admin)",
        description = "Admin can soft delete any user's education and profession details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education details deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Education details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<String>> deleteEducationDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting education details for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Education deletion not supported", 
            "Education deletion requires user authentication context");
        return ResponseEntity.status(501).body(response);
    }

    @PatchMapping("/user/{userId}/restore")
    public ResponseEntity<ResponseDto<String>> restoreEducationDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin restoring education details for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Education restoration not supported", 
            "Education restoration functionality not available");
        return ResponseEntity.status(501).body(response);
    }

    @Operation(
        summary = "Get education statistics (Admin)",
        description = "Admin can get education and profession statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/statistics")
    public ResponseEntity<EducationAndProfessionService.EducationAndProfessionStats> getEducationStatistics() {
        
        log.info("Admin retrieving education statistics");
        EducationAndProfessionService.EducationAndProfessionStats stats = educationAndProfessionService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "Search education details (Admin)",
        description = "Admin can search education details by various criteria"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/search")
    public ResponseEntity<List<EducationAndProfessionResponse>> searchEducationDetails(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Occupation filter")
            @RequestParam(required = false) String occupation,
            @Parameter(description = "Education level filter")
            @RequestParam(required = false) String education,
            @Parameter(description = "Minimum income filter")
            @RequestParam(required = false) Integer minIncome,
            @Parameter(description = "Maximum income filter")
            @RequestParam(required = false) Integer maxIncome,
            @Parameter(description = "Work location filter")
            @RequestParam(required = false) String workLocation) {
        
        log.info("Admin searching education details with filters - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<EducationAndProfessionResponse> searchResults = 
            educationAndProfessionService.searchEducationAndProfession(occupation, education, minIncome, maxIncome, workLocation, pageable);
        return ResponseEntity.ok(searchResults.getContent());
    }
}