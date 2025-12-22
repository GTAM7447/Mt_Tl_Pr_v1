package com.spring.jwt.FamilyBackground;

import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Comprehensive validation service for family background business rules.
 * Implements complex validation logic that goes beyond simple field validation.
 */
@Service
@Slf4j
public class FamilyBackgroundValidationService {

    /**
     * Validate family background creation request with comprehensive business rules.
     * 
     * @param request the creation request
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCreateRequest(FamilyBackgroundCreateRequest request) {
        List<String> errors = new ArrayList<>();

        validateSiblingRelationships(request.getBrother(), request.getMarriedBrothers(), 
                request.getSisters(), request.getMarriedSisters(), errors);

        validateNameConsistency(request.getFathersName(), request.getMothersName(), 
                request.getMamaSurname(), errors);

        validateOccupations(request.getFatherOccupation(), request.getMotherOccupation(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join("; ", errors);
            log.warn("Family background creation validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validate family background update request with comprehensive business rules.
     * 
     * @param request the update request
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUpdateRequest(FamilyBackgroundUpdateRequest request) {
        List<String> errors = new ArrayList<>();

        if (hasAnyValue(request.getBrother(), request.getMarriedBrothers(), 
                request.getSisters(), request.getMarriedSisters())) {
            validateSiblingRelationships(request.getBrother(), request.getMarriedBrothers(), 
                    request.getSisters(), request.getMarriedSisters(), errors);
        }

        if (hasAnyValue(request.getFathersName(), request.getMothersName(), request.getMamaSurname())) {
            validateNameConsistency(request.getFathersName(), request.getMothersName(), 
                    request.getMamaSurname(), errors);
        }

        if (hasAnyValue(request.getFatherOccupation(), request.getMotherOccupation())) {
            validateOccupations(request.getFatherOccupation(), request.getMotherOccupation(), errors);
        }
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join("; ", errors);
            log.warn("Family background update validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
    }

    /**
     * Validate sibling relationships to ensure logical consistency.
     */
    private void validateSiblingRelationships(Integer brothers, Integer marriedBrothers, 
                                            Integer sisters, Integer marriedSisters, List<String> errors) {
        
        if (brothers != null && marriedBrothers != null && marriedBrothers > brothers) {
            errors.add("Married brothers cannot exceed total brothers");
        }
        
        if (sisters != null && marriedSisters != null && marriedSisters > sisters) {
            errors.add("Married sisters cannot exceed total sisters");
        }

        if (brothers != null && sisters != null) {
            int totalSiblings = brothers + sisters;
            if (totalSiblings > 15) {
                errors.add("Total number of siblings seems unusually high");
            }
        }
    }

    /**
     * Validate name consistency and format.
     */
    private void validateNameConsistency(String fathersName, String mothersName, 
                                       String mamaSurname, List<String> errors) {

        if (fathersName != null && mothersName != null && fathersName.equals(mothersName)) {
            errors.add("Father's and mother's names cannot be identical");
        }

        if (fathersName != null && fathersName.length() < 2) {
            errors.add("Father's name is too short");
        }
        
        if (mothersName != null && mothersName.length() < 2) {
            errors.add("Mother's name is too short");
        }
        
        if (mamaSurname != null && mamaSurname.length() < 2) {
            errors.add("Mama surname is too short");
        }
    }

    /**
     * Validate occupation fields for reasonableness.
     */
    private void validateOccupations(String fatherOccupation, String motherOccupation, List<String> errors) {
        
        if (fatherOccupation != null && fatherOccupation.length() < 2) {
            errors.add("Father's occupation is too short");
        }
        
        if (motherOccupation != null && motherOccupation.length() < 2) {
            errors.add("Mother's occupation is too short");
        }

        if (fatherOccupation != null && isInvalidOccupation(fatherOccupation)) {
            errors.add("Father's occupation appears to be invalid");
        }
        
        if (motherOccupation != null && isInvalidOccupation(motherOccupation)) {
            errors.add("Mother's occupation appears to be invalid");
        }
    }

    /**
     * Check if any of the provided values is not null.
     */
    private boolean hasAnyValue(Object... values) {
        for (Object value : values) {
            if (value != null) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if occupation string appears to be invalid.
     */
    private boolean isInvalidOccupation(String occupation) {
        String lower = occupation.toLowerCase().trim();
        return lower.equals("n/a") || lower.equals("na") || lower.equals("none") || 
               lower.equals("nil") || lower.equals("test") || lower.length() < 2;
    }
}