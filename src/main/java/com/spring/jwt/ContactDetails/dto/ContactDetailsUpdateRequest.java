package com.spring.jwt.ContactDetails.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating contact details.
 * Supports partial updates with comprehensive validation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to update contact details")
public class ContactDetailsUpdateRequest {

    @Schema(description = "Version for optimistic locking", example = "1", required = true)
    @NotNull(message = "Version is required for optimistic locking")
    @Min(value = 0, message = "Version must be non-negative")
    private Integer version;

    @Schema(description = "Full address including house number, street, area", 
            example = "123, MG Road, Koramangala, Bangalore", maxLength = 500)
    @Size(max = 500, message = "Full address cannot exceed 500 characters")
    private String fullAddress;

    @Schema(description = "Street address", example = "123, MG Road", maxLength = 200)
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    private String streetAddress;

    @Schema(description = "City name", example = "Bangalore", maxLength = 100)
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @Schema(description = "State name", example = "Karnataka", maxLength = 100)
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Schema(description = "Country name", example = "India", maxLength = 100)
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    @Schema(description = "PIN/ZIP code", example = "560034", maxLength = 20)
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,20}$", message = "PIN code must be 3-20 characters and contain only letters, numbers, spaces, and hyphens")
    private String pinCode;

    @Schema(description = "Primary mobile number", example = "+91-9876543210", maxLength = 20)
    @Pattern(regexp = "^[+]?[0-9\\s()-]{10,20}$", message = "Mobile number must be 10-20 characters and contain only numbers, spaces, +, -, and ()")
    private String mobileNumber;

    @Schema(description = "Alternate mobile number", example = "+91-9876543211", maxLength = 20)
    @Pattern(regexp = "^[+]?[0-9\\s()-]{10,20}$", message = "Alternate number must be 10-20 characters and contain only numbers, spaces, +, -, and ()")
    private String alternateNumber;

    @Schema(description = "WhatsApp number", example = "+91-9876543210", maxLength = 20)
    @Pattern(regexp = "^[+]?[0-9\\s()-]{10,20}$", message = "WhatsApp number must be 10-20 characters and contain only numbers, spaces, +, -, and ()")
    private String whatsappNumber;

    @Schema(description = "Email address", example = "user@example.com", maxLength = 100)
    @Email(message = "Email address must be valid")
    @Size(max = 100, message = "Email address cannot exceed 100 characters")
    private String emailAddress;

    @Schema(description = "Emergency contact person name", example = "John Doe", maxLength = 100)
    @Size(max = 100, message = "Emergency contact name cannot exceed 100 characters")
    private String emergencyContactName;

    @Schema(description = "Emergency contact number", example = "+91-9876543212", maxLength = 20)
    @Pattern(regexp = "^[+]?[0-9\\s()-]{10,20}$", message = "Emergency contact number must be 10-20 characters and contain only numbers, spaces, +, -, and ()")
    private String emergencyContactNumber;

    @Schema(description = "Relationship with emergency contact", example = "Father", maxLength = 50)
    @Size(max = 50, message = "Emergency contact relation cannot exceed 50 characters")
    private String emergencyContactRelation;

    @Schema(description = "Preferred method of contact", 
            example = "Mobile", 
            allowableValues = {"Mobile", "WhatsApp", "Email", "Alternate Number"},
            maxLength = 50)
    @Size(max = 50, message = "Preferred contact method cannot exceed 50 characters")
    private String preferredContactMethod;

    @Schema(description = "Contact visibility setting", 
            example = "PRIVATE", 
            allowableValues = {"PRIVATE", "MEMBERS_ONLY", "PUBLIC"})
    @Pattern(regexp = "^(PRIVATE|MEMBERS_ONLY|PUBLIC)$", message = "Contact visibility must be PRIVATE, MEMBERS_ONLY, or PUBLIC")
    private String contactVisibility;

    /**
     * Check if any field is provided for update.
     * @return true if at least one field is provided
     */
    public boolean hasAnyFieldToUpdate() {
        return fullAddress != null || streetAddress != null || city != null || 
               state != null || country != null || pinCode != null ||
               mobileNumber != null || alternateNumber != null || whatsappNumber != null ||
               emailAddress != null || emergencyContactName != null || 
               emergencyContactNumber != null || emergencyContactRelation != null ||
               preferredContactMethod != null || contactVisibility != null;
    }
}