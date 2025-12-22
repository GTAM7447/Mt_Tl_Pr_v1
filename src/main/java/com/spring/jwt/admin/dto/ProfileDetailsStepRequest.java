package com.spring.jwt.admin.dto;

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
 * Step 2: Profile Details Request
 * Contains profile information fields
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfileDetailsStepRequest {

    @NotNull(message = "Age cannot be null")
    @Min(value = 18, message = "Age must be at least 18")
    @Max(value = 100, message = "Age cannot exceed 100")
    private Integer age;

    @NotBlank(message = "Gender cannot be blank")
    @Pattern(regexp = "^(MALE|FEMALE|OTHER)$", message = "Gender must be MALE, FEMALE, or OTHER")
    private String gender;

    @NotBlank(message = "Religion cannot be blank")
    @Size(max = 50, message = "Religion cannot exceed 50 characters")
    private String religion;

    @NotBlank(message = "Caste cannot be blank")
    @Size(max = 50, message = "Caste cannot exceed 50 characters")
    private String caste;

    @NotBlank(message = "Marital status cannot be blank")
    @Pattern(regexp = "^(Single|Married|Divorced|Widowed)$", message = "Invalid marital status")
    private String maritalStatus;

    @NotBlank(message = "Status cannot be blank")
    @Pattern(regexp = "^(ACTIVE|INACTIVE|DEACTIVE)$", message = "Status must be ACTIVE, INACTIVE, or DEACTIVE")
    private String status;

    @NotBlank(message = "Diet cannot be blank")
    @Pattern(regexp = "^(Vegetarian|Non-Vegetarian|Vegan|Jain)$", message = "Invalid diet preference")
    private String diet;

    @NotBlank(message = "Blood group cannot be blank")
    @Pattern(regexp = "^(A\\+|A-|B\\+|B-|AB\\+|AB-|O\\+|O-)$", message = "Invalid blood group")
    private String bloodGroup;

    @NotNull(message = "Height cannot be null")
    @Min(value = 100, message = "Height must be at least 100 cm")
    @Max(value = 250, message = "Height cannot exceed 250 cm")
    private Integer height;

    @NotNull(message = "Weight cannot be null")
    @Min(value = 30, message = "Weight must be at least 30 kg")
    @Max(value = 200, message = "Weight cannot exceed 200 kg")
    private Integer weight;

    @NotBlank(message = "Complexion cannot be blank")
    @Size(max = 30, message = "Complexion cannot exceed 30 characters")
    private String complexion;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    @NotBlank(message = "Current city cannot be blank")
    @Size(max = 100, message = "Current city cannot exceed 100 characters")
    private String currentCity;

    @NotBlank(message = "District cannot be blank")
    @Size(max = 100, message = "District cannot exceed 100 characters")
    private String district;

    @NotBlank(message = "Taluka cannot be blank")
    @Size(max = 100, message = "Taluka cannot exceed 100 characters")
    private String taluka;

    @NotNull(message = "PIN code cannot be null")
    @Min(value = 100000, message = "PIN code must be 6 digits")
    @Max(value = 999999, message = "PIN code must be 6 digits")
    private Integer pinCode;

    @NotBlank(message = "Home town district cannot be blank")
    @Size(max = 100, message = "Home town district cannot exceed 100 characters")
    private String homeTownDistrict;

    @NotBlank(message = "Native taluka cannot be blank")
    @Size(max = 100, message = "Native taluka cannot exceed 100 characters")
    private String nativeTaluka;

    @NotNull(message = "Physically challenged information cannot be null")
    private Boolean physicallyChallenged;

    @NotNull(message = "Spectacle information cannot be null")
    private Boolean spectacle;

    @NotNull(message = "Lens information cannot be null")
    private Boolean lens;
}