package com.spring.jwt.ContactDetails;

import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.ContactDetails.dto.ContactDetailsUpdateRequest;
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
 * REST Controller for contact details management.
 * Implements secure endpoints with proper authorization and comprehensive API documentation.
 * All user operations are scoped to the authenticated user's data to prevent IDOR vulnerabilities.
 */
@RestController
@RequestMapping("/api/v1/contact-details")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Contact Details", description = "Contact details management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class ContactDetailsController {

    private final ContactDetailsService contactDetailsService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "contactDetailsApi")
    @Operation(summary = "Create contact details", 
               description = "Create contact details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Contact details created successfully",
                    content = @Content(schema = @Schema(implementation = ContactDetailsResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "Contact details already exist for user or mobile number already registered"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ContactDetailsResponse>> createContactDetails(
            @Valid @RequestBody ContactDetailsCreateRequest request) {
        
        log.info("Creating contact details for authenticated user");
        
        ContactDetailsResponse response = contactDetailsService.createForCurrentUser(request);
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseDto.success("Contact details created successfully", response));
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "contactDetailsApi")
    @Operation(summary = "Get current user's contact details", 
               description = "Retrieve contact details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ContactDetailsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ContactDetailsResponse>> getCurrentUserContactDetails() {
        
        log.debug("Fetching contact details for authenticated user");
        
        ContactDetailsResponse response = contactDetailsService.getCurrentUserContactDetails();
        
        return ResponseEntity.ok(ResponseDto.success("Contact details retrieved successfully", response));
    }

    @PatchMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "contactDetailsApi")
    @Operation(summary = "Update current user's contact details", 
               description = "Update contact details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details updated successfully",
                    content = @Content(schema = @Schema(implementation = ContactDetailsResponse.class))),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "409", description = "Version conflict - Resource was modified by another transaction or mobile number already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<ContactDetailsResponse>> updateCurrentUserContactDetails(
            @Valid @RequestBody ContactDetailsUpdateRequest request) {
        
        log.info("Updating contact details for authenticated user");
        
        ContactDetailsResponse response = contactDetailsService.updateCurrentUserContactDetails(request);
        
        return ResponseEntity.ok(ResponseDto.success("Contact details updated successfully", response));
    }

    @DeleteMapping("/me")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "contactDetailsApi")
    @Operation(summary = "Delete current user's contact details", 
               description = "Soft delete contact details for the authenticated user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Insufficient permissions"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Object>> deleteCurrentUserContactDetails() {
        
        log.info("Deleting contact details for authenticated user");
        
        contactDetailsService.deleteCurrentUserContactDetails();
        
        return ResponseEntity.ok(ResponseDto.success("Contact details deleted successfully", null));
    }

    // Verification endpoints

    @PostMapping("/verify/mobile")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "verificationApi")
    @Operation(summary = "Verify mobile number", 
               description = "Verify mobile number with verification code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Mobile number verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification code"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Boolean>> verifyMobileNumber(
            @Parameter(description = "Verification code", required = true)
            @RequestParam String verificationCode) {
        
        log.info("Verifying mobile number for authenticated user");
        
        boolean verified = contactDetailsService.verifyMobileNumber(verificationCode);
        
        return ResponseEntity.ok(ResponseDto.success(
                verified ? "Mobile number verified successfully" : "Invalid verification code", 
                verified));
    }

    @PostMapping("/verify/email")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "verificationApi")
    @Operation(summary = "Verify email address", 
               description = "Verify email address with verification code")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Email address verified successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid verification code"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Boolean>> verifyEmailAddress(
            @Parameter(description = "Verification code", required = true)
            @RequestParam String verificationCode) {
        
        log.info("Verifying email address for authenticated user");
        
        boolean verified = contactDetailsService.verifyEmailAddress(verificationCode);
        
        return ResponseEntity.ok(ResponseDto.success(
                verified ? "Email address verified successfully" : "Invalid verification code", 
                verified));
    }

    @PostMapping("/send-verification/mobile")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "verificationApi")
    @Operation(summary = "Send mobile verification code", 
               description = "Send verification code to mobile number")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Boolean>> sendMobileVerificationCode() {
        
        log.info("Sending mobile verification code for authenticated user");
        
        boolean sent = contactDetailsService.sendMobileVerificationCode();
        
        return ResponseEntity.ok(ResponseDto.success("Verification code sent successfully", sent));
    }

    @PostMapping("/send-verification/email")
    @PreAuthorize("hasRole('USER')")
    @RateLimiter(name = "verificationApi")
    @Operation(summary = "Send email verification code", 
               description = "Send verification code to email address")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Verification code sent successfully"),
        @ApiResponse(responseCode = "400", description = "Email address not provided"),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "429", description = "Too many requests - Rate limit exceeded")
    })
    public ResponseEntity<ResponseDto<Boolean>> sendEmailVerificationCode() {
        
        log.info("Sending email verification code for authenticated user");
        
        boolean sent = contactDetailsService.sendEmailVerificationCode();
        
        return ResponseEntity.ok(ResponseDto.success("Verification code sent successfully", sent));
    }

    // Admin-only endpoints

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get contact details by user ID", 
               description = "Retrieve contact details for a specific user (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details retrieved successfully",
                    content = @Content(schema = @Schema(implementation = ContactDetailsResponse.class))),
        @ApiResponse(responseCode = "404", description = "Contact details not found"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<ContactDetailsResponse>> getContactDetailsByUserId(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.debug("Admin fetching contact details for user ID: {}", userId);
        
        ContactDetailsResponse response = contactDetailsService.getByUserId(userId);
        
        return ResponseEntity.ok(ResponseDto.success("Contact details retrieved successfully", response));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get all contact details", 
               description = "Retrieve all contact details with pagination (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Contact details retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<ContactDetailsResponse>>> getAllContactDetails(
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "createdAt")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin fetching all contact details - page: {}, size: {}", page, size);
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<ContactDetailsResponse> response = contactDetailsService.getAllContactDetails(pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Contact details retrieved successfully", response));
    }

    @GetMapping("/search/location")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search contact details by location", 
               description = "Search contact details by location criteria (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<ContactDetailsResponse>>> searchByLocation(
            @Parameter(description = "City filter", example = "Bangalore")
            @RequestParam(required = false) String city,
            
            @Parameter(description = "State filter", example = "Karnataka")
            @RequestParam(required = false) String state,
            
            @Parameter(description = "Country filter", example = "India")
            @RequestParam(required = false) String country,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "city")
            @RequestParam(defaultValue = "city") String sortBy,
            
            @Parameter(description = "Sort direction", example = "ASC")
            @RequestParam(defaultValue = "ASC") Sort.Direction sortDir) {
        
        log.debug("Admin searching contact details by location");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<ContactDetailsResponse> response = contactDetailsService.searchByLocation(city, state, country, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Location search results retrieved successfully", response));
    }

    @GetMapping("/search/verification")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Search contact details by verification status", 
               description = "Search contact details by verification status (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Search results retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<Page<ContactDetailsResponse>>> searchByVerificationStatus(
            @Parameter(description = "Mobile verification status", example = "true")
            @RequestParam(required = false) Boolean mobileVerified,
            
            @Parameter(description = "Email verification status", example = "false")
            @RequestParam(required = false) Boolean emailVerified,
            
            @Parameter(description = "Page number (0-based)", example = "0")
            @RequestParam(defaultValue = "0") @Min(0) int page,
            
            @Parameter(description = "Page size", example = "20")
            @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size,
            
            @Parameter(description = "Sort field", example = "isVerifiedMobile")
            @RequestParam(defaultValue = "isVerifiedMobile") String sortBy,
            
            @Parameter(description = "Sort direction", example = "DESC")
            @RequestParam(defaultValue = "DESC") Sort.Direction sortDir) {
        
        log.debug("Admin searching contact details by verification status");
        
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDir, sortBy));
        Page<ContactDetailsResponse> response = contactDetailsService.searchByVerificationStatus(
                mobileVerified, emailVerified, pageable);
        
        return ResponseEntity.ok(ResponseDto.success("Verification search results retrieved successfully", response));
    }

    @GetMapping("/statistics")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get contact details statistics", 
               description = "Retrieve statistical information about contact details data (Admin only)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin role required")
    })
    public ResponseEntity<ResponseDto<ContactDetailsService.ContactDetailsStats>> getStatistics() {
        
        log.debug("Admin fetching contact details statistics");
        
        ContactDetailsService.ContactDetailsStats stats = contactDetailsService.getStatistics();
        
        return ResponseEntity.ok(ResponseDto.success("Statistics retrieved successfully", stats));
    }
}
