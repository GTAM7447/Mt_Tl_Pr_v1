package com.spring.jwt.profile.dto.request;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating a new user profile.
 * All required fields are marked with validation annotations.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateProfileRequest {

    // Basic Info
    @NotBlank(message = "First name cannot be blank")
    @Size(max = 45, message = "First name cannot exceed 45 characters")
    private String firstName;

    @Size(max = 45, message = "Middle name cannot exceed 45 characters")
    private String middleName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 45, message = "Last name cannot exceed 45 characters")
    private String lastName;

    @NotNull(message = "Age cannot be null")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "ACTIVE|INACTIVE|DEACTIVE", message = "Status must be ACTIVE, INACTIVE, or DEACTIVE")
    private String status;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 250, message = "Address cannot exceed 250 characters")
    private String address;

    @NotBlank(message = "Taluka cannot be blank")
    @Size(max = 45, message = "Taluka cannot exceed 45 characters")
    private String taluka;

    @NotBlank(message = "District cannot be blank")
    @Size(max = 45, message = "District cannot exceed 45 characters")
    private String district;

    @NotNull(message = "PIN code cannot be null")
    @Min(value = 100000, message = "PIN code must be 6 digits")
    @Max(value = 999999, message = "PIN code must be 6 digits")
    private Integer pinCode;

    @NotBlank(message = "Religion cannot be blank")
    @Size(max = 45, message = "Religion cannot exceed 45 characters")
    private String religion;

    @NotBlank(message = "Caste cannot be blank")
    @Size(max = 45, message = "Caste cannot exceed 45 characters")
    private String caste;

    @NotBlank(message = "Marital status cannot be blank")
    @Size(max = 45, message = "Marital status cannot exceed 45 characters")
    private String maritalStatus;

    @NotNull(message = "Height cannot be null")
    @DecimalMin(value = "100.0", message = "Height must be at least 100 cm")
    @DecimalMax(value = "250.0", message = "Height cannot exceed 250 cm")
    private Double height;

    @NotNull(message = "Weight cannot be null")
    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 300, message = "Weight cannot exceed 300 kg")
    private Integer weight;

    @NotBlank(message = "Blood group cannot be blank")
    @Size(max = 45, message = "Blood group cannot exceed 45 characters")
    private String bloodGroup;

    @NotBlank(message = "Complexion cannot be blank")
    @Size(max = 45, message = "Complexion cannot exceed 45 characters")
    private String complexion;

    @NotBlank(message = "Diet cannot be blank")
    @Size(max = 45, message = "Diet cannot exceed 45 characters")
    private String diet;

    @NotNull(message = "Spectacle information cannot be null")
    private Boolean spectacle;

    @NotNull(message = "Lens information cannot be null")
    private Boolean lens;

    @NotNull(message = "Physically challenged information cannot be null")
    private Boolean physicallyChallenged;

    @NotBlank(message = "Home town district cannot be blank")
    @Size(max = 45, message = "Home town district cannot exceed 45 characters")
    private String homeTownDistrict;

    @NotBlank(message = "Native taluka cannot be blank")
    @Size(max = 45, message = "Native taluka cannot exceed 45 characters")
    private String nativeTaluka;

    @NotBlank(message = "Current city cannot be blank")
    @Size(max = 45, message = "Current city cannot exceed 45 characters")
    private String currentCity;
}
