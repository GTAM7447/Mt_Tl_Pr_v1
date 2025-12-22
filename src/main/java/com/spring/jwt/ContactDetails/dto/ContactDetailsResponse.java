package com.spring.jwt.ContactDetails.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Response DTO for contact details.
 * Provides comprehensive contact information with audit trail.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Contact details response")
public class ContactDetailsResponse {

    @Schema(description = "Contact details ID", example = "1")
    private Integer contactDetailsId;

    @Schema(description = "Full address including house number, street, area", 
            example = "123, MG Road, Koramangala, Bangalore")
    private String fullAddress;

    @Schema(description = "Street address", example = "123, MG Road")
    private String streetAddress;

    @Schema(description = "City name", example = "Bangalore")
    private String city;

    @Schema(description = "State name", example = "Karnataka")
    private String state;

    @Schema(description = "Country name", example = "India")
    private String country;

    @Schema(description = "PIN/ZIP code", example = "560034")
    private String pinCode;

    @Schema(description = "Primary mobile number", example = "+91-9876543210")
    private String mobileNumber;

    @Schema(description = "Alternate mobile number", example = "+91-9876543211")
    private String alternateNumber;

    @Schema(description = "WhatsApp number", example = "+91-9876543210")
    private String whatsappNumber;

    @Schema(description = "Email address", example = "user@example.com")
    private String emailAddress;

    @Schema(description = "Emergency contact person name", example = "John Doe")
    private String emergencyContactName;

    @Schema(description = "Emergency contact number", example = "+91-9876543212")
    private String emergencyContactNumber;

    @Schema(description = "Relationship with emergency contact", example = "Father")
    private String emergencyContactRelation;

    @Schema(description = "Preferred method of contact", example = "Mobile")
    private String preferredContactMethod;

    @Schema(description = "Contact visibility setting", example = "PRIVATE")
    private String contactVisibility;

    @Schema(description = "Whether mobile number is verified", example = "true")
    private Boolean isVerifiedMobile;

    @Schema(description = "Whether email address is verified", example = "false")
    private Boolean isVerifiedEmail;

    @Schema(description = "Number of verification attempts", example = "2")
    private Integer verificationAttempts;

    @Schema(description = "Last verification attempt timestamp")
    private LocalDateTime lastVerificationAttempt;

    @Schema(description = "User ID", example = "123")
    private Integer userId;

    @Schema(description = "Record creation timestamp")
    private LocalDateTime createdAt;

    @Schema(description = "Record last update timestamp")
    private LocalDateTime updatedAt;

    @Schema(description = "User who created the record", example = "123")
    private Integer createdBy;

    @Schema(description = "User who last updated the record", example = "123")
    private Integer updatedBy;

    @Schema(description = "Version for optimistic locking", example = "1")
    private Integer version;
}