package com.spring.jwt.admin.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 4: Education & Profession Request
 * Contains education and professional details
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EducationProfessionStepRequest {

    @NotBlank(message = "Education field cannot be blank")
    @Size(max = 100, message = "Education cannot exceed 100 characters")
    private String education;

    @NotBlank(message = "Degree cannot be blank")
    @Size(max = 100, message = "Degree cannot exceed 100 characters")
    private String degree;

    @NotBlank(message = "Occupation cannot be blank")
    @Size(max = 100, message = "Occupation cannot exceed 100 characters")
    private String occupation;

    @NotBlank(message = "Occupation details are required for this type of occupation")
    @Size(max = 500, message = "Occupation details cannot exceed 500 characters")
    private String occupationDetailsValid;

    @NotNull(message = "Income per year is required")
    @Min(value = 0, message = "Income cannot be negative")
    private Long incomePerYear;
}