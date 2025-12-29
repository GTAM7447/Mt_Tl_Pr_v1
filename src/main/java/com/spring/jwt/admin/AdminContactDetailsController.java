package com.spring.jwt.admin;

import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.ContactDetails.dto.ContactDetailsUpdateRequest;
import com.spring.jwt.ContactDetails.ContactDetailsService;
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
@RequestMapping("/api/v1/admin/contact-details")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin Contact Details Management", description = "Admin operations for user contact details management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminContactDetailsController {

    private final ContactDetailsService contactDetailsService;

    @Operation(
        summary = "Create contact details for user (Admin)",
        description = "Admin can create contact details for any user"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contact details created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "409", description = "Contact details already exist"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/user/{userId}")
    public ResponseEntity<ContactDetailsResponse> createContactDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody ContactDetailsCreateRequest request) {
        
        log.info("Admin creating contact details for user ID: {}", userId);
        return ResponseEntity.status(501).body(null);
    }

    @Operation(
        summary = "Get contact details by user ID (Admin)",
        description = "Admin can retrieve any user's contact details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/user/{userId}")
    public ResponseEntity<ContactDetailsResponse> getContactDetailsByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving contact details for user ID: {}", userId);
        ContactDetailsResponse response = contactDetailsService.getByUserId(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Update contact details (Admin)",
        description = "Admin can update any user's contact details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PutMapping("/user/{userId}")
    public ResponseEntity<ContactDetailsResponse> updateContactDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody ContactDetailsUpdateRequest request) {
        
        log.info("Admin updating contact details for user ID: {}", userId);
        return ResponseEntity.status(501).body(null);
    }

    @Operation(
        summary = "Get all contact details (Admin)",
        description = "Admin can retrieve all users' contact details with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/all")
    public ResponseEntity<List<ContactDetailsResponse>> getAllContactDetails(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "contactDetailsId") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "asc") String sortDir) {
        
        log.info("Admin retrieving all contact details - page: {}, size: {}, sortBy: {}, sortDir: {}", 
                page, size, sortBy, sortDir);

        org.springframework.data.domain.Sort sort = sortDir.equalsIgnoreCase("desc") 
            ? org.springframework.data.domain.Sort.by(sortBy).descending()
            : org.springframework.data.domain.Sort.by(sortBy).ascending();
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);
        
        org.springframework.data.domain.Page<ContactDetailsResponse> contactPage = 
            contactDetailsService.getAllContactDetails(pageable);
        return ResponseEntity.ok(contactPage.getContent());
    }

    @Operation(
        summary = "Delete contact details (Admin)",
        description = "Admin can soft delete any user's contact details"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @DeleteMapping("/user/{userId}")
    public ResponseEntity<ResponseDto<String>> deleteContactDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin deleting contact details for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Contact details deletion not supported", 
            "Contact details deletion requires user authentication context");
        return ResponseEntity.status(501).body(response);
    }

    @PatchMapping("/user/{userId}/restore")
    public ResponseEntity<ResponseDto<String>> restoreContactDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin restoring contact details for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Contact details restoration not supported", 
            "Contact details restoration functionality not available");
        return ResponseEntity.status(501).body(response);
    }

    @PatchMapping("/user/{userId}/verify")
    public ResponseEntity<ResponseDto<String>> verifyContactDetails(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Verification status", required = true)
            @RequestParam boolean verified) {
        
        log.info("Admin {} contact details for user ID: {}", verified ? "verifying" : "unverifying", userId);
        ResponseDto<String> response = ResponseDto.success("Contact details verification not supported",
            "Contact details verification functionality not available");
        return ResponseEntity.status(501).body(response);
    }

    @Operation(
        summary = "Get contact details statistics (Admin)",
        description = "Admin can get contact details statistics"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/statistics")
    public ResponseEntity<ContactDetailsService.ContactDetailsStats> getContactDetailsStatistics() {
        
        log.info("Admin retrieving contact details statistics");
        ContactDetailsService.ContactDetailsStats stats = contactDetailsService.getStatistics();
        return ResponseEntity.ok(stats);
    }

    @Operation(
        summary = "Search contact details by location (Admin)",
        description = "Admin can search contact details by location criteria"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/search/location")
    public ResponseEntity<List<ContactDetailsResponse>> searchByLocation(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "City filter")
            @RequestParam(required = false) String city,
            @Parameter(description = "State filter")
            @RequestParam(required = false) String state,
            @Parameter(description = "Country filter")
            @RequestParam(required = false) String country) {
        
        log.info("Admin searching contact details by location - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<ContactDetailsResponse> searchResults = 
            contactDetailsService.searchByLocation(city, state, country, pageable);
        return ResponseEntity.ok(searchResults.getContent());
    }

    @Operation(
        summary = "Search contact details by verification status (Admin)",
        description = "Admin can search contact details by verification status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/search/verification")
    public ResponseEntity<List<ContactDetailsResponse>> searchByVerificationStatus(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size,
            @Parameter(description = "Mobile verified filter")
            @RequestParam(required = false) Boolean mobileVerified,
            @Parameter(description = "Email verified filter")
            @RequestParam(required = false) Boolean emailVerified) {
        
        log.info("Admin searching contact details by verification status - page: {}, size: {}", page, size);
        
        org.springframework.data.domain.Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size);
        org.springframework.data.domain.Page<ContactDetailsResponse> searchResults = 
            contactDetailsService.searchByVerificationStatus(mobileVerified, emailVerified, pageable);
        return ResponseEntity.ok(searchResults.getContent());
    }
}