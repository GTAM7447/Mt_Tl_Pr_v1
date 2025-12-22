package com.spring.jwt.FamilyBackground.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request DTO for updating family background information.
 * All fields are optional for partial updates (PATCH semantics).
 * Includes version for optimistic locking.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FamilyBackgroundUpdateRequest {

    @NotNull(message = "Version is required for updates to prevent conflicts")
    private Integer version;

    @Size(max = 45, message = "Father's name cannot exceed 45 characters")
    private String fathersName;

    @Size(max = 45, message = "Father's occupation cannot exceed 45 characters")
    private String fatherOccupation;

    @Size(max = 45, message = "Mother's name cannot exceed 45 characters")
    private String mothersName;

    @Size(max = 45, message = "Mother's occupation cannot exceed 45 characters")
    private String motherOccupation;

    @Min(value = 0, message = "Number of brothers cannot be negative")
    @Max(value = 20, message = "Number of brothers cannot exceed 20")
    private Integer brother;

    @Min(value = 0, message = "Number of married brothers cannot be negative")
    @Max(value = 20, message = "Number of married brothers cannot exceed 20")
    private Integer marriedBrothers;

    @Min(value = 0, message = "Number of sisters cannot be negative")
    @Max(value = 20, message = "Number of sisters cannot exceed 20")
    private Integer sisters;

    @Min(value = 0, message = "Number of married sisters cannot be negative")
    @Max(value = 20, message = "Number of married sisters cannot exceed 20")
    private Integer marriedSisters;

    private Boolean interCasteInFamily;

    @Size(max = 45, message = "Parent residing information cannot exceed 45 characters")
    private String parentResiding;

    @Size(max = 45, message = "Family wealth information cannot exceed 45 characters")
    private String familyWealth;

    @Size(max = 45, message = "Mama surname cannot exceed 45 characters")
    private String mamaSurname;

    @Size(max = 45, message = "Mama place cannot exceed 45 characters")
    private String mamaPlace;

    @Size(max = 45, message = "Family background column cannot exceed 45 characters")
    private String familyBackgroundCol;

    @Size(max = 45, message = "Relative surnames cannot exceed 45 characters")
    private String relativeSurnames;

    @AssertTrue(message = "Married brothers cannot exceed total brothers")
    public boolean isMarriedBrothersValid() {
        return marriedBrothers == null || brother == null || marriedBrothers <= brother;
    }

    @AssertTrue(message = "Married sisters cannot exceed total sisters")
    public boolean isMarriedSistersValid() {
        return marriedSisters == null || sisters == null || marriedSisters <= sisters;
    }
}