package com.spring.jwt.HoroscopeDetails;

import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;
import com.spring.jwt.dto.horoscope.HoroscopeUpdateRequest;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for horoscope details management.
 * 
 * All operations are scoped to the authenticated user's data only.
 * Base path: /api/v1/horoscope
 */
@RestController
@RequestMapping("/api/v1/horoscope")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Horoscope Details", description = "API for managing user horoscope details")
@SecurityRequirement(name = "bearerAuth")
public class HoroscopeDetailsController {

    private final HoroscopeDetailsService horoscopeService;

    /**
     * Create horoscope details for the authenticated user.
     * POST /api/v1/horoscope
     * @param request the horoscope creation request
     * @return created horoscope details with HTTP 201 status
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "horoscopeApi")
    @Operation(summary = "Create horoscope details", 
               description = "Create horoscope details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Horoscope details created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Horoscope details already exist for user"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<HoroscopeResponse>> createHoroscope(
            @Valid @RequestBody HoroscopeCreateRequest request) {
        
        log.info("Received create horoscope request");
        
        HoroscopeResponse response = horoscopeService.createForCurrentUser(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.success("Horoscope details created successfully", response));
    }

    /**
     * Get the authenticated user's horoscope details.
     * GET /api/v1/horoscope/me
     * 
     * @return current user's horoscope details
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get my horoscope details", 
               description = "Retrieve horoscope details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<HoroscopeResponse>> getCurrentUserHoroscope() {
        
        HoroscopeResponse response = horoscopeService.getCurrentUserHoroscope();
        
        return ResponseEntity.ok(
                ResponseDto.success("Horoscope details retrieved successfully", response));
    }

    /**
     * Get horoscope details by user ID (Admin only).
     * GET /api/v1/horoscope/user/{userId}
     * 
     * Only administrators can access other users' horoscope details.
     * 
     * @param userId the user ID
     * @return horoscope details for the specified user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get horoscope by user ID (Admin only)", 
               description = "Retrieve horoscope details for a specific user (admin access required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<HoroscopeResponse>> getHoroscopeByUserId(
            @PathVariable Integer userId) {
        
        log.info("Admin accessing horoscope for user ID: {}", userId);
        
        HoroscopeResponse response = horoscopeService.getByUserId(userId);
        
        return ResponseEntity.ok(
                ResponseDto.success("Horoscope details retrieved successfully", response));
    }

    /**
     * Browse all horoscope details (Admin only).
     * GET /api/v1/horoscope
     * 
     * Returns paginated list of all horoscope details.
     * Only accessible by administrators.
     * 
     * @param page page number (0-indexed)
     * @param size page size (max 50)
     * @param sort sort field
     * @param direction sort direction
     * @return paginated list of horoscope details
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Browse all horoscope details (Admin only)", 
               description = "Retrieve paginated list of all horoscope details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<Page<HoroscopeResponse>>> getAllHoroscopes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "horoscopeDetailsId") String sort,
            @RequestParam(defaultValue = "DESC") Sort.Direction direction) {

        if (!sort.matches("^(horoscopeDetailsId|dob|rashi|nakshatra)$")) {
            throw new IllegalArgumentException("Invalid sort field: " + sort);
        }

        if (size > 50) {
            size = 50;
        }
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
        Page<HoroscopeResponse> horoscopes = horoscopeService.getAllHoroscopes(pageable);
        
        return ResponseEntity.ok(
                ResponseDto.success("Horoscope details retrieved successfully", horoscopes));
    }

    /**
     * Update the authenticated user's horoscope details.
     * PATCH /api/v1/horoscope
     * 
     * User can only update their own horoscope details.
     * Supports partial updates (PATCH semantics).
     * 
     * @param request the update request
     * @return updated horoscope details
     */
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update my horoscope details", 
               description = "Update horoscope details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent modification detected"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<HoroscopeResponse>> updateCurrentUserHoroscope(
            @Valid @RequestBody HoroscopeUpdateRequest request) {
        
        log.info("Received update horoscope request");
        
        HoroscopeResponse response = horoscopeService.updateCurrentUserHoroscope(request);
        
        return ResponseEntity.ok(
                ResponseDto.success("Horoscope details updated successfully", response));
    }

    /**
     * Delete the authenticated user's horoscope details.
     * DELETE /api/v1/horoscope
     * 
     * User can only delete their own horoscope details.
     * This is a soft delete operation.
     * 
     * @return success response
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete my horoscope details", 
               description = "Delete horoscope details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<Void>> deleteCurrentUserHoroscope() {
        
        log.info("Received delete horoscope request");
        
        horoscopeService.deleteCurrentUserHoroscope();
        
        return ResponseEntity.ok(
                ResponseDto.success("Horoscope details deleted successfully", null));
    }
}
