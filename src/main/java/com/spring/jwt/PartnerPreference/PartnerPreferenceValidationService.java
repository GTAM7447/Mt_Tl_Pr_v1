package com.spring.jwt.PartnerPreference;

import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validation service for partner preference business rules.
 * Provides comprehensive validation beyond basic field validation.
 */
@Service
@Slf4j
public class PartnerPreferenceValidationService {

    private static final Set<String> VALID_EATING_HABITS = Set.of(
        "Vegetarian", "Non-Vegetarian", "Vegan", "Jain Vegetarian", "Eggetarian", "Occasionally Non-Vegetarian"
    );

    private static final Set<String> VALID_DRINKING_HABITS = Set.of(
        "Non-drinker", "Drinks socially", "Drinks regularly", "Occasional drinker"
    );

    private static final Set<String> VALID_SMOKING_HABITS = Set.of(
        "Non-smoker", "Light smoker", "Regular smoker", "Occasional smoker"
    );

    private static final Set<String> VALID_MARITAL_STATUS = Set.of(
        "Never Married", "Divorced", "Widowed", "Separated", "Awaiting Divorce"
    );

    private static final Set<String> VALID_RESIDENT_STATUS = Set.of(
        "Citizen", "Permanent Resident", "Work Permit", "Student Visa", "Temporary Visa"
    );

    /**
     * Validate create request with comprehensive business rules.
     *
     * @param request the create request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCreateRequest(PartnerPreferenceCreateRequest request) {
        log.debug("Validating partner preference create request");
        
        List<String> errors = new ArrayList<>();
        
        validateAgeRange(request.getAgeRange(), errors);
        validateHeightRange(request.getHeightRange(), errors);
        validateLifestyleChoices(request.getEatingHabits(), request.getDrinkingHabits(), request.getSmokingHabits(), errors);
        validateLocationPreferences(request.getCountryLivingIn(), request.getCityLivingIn(), request.getStateLivingIn(), errors);
        validateReligiousCastePreferences(request.getReligion(), request.getCaste(), request.getSubCaste(), errors);
        validateMaritalStatusPreference(request.getMaritalStatus(), errors);
        validateResidentStatusPreference(request.getResidentStatus(), errors);
        validateIncomeExpectation(request.getPartnerIncome(), errors);
        validateOccupationPreference(request.getPartnerOccupation(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Partner preference create validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Partner preference create request validation passed");
    }

    /**
     * Validate update request with comprehensive business rules.
     *
     * @param request the update request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUpdateRequest(PartnerPreferenceUpdateRequest request) {
        log.debug("Validating partner preference update request");
        
        List<String> errors = new ArrayList<>();

        if (!request.hasAnyFieldToUpdate()) {
            errors.add("At least one field must be provided for update");
        }

        if (request.getAgeRange() != null) {
            validateAgeRange(request.getAgeRange(), errors);
        }
        
        if (request.getHeightRange() != null) {
            validateHeightRange(request.getHeightRange(), errors);
        }
        
        validateLifestyleChoices(request.getEatingHabits(), request.getDrinkingHabits(), request.getSmokingHabits(), errors);
        validateLocationPreferences(request.getCountryLivingIn(), request.getCityLivingIn(), request.getStateLivingIn(), errors);
        validateReligiousCastePreferences(request.getReligion(), request.getCaste(), request.getSubCaste(), errors);
        
        if (request.getMaritalStatus() != null) {
            validateMaritalStatusPreference(request.getMaritalStatus(), errors);
        }
        
        if (request.getResidentStatus() != null) {
            validateResidentStatusPreference(request.getResidentStatus(), errors);
        }
        
        if (request.getPartnerIncome() != null) {
            validateIncomeExpectation(request.getPartnerIncome(), errors);
        }
        
        if (request.getPartnerOccupation() != null) {
            validateOccupationPreference(request.getPartnerOccupation(), errors);
        }
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Partner preference update validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Partner preference update request validation passed");
    }

    /**
     * Validate age range format and values.
     */
    private void validateAgeRange(String ageRange, List<String> errors) {
        if (ageRange == null || ageRange.trim().isEmpty()) {
            return;
        }
        
        String[] parts = ageRange.split("-");
        if (parts.length != 2) {
            errors.add("Age range should be in format 'min-max' (e.g., '25-30')");
            return;
        }
        
        try {
            int minAge = Integer.parseInt(parts[0].trim());
            int maxAge = Integer.parseInt(parts[1].trim());
            
            if (minAge < 18 || maxAge > 80) {
                errors.add("Age range should be between 18 and 80");
            }
            
            if (minAge > maxAge) {
                errors.add("Minimum age cannot be greater than maximum age");
            }
            
            if (maxAge - minAge > 20) {
                errors.add("Age range span should not exceed 20 years");
            }
            
        } catch (NumberFormatException e) {
            errors.add("Age range should contain valid numbers");
        }
    }

