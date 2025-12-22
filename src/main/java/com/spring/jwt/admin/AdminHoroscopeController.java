package com.spring.jwt.admin;

import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;
import com.spring.jwt.dto.horoscope.HoroscopeUpdateRequest;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsService;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/horoscope")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Horoscope Management", description = "Admin operations for user horoscope details management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminHoroscopeController {

    private final HoroscopeDetailsService horoscopeDetailsService;

    @Operation(
        summary = "Create horoscope details for user (Admin)",
        description = "Admin can create horoscope details for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Horoscope details created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Horoscope details already exist"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<HoroscopeResponse> createHoroscopeDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody HoroscopeCreateRequest request) {
        
        log.info("Admin creating horoscope details for user ID: {}", userId);
        HoroscopeResponse response = horoscopeDetailsService.createForCurrentUser(request);
        return ResponseEntity.status(201).body(response);
    }

    @Operation(
        summary = "Get horoscope details by user ID (Admin)",
        description = "Admin can retrieve any user's horoscope details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<HoroscopeResponse> getHoroscopeByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving horoscope details for user ID: {}", userId);
        HoroscopeResponse response = horoscopeDetailsService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update horoscope details (Admin)",
        description = "Admin can update any user's horoscope details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<HoroscopeResponse> updateHoroscopeDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody HoroscopeUpdateRequest request) {
        
        log.info("Admin updating horoscope details for user ID: {}", userId);
        HoroscopeResponse response = horoscopeDetailsService.updateCurrentUserHoroscope(request);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all horoscope details (Admin)",
        description = "Admin can retrieve all users' horoscope details with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<HoroscopeResponse>> getAllHoroscopeDetails(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "horoscopeId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Admin retrieving all horoscope details - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);
        
        Sort sort = Sort.by(Sort.Direction.fromString(sortDir), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<HoroscopeResponse> response = horoscopeDetailsService.getAllHoroscopes(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Delete horoscope details (Admin)",
        description = "Admin can soft delete any user's horoscope details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseDto> deleteHoroscopeDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting horoscope details for user ID: {}", userId);
        horoscopeDetailsService.deleteCurrentUserHoroscope();
        ResponseDto response = ResponseDto.success("Horoscope details deleted successfully", 
            "Horoscope details for user " + userId + " have been deleted");
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Restore horoscope details (Admin)",
        description = "Admin can restore soft-deleted horoscope details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Horoscope details restored successfully"),
        @ApiResponse(responseCode = "404", description = "Horoscope details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PatchMapping("/user/{userId}/restore")
    public ResponseEntity<ResponseDto> restoreHoroscopeDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin restoring horoscope details for user ID: {}", userId);
        ResponseDto response = ResponseDto.success("Horoscope details restored successfully", 
            "Horoscope details for user " + userId + " have been restored");
        return ResponseEntity.ok(response);
    }
}