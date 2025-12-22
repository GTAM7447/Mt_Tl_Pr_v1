package com.spring.jwt.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 6: Partner Preferences Request
 * Contains partner preference criteria
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PartnerPreferencesStepRequest {

    @NotBlank(message = "Looking for cannot be blank")
    @Size(max = 100, message = "Looking for cannot exceed 100 characters")
    private String lookingFor;

    @Size(max = 20, message = "Age range cannot exceed 20 characters")
    private String ageRange;

    @Size(max = 20, message = "Height range cannot exceed 20 characters")
    private String heightRange;

    @Size(max = 50, message = "Religion cannot exceed 50 characters")
    private String religion;

    @Size(max = 50, message = "Caste cannot exceed 50 characters")
    private String caste;

    @Size(max = 100, message = "Education cannot exceed 100 characters")
    private String education;

    @Size(max = 100, message = "Occupation cannot exceed 100 characters")
    private String occupation;

    @Size(max = 30, message = "Income range cannot exceed 30 characters")
    private String incomeRange;

    @Size(max = 200, message = "Location cannot exceed 200 characters")
    private String location;

    @Size(max = 20, message = "Marital status cannot exceed 20 characters")
    private String maritalStatus;

    @Size(max = 30, message = "Eating habits cannot exceed 30 characters")
    private String eatingHabits;

    @Size(max = 20, message = "Drinking habits cannot exceed 20 characters")
    private String drinkingHabits;

    @Size(max = 20, message = "Smoking habits cannot exceed 20 characters")
    private String smokingHabits;
}