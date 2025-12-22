package com.spring.jwt.profile.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Search criteria for filtering profiles.
 * All fields are optional for flexible searching.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProfileSearchCriteria {

    private String gender;

    private String religion;

    private String caste;

    private String district;

    private String maritalStatus;

    @Min(value = 18, message = "Minimum age must be at least 18")
    @Max(value = 100, message = "Minimum age cannot exceed 100")
    private Integer minAge;

    @Min(value = 18, message = "Maximum age must be at least 18")
    @Max(value = 100, message = "Maximum age cannot exceed 100")
    private Integer maxAge;

    @Min(value = 100, message = "Minimum height must be at least 100 cm")
    @Max(value = 250, message = "Minimum height cannot exceed 250 cm")
    private Double minHeight;

    @Min(value = 100, message = "Maximum height must be at least 100 cm")
    @Max(value = 250, message = "Maximum height cannot exceed 250 cm")
    private Double maxHeight;
}
