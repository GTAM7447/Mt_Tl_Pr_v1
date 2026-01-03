package com.spring.jwt.EducationAndProfession;

import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionUpdateRequest;
import com.spring.jwt.dto.ResponseDto;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for education and profession management.
 * Implements secure endpoints with proper authorization and comprehensive API documentation.
 * All user operations are scoped to the authenticated user's data to prevent IDOR vulnerabilities.
 */
@RestController
@RequestMapping("/api/v1/education-profession")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Education and Profession", description = "Education and profession management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class EducationAndProfessionController
{

    private final EducationAndProfessionService educationAndProfessionService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "educationProfessionApi")
    @Operation(summary = "Create education and profession", 
               description = "Create education and profession information for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Education and profession created successfully",
                    content = @Content(schema = @Schema(implementation = EducationAndProfessionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Education and profession already exists for user"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<EducationAndProfessionResponse>> createEducationAndProfession(
            @Valid @RequestBody EducationAndProfessionCreateRequest request) {
        
        log.info("Creating education and profession for authenticated user");
        
        EducationAndProfessionResponse response = educationAndProfessionService.createForCurrentUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Education and profession created successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "educationProfessionApi")
    @Operation(summary = "Get current user's education and profession", 
               description = "Retrieve education and profession information for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education and profession retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EducationAndProfessionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Education and profession not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<EducationAndProfessionResponse>> getCurrentUserEducationAndProfession()
    {
        
        log.debug("Fetching education and profession for authenticated user");
        
        EducationAndProfessionResponse response = educationAndProfessionService.getCurrentUserEducationAndProfession();
        
        return ResponseEntity.ok(ResponseDto.success("Education and profession retrieved successfully", response));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "educationProfessionApi")
    @Operation(summary = "Update current user's education and profession", 
               description = "Update education and profession information for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education and profession updated successfully",
                    content = @Content(schema = @Schema(implementation = EducationAndProfessionResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Education and profession not found"),
        @ApiResponse(responseCode = "409", description = "Version conflict - Resource was modified by another transaction"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<EducationAndProfessionResponse>> updateCurrentUserEducationAndProfession
            (
            @Valid @RequestBody EducationAndProfessionUpdateRequest request)
    {
        
        log.info("Updating education and profession for authenticated user");
        
        EducationAndProfessionResponse response = educationAndProfessionService.updateCurrentUserEducationAndProfession(request);
        
        return ResponseEntity.ok(ResponseDto.success("Education and profession updated successfully", response));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "educationProfessionApi")
    @Operation(summary = "Delete current user's education and profession", 
               description = "Soft delete education and profession information for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education and profession deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Education and profession not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Object>> deleteCurrentUserEducationAndProfession() {
        
        log.info("Deleting education and profession for authenticated user");
        
        educationAndProfessionService.deleteCurrentUserEducationAndProfession();
        
        return ResponseEntity.ok(ResponseDto.success("Education and profession deleted successfully", null));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get education and profession by user ID", 
               description = "Retrieve education and profession information for a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Education and profession retrieved successfully",
                    content = @Content(schema = @Schema(implementation = EducationAndProfessionResponse.class))),
        @ApiResponse(responseCode = "404", description = "Education and profession not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<EducationAndProfessionResponse>> getEducationAndProfessionByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.debug("Admin fetching education and profession for user ID: {}", userId);
        
        EducationAndProfessionResponse response = educationAndProfessionService.getByUserId(userId);
        
        return ResponseEntity.ok(ResponseDto.success("Education and profession retrieved successfully", response));
    }

//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Get all education and profession records",
//               description = "Retrieve all education and profession records with pagination (Admin only)")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Education and profession records retrieved successfully"),
//        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
//        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
//    })
//    public ResponseEntity<ResponseDto<Page<EducationAndProfessionResponse>>> getAllEducationAndProfession(
//            @Parameter(description = "Page number (0-based)", example = "0")
//            @RequestParam(defaultValue = "0") @Min(0) int page,
//
//            @Parameter(description = "Page size", example = "20")
//            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
//
//            @Parameter(description = "Sort field", example = "createdAt")
//            @RequestParam(defaultValue = "createdAt") String sortBy,
//
//            @Parameter(description = "Sort direction", example = "DESC")
//            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
//
//        log.debug("Admin fetching all education and profession records - page: {}, size: {}", page, size);
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
//        Page<EducationAndProfessionResponse> response = educationAndProfessionService.getAllEducationAndProfession(pageable);
//
//        return ResponseEntity.ok(ResponseDto.success("Education and profession records retrieved successfully", response));
//    }

    @GetMapping("/search")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search education and profession records", 
               description = "Search education and profession records by various criteria (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<EducationAndProfessionResponse>>> searchEducationAndProfession(
            @Parameter(description = "Occupation filter", example = "Engineer")
            @RequestParam(required = false) String occupation,
            
            @Parameter(description = "Education level filter", example = "Bachelor")
            @RequestParam(required = false) String education,
            
            @Parameter(description = "Minimum income filter", example = "500000")
            @RequestParam(required = false) Integer minIncome,
            
            @Parameter(description = "Maximum income filter", example = "2000000")
            @RequestParam(required = false) Integer maxIncome,
            
            @Parameter(description = "Work location filter", example = "Bangalore")
            @RequestParam(required = false) String workLocation,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "incomePerYear")
            @RequestParam(defaultValue = "incomePerYear") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin searching education and profession records with filters");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<EducationAndProfessionResponse> response = educationAndProfessionService.searchEducationAndProfession(
                occupation, education, minIncome, maxIncome, workLocation, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Search results retrieved successfully", response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get education and profession statistics", 
               description = "Retrieve statistical information about education and profession data (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<EducationAndProfessionService.EducationAndProfessionStats>> getStatistics() {
        
        log.debug("Admin fetching education and profession statistics");
        
        EducationAndProfessionService.EducationAndProfessionStats stats = educationAndProfessionService.getStatistics();
        
        return ResponseEntity.ok(ResponseDto.success("Statistics retrieved successfully", stats));
    }
}
