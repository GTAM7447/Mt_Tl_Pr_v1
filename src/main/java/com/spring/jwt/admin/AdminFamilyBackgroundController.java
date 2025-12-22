package com.spring.jwt.admin;

import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundUpdateRequest;
import com.spring.jwt.FamilyBackground.FamilyBackgroundService;
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
@RequestMapping("/api/v1/admin/family-background")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Family Background Management", description = "Admin operations for user family background management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminFamilyBackgroundController {

    private final FamilyBackgroundService familyBackgroundService;

    @Operation(
        summary = "Create family background for user (Admin)",
        description = "Admin can create family background details for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Family background created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Family background already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<FamilyBackgroundResponse> createFamilyBackground(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody FamilyBackgroundCreateRequest request) {
        
        log.info("Admin creating family background for user ID: {}", userId);
        return ResponseEntity.status(501).body(null);
    }

    @Operation(
        summary = "Get family background by user ID (Admin)",
        description = "Admin can retrieve any user's family background details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<FamilyBackgroundResponse> getFamilyBackgroundByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving family background for user ID: {}", userId);
        FamilyBackgroundResponse response = familyBackgroundService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update family background (Admin)",
        description = "Admin can update any user's family background details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<FamilyBackgroundResponse> updateFamilyBackground(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody FamilyBackgroundUpdateRequest request) {
        
        log.info("Admin updating family background for user ID: {}", userId);
        return ResponseEntity.status(501).body(null);
    }

    @Operation(
        summary = "Get all family backgrounds (Admin)",
        description = "Admin can retrieve all users' family background details with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family backgrounds retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/all")
    public ResponseEntity<List<FamilyBackgroundResponse>> getAllFamilyBackgrounds(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "familyBackgroundId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Admin retrieving all family backgrounds - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? org.springframework.data.domain.Sort.by(sortBy).descending()
            : org.springframework.data.domain.Sort.by(sortBy).ascending();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        org.springframework.data.domain.Page<FamilyBackgroundResponse> familyPage = 
            familyBackgroundService.getAllFamilyBackgrounds(pageable);
        return ResponseEntity.ok(familyPage.getContent());
    }

    @Operation(
        summary = "Delete family background (Admin)",
        description = "Admin can soft delete any user's family background details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Family background deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Family background not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<String>> deleteFamilyBackground(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting family background for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Family background deletion not supported", 
            "Family background deletion requires user authentication context");
        return ResponseEntity.status(501).body(response);
    }

    @PatchMapping("/user/{userId}/restore")
    public ResponseEntity<ResponseDto<String>> restoreFamilyBackground(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin restoring family background for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Family background restoration not supported", 
            "Family background restoration functionality not available");
        return ResponseEntity.status(501).body(response);
    }
}