    /**
     * Validate height range format.
     */
    private void validateHeightRange(String heightRange, List<String> errors) {
        if (heightRange == null || heightRange.trim().isEmpty()) {
            return;
        }
        
        if (heightRange.length() < 3 || heightRange.length() > 50) {
            errors.add("Height range should be between 3 and 50 characters");
        }
        
        // Basic format validation - should contain some height indicators
        String lowerHeight = heightRange.toLowerCase();
        if (!lowerHeight.contains("'") && !lowerHeight.contains("ft") && 
            !lowerHeight.contains("cm") && !lowerHeight.contains("inch")) {
            errors.add("Height range should contain valid height units (ft, cm, inch, or ')");
        }
    }

    /**
     * Validate lifestyle choices.
     */
    private void validateLifestyleChoices(String eatingHabits, String drinkingHabits, String smokingHabits, List<String> errors) {
        if (eatingHabits != null && !eatingHabits.trim().isEmpty() && 
            !VALID_EATING_HABITS.contains(eatingHabits)) {
            errors.add("Invalid eating habits. Valid options: " + String.join(", ", VALID_EATING_HABITS));
        }
        
        if (drinkingHabits != null && !drinkingHabits.trim().isEmpty() && 
            !VALID_DRINKING_HABITS.contains(drinkingHabits)) {
            errors.add("Invalid drinking habits. Valid options: " + String.join(", ", VALID_DRINKING_HABITS));
        }
        
        if (smokingHabits != null && !smokingHabits.trim().isEmpty() && 
            !VALID_SMOKING_HABITS.contains(smokingHabits)) {
            errors.add("Invalid smoking habits. Valid options: " + String.join(", ", VALID_SMOKING_HABITS));
        }
    }

    /**
     * Validate location preferences consistency.
     */
    private void validateLocationPreferences(String country, String city, String state, List<String> errors) {
        if (city != null && !city.trim().isEmpty()) {
            if ((state == null || state.trim().isEmpty()) && (country == null || country.trim().isEmpty())) {
                log.info("City specified without state or country - this is acceptable but not ideal");
            }
        }

        if (country != null && country.trim().length() < 2) {
            errors.add("Country name should be at least 2 characters long");
        }
        
        if (city != null && city.trim().length() < 2) {
            errors.add("City name should be at least 2 characters long");
        }
        
        if (state != null && state.trim().length() < 2) {
            errors.add("State name should be at least 2 characters long");
        }
    }

    /**
     * Validate religious and caste preferences.
     */
    private void validateReligiousCastePreferences(String religion, String caste, String subCaste, List<String> errors) {
        if (subCaste != null && !subCaste.trim().isEmpty() &&
            (caste == null || caste.trim().isEmpty())) {
            errors.add("Caste must be specified if sub-caste is provided");
        }

        if (religion != null && religion.trim().length() < 2) {
            errors.add("Religion should be at least 2 characters long");
        }
        
        if (caste != null && caste.trim().length() < 2) {
            errors.add("Caste should be at least 2 characters long");
        }
    }

    /**
     * Validate marital status preference.
     */
    private void validateMaritalStatusPreference(String maritalStatus, List<String> errors) {
        if (maritalStatus != null && !maritalStatus.trim().isEmpty() && 
            !VALID_MARITAL_STATUS.contains(maritalStatus)) {
            errors.add("Invalid marital status. Valid options: " + String.join(", ", VALID_MARITAL_STATUS));
        }
    }

    /**
     * Validate resident status preference.
     */
    private void validateResidentStatusPreference(String residentStatus, List<String> errors) {
        if (residentStatus != null && !residentStatus.trim().isEmpty() && 
            !VALID_RESIDENT_STATUS.contains(residentStatus)) {
            errors.add("Invalid resident status. Valid options: " + String.join(", ", VALID_RESIDENT_STATUS));
        }
    }

    /**
     * Validate income expectation.
     */
    private void validateIncomeExpectation(Integer partnerIncome, List<String> errors) {
        if (partnerIncome == null) {
            return;
        }
        
        if (partnerIncome < 100000) {
            errors.add("Partner income expectation seems too low (minimum ₹1,00,000)");
        }
        
        if (partnerIncome > 50000000) {
            errors.add("Partner income expectation seems unrealistic (maximum ₹5,00,00,000)");
        }
    }

    /**
     * Validate occupation preference.
     */
    private void validateOccupationPreference(String partnerOccupation, List<String> errors) {
        if (partnerOccupation != null && partnerOccupation.trim().length() < 2) {
            errors.add("Partner occupation should be at least 2 characters long");
        }
    }

    /**
     * Validate preference completeness for recommendations.
     */
    public void validatePreferenceCompleteness(PartnerPreferenceCreateRequest request) {
        List<String> missingCriticalFields = new ArrayList<>();
        
        if (request.getAgeRange() == null || request.getAgeRange().trim().isEmpty()) {
            missingCriticalFields.add("Age range");
        }
        
        if (request.getHeightRange() == null || request.getHeightRange().trim().isEmpty()) {
            missingCriticalFields.add("Height range");
        }
        
        if (request.getReligion() == null || request.getReligion().trim().isEmpty()) {
            missingCriticalFields.add("Religion");
        }
        
        if (request.getEducation() == null || request.getEducation().trim().isEmpty()) {
            missingCriticalFields.add("Education");
        }
        
        if (!missingCriticalFields.isEmpty()) {
            log.warn("Critical partner preference fields missing: {}", String.join(", ", missingCriticalFields));
        }
    }
}