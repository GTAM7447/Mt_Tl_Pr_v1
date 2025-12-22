package com.spring.jwt.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 7: Contact Details Request
 * Contains contact information
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContactDetailsStepRequest {

    @NotNull(message = "Mobile number is required")
    private Long mobileNumber;

    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Address is required")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @NotNull(message = "PIN code is required")
    @Min(value = 100000, message = "PIN code must be 6 digits")
    @Max(value = 999999, message = "PIN code must be 6 digits")
    private Integer pinCode;

    @Pattern(regexp = "^(PUBLIC|MEMBERS_ONLY|PRIVATE)$", message = "Visibility must be PUBLIC, MEMBERS_ONLY, or PRIVATE")
    @Builder.Default
    private String visibility = "MEMBERS_ONLY";
}