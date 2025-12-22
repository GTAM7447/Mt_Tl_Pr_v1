package com.spring.jwt.profile.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating an existing user profile.
 * All fields are optional for partial updates (PATCH semantics).
 * Includes version for optimistic locking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {

    @NotNull(message = "Version is required for updates to prevent conflicts")
    private Integer version;

    @Size(max = 45, message = "First name cannot exceed 45 characters")
    private String firstName;

    @Size(max = 45, message = "Middle name cannot exceed 45 characters")
    private String middleName;

    @Size(max = 45, message = "Last name cannot exceed 45 characters")
    private String lastName;

    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;

    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @Pattern(regexp = "ACTIVE|INACTIVE|DEACTIVE", message = "Status must be ACTIVE, INACTIVE, or DEACTIVE")
    private String status;

    @Size(max = 250, message = "Address cannot exceed 250 characters")
    private String address;

    @Size(max = 45, message = "Taluka cannot exceed 45 characters")
    private String taluka;

    @Size(max = 45, message = "District cannot exceed 45 characters")
    private String district;

    @Min(value = 100000, message = "PIN code must be 6 digits")
    @Max(value = 999999, message = "PIN code must be 6 digits")
    private Integer pinCode;

    @Size(max = 45, message = "Religion cannot exceed 45 characters")
    private String religion;

    @Size(max = 45, message = "Caste cannot exceed 45 characters")
    private String caste;

    @Size(max = 45, message = "Marital status cannot exceed 45 characters")
    private String maritalStatus;

    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm")
    @DecimalMax(value = "250.0", message = "Height cannot exceed 250 cm")
    private Double height;

    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 300, message = "Weight cannot exceed 300 kg")
    private Integer weight;

    @Size(max = 45, message = "Blood group cannot exceed 45 characters")
    private String bloodGroup;

    @Size(max = 45, message = "Complexion cannot exceed 45 characters")
    private String complexion;

    @Size(max = 45, message = "Diet cannot exceed 45 characters")
    private String diet;

    private Boolean spectacle;
    private Boolean lens;
    private Boolean physicallyChallenged;

    @Size(max = 45, message = "Home town district cannot exceed 45 characters")
    private String homeTownDistrict;

    @Size(max = 45, message = "Native taluka cannot exceed 45 characters")
    private String nativeTaluka;

    @Size(max = 45, message = "Current city cannot exceed 45 characters")
    private String currentCity;
}
