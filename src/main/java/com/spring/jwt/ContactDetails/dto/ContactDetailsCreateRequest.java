package com.spring.jwt.ContactDetails.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating contact details.
 * Includes comprehensive validation and API documentation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Request to create contact details")
public class ContactDetailsCreateRequest {

    @Schema(description = "Full address including house number, street, area", 
            example = "123, MG Road, Koramangala, Bangalore", maxLength = 500)
    @Size(max = 500, message = "Full address cannot exceed 500 characters")
    private String fullAddress;

    @Schema(description = "Street address", example = "123, MG Road", maxLength = 200)
    @Size(max = 200, message = "Street address cannot exceed 200 characters")
    private String streetAddress;

    @Schema(description = "City name", example = "Bangalore", required = true, maxLength = 100)
    @NotBlank(message = "City is required")
    @Size(min = 2, max = 100, message = "City must be between 2 and 100 characters")
    private String city;

    @Schema(description = "State name", example = "Karnataka", maxLength = 100)
    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Schema(description = "Country name", example = "India", required = true, maxLength = 100)
    @NotBlank(message = "Country is required")
    @Size(min = 2, max = 100, message = "Country must be between 2 and 100 characters")
    private String country;

    @Schema(description = "PIN/ZIP code", example = "560034", required = true, maxLength = 20)
    @NotBlank(message = "PIN code is required")
    @Pattern(regexp = "^[A-Za-z0-9\\s-]{3,20}$", message = "PIN code must be 3-20 characters and contain only letters, numbers, spaces, and hyphens")
    private String pinCode;

    @Schema(description = "Primary mobile number", example = "+91-9876543210", required = true, maxLength = 20)
    @NotBlank(message = "Mobile number is required")
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
            allowableValues = {"PRIVATE", "MEMBERS_ONLY", "PUBLIC"},
            required = true)
    @NotBlank(message = "Contact visibility is required")
    @Pattern(regexp = "^(PRIVATE|MEMBERS_ONLY|PUBLIC)$", message = "Contact visibility must be PRIVATE, MEMBERS_ONLY, or PUBLIC")
    private String contactVisibility = "PRIVATE";
}