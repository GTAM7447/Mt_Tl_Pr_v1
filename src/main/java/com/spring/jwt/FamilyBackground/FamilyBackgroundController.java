package com.spring.jwt.FamilyBackground;

import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundUpdateRequest;

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
 * REST Controller for family background management.
 * Implements secure API design with proper authorization and IDOR protection.
 * 
 * All operations are scoped to the authenticated user's data only.
 * Base path: /api/v1/family-background
 */
@RestController
@RequestMapping("/api/v1/family-background")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Family Background", description = "API for managing user family background information")
@SecurityRequirement(name = "bearerAuth")
public class FamilyBackgroundController {

    private final FamilyBackgroundService familyBackgroundService;

    /**
     * Create family background for the authenticated user.
     * POST /api/v1/family-background
     * @param request the family background creation request
     * @return created family background with HTTP 201 status
     */
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Create family background", 
               description = "Create family background for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Family background created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Family background already exists for user"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<FamilyBackgroundResponse>> createFamilyBackground(
            @Valid @RequestBody FamilyBackgroundCreateRequest request) {
        
        log.info("Received create family background request");
        
        FamilyBackgroundResponse response = familyBackgroundService.createForCurrentUser(request);
        
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseDto.success("Family background created successfully", response));
    }

    /**
     * Get the authenticated user's family background.
     * GET /api/v1/family-background/me
     * 
     * @return current user's family background
     */
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Get my family background", 
               description = "Retrieve family background for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<FamilyBackgroundResponse>> getCurrentUserFamilyBackground() {
        
        FamilyBackgroundResponse response = familyBackgroundService.getCurrentUserFamilyBackground();
        
        return ResponseEntity.ok(
                ResponseDto.success("Family background retrieved successfully", response));
    }

    /**
     * Get family background by user ID (Admin only).
     * GET /api/v1/family-background/user/{userId}
     * 
     * Only administrators can access other users' family background.
     * 
     * @param userId the user ID
     * @return family background for the specified user
     */
    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get family background by user ID (Admin only)", 
               description = "Retrieve family background for a specific user (admin access required)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Admin access required")
    })
    public ResponseEntity<ResponseDto<FamilyBackgroundResponse>> getFamilyBackgroundByUserId(
            @PathVariable Integer userId) {
        
        log.info("Admin accessing family background for user ID: {}", userId);
        
        FamilyBackgroundResponse response = familyBackgroundService.getByUserId(userId);
        
        return ResponseEntity.ok(
                ResponseDto.success("Family background retrieved successfully", response));
    }

    /**
     * Browse all family backgrounds (Admin only).
     * GET /api/v1/family-background
     * 
     * Returns paginated list of all family backgrounds.
     * Only accessible by administrators.
     * 
     * @param page page number (0-indexed)
     * @param size page size (max 50)
     * @param sort sort field
     * @param direction sort direction
     * @return paginated list of family backgrounds
     */
//    @GetMapping
//    @PreAuthorize("hasRole('ADMIN')")
//    @Operation(summary = "Browse all family backgrounds (Admin only)",
//               description = "Retrieve paginated list of all family backgrounds")
//    @ApiResponses(value = {
//        @ApiResponse(responseCode = "200", description = "Family backgrounds retrieved successfully"),
//        @ApiResponse(responseCode = "401", description = "User not authenticated"),
//        @ApiResponse(responseCode = "403", description = "Admin access required")
//    })
//    public ResponseEntity<ResponseDto<Page<FamilyBackgroundResponse>>> getAllFamilyBackgrounds(
//            @RequestParam(defaultValue = "0") int page,
//            @RequestParam(defaultValue = "20") int size,
//            @RequestParam(defaultValue = "familyBackgroundId") String sort,
//            @RequestParam(defaultValue = "DESC") Sort.Direction direction)
//    {
//        if (!sort.matches("^(familyBackgroundId|fathersName|createdAt)$")) {
//            throw new IllegalArgumentException("Invalid sort field: " + sort);
//        }
//
//        if (size > 50) {
//            size = 50;
//        }
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sort));
//        Page<FamilyBackgroundResponse> familyBackgrounds = familyBackgroundService.getAllFamilyBackgrounds(pageable);
//
//        return ResponseEntity.ok(
//                ResponseDto.success("Family backgrounds retrieved successfully", familyBackgrounds));
//    }

    /**
     * Update the authenticated user's family background.
     * PATCH /api/v1/family-background
     * 
     * User can only update their own family background.
     * Supports partial updates (PATCH semantics).
     * 
     * @param request the update request
     * @return updated family background
     */
    @PatchMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Update my family background", 
               description = "Update family background for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "409", description = "Concurrent modification detected"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<FamilyBackgroundResponse>> updateCurrentUserFamilyBackground(
            @Valid @RequestBody FamilyBackgroundUpdateRequest request) {
        
        log.info("Received update family background request");
        
        FamilyBackgroundResponse response = familyBackgroundService.updateCurrentUserFamilyBackground(request);
        
        return ResponseEntity.ok(
                ResponseDto.success("Family background updated successfully", response));
    }

    /**
     * Delete the authenticated user's family background.
     * DELETE /api/v1/family-background
     * 
     * User can only delete their own family background.
     * This is a soft delete operation.
     * 
     * @return success response
     */
    @DeleteMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Delete my family background", 
               description = "Delete family background for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "401", description = "User not authenticated"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<ResponseDto<Void>> deleteCurrentUserFamilyBackground() {
        
        log.info("Received delete family background request");
        
        familyBackgroundService.deleteCurrentUserFamilyBackground();
        
        return ResponseEntity.ok(
                ResponseDto.success("Family background deleted successfully", null));
    }
}
