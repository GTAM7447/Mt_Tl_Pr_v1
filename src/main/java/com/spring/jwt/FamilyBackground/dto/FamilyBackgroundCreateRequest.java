package com.spring.jwt.FamilyBackground.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for creating family background information.
 * All required fields are validated with appropriate constraints.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyBackgroundCreateRequest {

    @NotBlank(message = "Father's name cannot be blank")
    @Size(max = 45, message = "Father's name cannot exceed 45 characters")
    private String fathersName;

    @NotBlank(message = "Father's occupation cannot be blank")
    @Size(max = 45, message = "Father's occupation cannot exceed 45 characters")
    private String fatherOccupation;

    @NotBlank(message = "Mother's name cannot be blank")
    @Size(max = 45, message = "Mother's name cannot exceed 45 characters")
    private String mothersName;

    @NotBlank(message = "Mother's occupation cannot be blank")
    @Size(max = 45, message = "Mother's occupation cannot exceed 45 characters")
    private String motherOccupation;

    @NotNull(message = "Number of brothers cannot be null")
    @Min(value = 0, message = "Number of brothers cannot be negative")
    @Max(value = 20, message = "Number of brothers cannot exceed 20")
    private Integer brother;

    @NotNull(message = "Number of married brothers cannot be null")
    @Min(value = 0, message = "Number of married brothers cannot be negative")
    @Max(value = 20, message = "Number of married brothers cannot exceed 20")
    private Integer marriedBrothers;

    @NotNull(message = "Number of sisters cannot be null")
    @Min(value = 0, message = "Number of sisters cannot be negative")
    @Max(value = 20, message = "Number of sisters cannot exceed 20")
    private Integer sisters;

    @NotNull(message = "Number of married sisters cannot be null")
    @Min(value = 0, message = "Number of married sisters cannot be negative")
    @Max(value = 20, message = "Number of married sisters cannot exceed 20")
    private Integer marriedSisters;

    @AssertTrue(message = "Married brothers cannot exceed total brothers")
    public boolean isMarriedBrothersValid() {
        return marriedBrothers == null || brother == null || marriedBrothers <= brother;
    }

    @AssertTrue(message = "Married sisters cannot exceed total sisters")
    public boolean isMarriedSistersValid() {
        return marriedSisters == null || sisters == null || marriedSisters <= sisters;
    }

    @NotNull(message = "Inter-caste information cannot be null")
    private Boolean interCasteInFamily;

    @NotBlank(message = "Parent residing information cannot be blank")
    @Size(max = 45, message = "Parent residing information cannot exceed 45 characters")
    private String parentResiding;

    @Size(max = 45, message = "Family wealth information cannot exceed 45 characters")
    private String familyWealth;

    @NotBlank(message = "Mama surname cannot be blank")
    @Size(max = 45, message = "Mama surname cannot exceed 45 characters")
    private String mamaSurname;

    @NotBlank(message = "Mama place cannot be blank")
    @Size(max = 45, message = "Mama place cannot exceed 45 characters")
    private String mamaPlace;

    @Size(max = 45, message = "Family background column cannot exceed 45 characters")
    private String familyBackgroundCol;

    @Size(max = 45, message = "Relative surnames cannot exceed 45 characters")
    private String relativeSurnames;
}