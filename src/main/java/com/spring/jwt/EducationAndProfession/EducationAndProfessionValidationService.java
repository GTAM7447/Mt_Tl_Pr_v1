package com.spring.jwt.EducationAndProfession;

import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Validation service for education and profession business rules.
 * Provides comprehensive validation beyond basic field validation.
 */
@Service
@Slf4j
public class EducationAndProfessionValidationService {

    // Valid education levels
    private static final Set<String> VALID_EDUCATION_LEVELS = Set.of(
        "10th", "12th", "Diploma", "Bachelor's Degree", "Master's Degree", 
        "PhD", "Professional Degree", "Certificate Course", "Other"
    );

    // High-income occupations that require details
    private static final Set<String> DETAIL_REQUIRED_OCCUPATIONS = Set.of(
        "engineer", "manager", "consultant", "developer", "architect", 
        "analyst", "specialist", "lead", "director", "executive"
    );

    /**
     * Validate create request with comprehensive business rules.
     *
     * @param request the create request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCreateRequest(EducationAndProfessionCreateRequest request) {
        log.debug("Validating education and profession create request");
        
        List<String> errors = new ArrayList<>();

        validateEducationLevel(request.getEducation(), errors);
        validateOccupationDetails(request.getOccupation(), request.getOccupationDetails(), errors);
        validateIncomeExperienceConsistency(request.getIncomePerYear(), request.getExperienceYears(), errors);
        validateCompanyInformation(request.getCompanyName(), request.getWorkLocation(), errors);
        validateDegreeEducationConsistency(request.getEducation(), request.getDegree(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Education and profession create validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Education and profession create request validation passed");
    }

    /**
     * Validate update request with comprehensive business rules.
     *
     * @param request the update request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUpdateRequest(EducationAndProfessionUpdateRequest request) {
        log.debug("Validating education and profession update request");
        
        List<String> errors = new ArrayList<>();

        if (!request.hasAnyFieldToUpdate()) {
            errors.add("At least one field must be provided for update");
        }

        if (request.getEducation() != null) {
            validateEducationLevel(request.getEducation(), errors);
        }
        
        if (request.getOccupation() != null || request.getOccupationDetails() != null) {
            validateOccupationDetails(request.getOccupation(), request.getOccupationDetails(), errors);
        }
        
        if (request.getIncomePerYear() != null || request.getExperienceYears() != null) {
            validateIncomeExperienceConsistency(request.getIncomePerYear(), request.getExperienceYears(), errors);
        }
        
        if (request.getCompanyName() != null || request.getWorkLocation() != null) {
            validateCompanyInformation(request.getCompanyName(), request.getWorkLocation(), errors);
        }
        
        if (request.getEducation() != null || request.getDegree() != null) {
            validateDegreeEducationConsistency(request.getEducation(), request.getDegree(), errors);
        }
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Education and profession update validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Education and profession update request validation passed");
    }

    /**
     * Validate education level against predefined standards.
     */
    private void validateEducationLevel(String education, List<String> errors) {
        if (education == null || education.trim().isEmpty()) {
            return;
        }
        
        // Check if education level is reasonable
        String lowerEducation = education.toLowerCase().trim();
        boolean isValid = VALID_EDUCATION_LEVELS.stream()
                .anyMatch(level -> lowerEducation.contains(level.toLowerCase()));
        
        if (!isValid) {
            boolean hasEducationKeywords = lowerEducation.contains("degree") || 
                                         lowerEducation.contains("diploma") || 
                                         lowerEducation.contains("certificate") ||
                                         lowerEducation.contains("bachelor") ||
                                         lowerEducation.contains("master") ||
                                         lowerEducation.contains("phd");
            
            if (!hasEducationKeywords) {
                errors.add("Education level should be a recognized qualification");
            }
        }
    }

    /**
     * Validate occupation details requirement based on occupation type.
     */
    private void validateOccupationDetails(String occupation, String occupationDetails, List<String> errors) {
        if (occupation == null) {
            return;
        }
        
        String lowerOccupation = occupation.toLowerCase().trim();
        boolean requiresDetails = DETAIL_REQUIRED_OCCUPATIONS.stream()
                .anyMatch(keyword -> lowerOccupation.contains(keyword));
        
        if (requiresDetails && (occupationDetails == null || occupationDetails.trim().isEmpty())) {
            errors.add("Occupation details are required for " + occupation);
        }
    }

    /**
     * Validate consistency between income and experience.
     */
    private void validateIncomeExperienceConsistency(Integer income, Integer experience, List<String> errors) {
        if (income == null || experience == null) {
            return;
        }

        if (experience == 0 && income > 1000000) {
            errors.add("Income seems too high for a fresher");
        }
        
        if (experience >= 1 && experience <= 2 && income > 3000000) {
            errors.add("Income seems too high for 1-2 years of experience");
        }
        
        if (experience >= 3 && experience <= 5 && income > 8000000) {
            errors.add("Income seems too high for 3-5 years of experience");
        }
        
        if (experience > 20 && income < 500000) {
            errors.add("Income seems too low for 20+ years of experience");
        }
    }

    /**
     * Validate company information consistency.
     */
    private void validateCompanyInformation(String companyName, String workLocation, List<String> errors) {
        if (companyName != null && !companyName.trim().isEmpty() &&
            (workLocation == null || workLocation.trim().isEmpty())) {
            log.info("Company name provided without work location - this is acceptable but not ideal");
        }

        if (companyName != null && companyName.trim().length() < 2) {
            errors.add("Company name should be at least 2 characters long");
        }
    }

    /**
     * Validate consistency between education and degree.
     */
    private void validateDegreeEducationConsistency(String education, String degree, List<String> errors) {
        if (education == null || degree == null) {
            return;
        }
        
        String lowerEducation = education.toLowerCase();
        String lowerDegree = degree.toLowerCase();

        if (lowerEducation.contains("bachelor") && lowerDegree.contains("master")) {
            errors.add("Degree level seems higher than education level");
        }
        
        if (lowerEducation.contains("master") && lowerDegree.contains("bachelor")) {
            errors.add("Education level seems higher than degree level");
        }
        
        if (lowerEducation.contains("phd") && !lowerDegree.contains("phd") && !lowerDegree.contains("doctorate")) {
            errors.add("PhD education should have corresponding doctorate degree");
        }
    }

    /**
     * Validate income range based on location and occupation.
     */
    public void validateIncomeRange(Integer income, String workLocation, String occupation) {
        if (income == null) {
            return;
        }

        if (income < 100000) {
            log.warn("Income {} seems very low for occupation: {}", income, occupation);
        }
        
        if (income > 50000000) {
            log.warn("Income {} seems very high for occupation: {}", income, occupation);
        }
    }
}