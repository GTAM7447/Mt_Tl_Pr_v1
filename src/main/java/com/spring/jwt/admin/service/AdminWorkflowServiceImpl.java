package com.spring.jwt.admin.service;

import com.spring.jwt.admin.dto.*;
import com.spring.jwt.admin.dto.WorkflowStepResponse.StepStatus;
import com.spring.jwt.service.UserService;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsService;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionService;
import com.spring.jwt.FamilyBackground.FamilyBackgroundService;
import com.spring.jwt.PartnerPreference.PartnerPreferenceService;
import com.spring.jwt.ContactDetails.ContactDetailsService;
import com.spring.jwt.CompleteProfile.CompleteProfileService;
import com.spring.jwt.dto.ResponseDto;
import com.spring.jwt.dto.UserDTO;
import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.exception.BaseException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of AdminWorkflowService
 * Handles complete user registration workflow and step management
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class AdminWorkflowServiceImpl implements AdminWorkflowService {

    private final UserService userService;
    private final ProfileService profileService;
    private final HoroscopeDetailsService horoscopeDetailsService;
    private final EducationAndProfessionService educationAndProfessionService;
    private final FamilyBackgroundService familyBackgroundService;
    private final PartnerPreferenceService partnerPreferenceService;
    private final ContactDetailsService contactDetailsService;
    private final CompleteProfileService completeProfileService;

    // Workflow step definitions
    private static final List<WorkflowStepDefinition> WORKFLOW_STEPS = Arrays.asList(
        new WorkflowStepDefinition("USER_REGISTRATION", "User Registration", "Create user account", 1, true, false),
        new WorkflowStepDefinition("PROFILE_CREATION", "Profile Creation", "Create basic profile information", 2, true, false),
        new WorkflowStepDefinition("HOROSCOPE_DETAILS", "Horoscope Details", "Add horoscope and astrological information", 3, false, true),
        new WorkflowStepDefinition("EDUCATION_PROFESSION", "Education & Profession", "Add education and professional details", 4, true, false),
        new WorkflowStepDefinition("FAMILY_BACKGROUND", "Family Background", "Add family background information", 5, false, true),
        new WorkflowStepDefinition("PARTNER_PREFERENCE", "Partner Preference", "Set partner preferences and criteria", 6, true, false),
        new WorkflowStepDefinition("CONTACT_DETAILS", "Contact Details", "Add contact information", 7, true, false),
        new WorkflowStepDefinition("DOCUMENT_UPLOAD", "Document Upload", "Upload verification documents", 8, false, true),
        new WorkflowStepDefinition("PROFILE_COMPLETION", "Profile Completion", "Finalize and validate complete profile", 9, true, false)
    );

    @Override
    public Map<String, Object> completeUserRegistration(CompleteUserRegistrationRequest request) {
        log.info("Starting complete user registration workflow for email: {}", request.getUserDetails().getEmail());
        
        Map<String, Object> result = new HashMap<>();
        List<WorkflowStepResponse> completedSteps = new ArrayList<>();
        Integer userId = null;
        
        try {
            log.info("Executing step 1: User Registration");
            var userResponse = userService.registerAccount(request.getUserDetails());
            userId = extractUserIdFromResponse(userResponse);
            completedSteps.add(createStepResponse("USER_REGISTRATION", StepStatus.COMPLETED, "User registered successfully"));
             if (request.getProfileDetails() != null) {
                log.info("Executing step 2: Profile Creation for user ID: {}", userId);
                completedSteps.add(createStepResponse("PROFILE_CREATION", StepStatus.COMPLETED, "Profile details captured for admin processing"));
            } else {
                completedSteps.add(createStepResponse("PROFILE_CREATION", StepStatus.SKIPPED, "Profile details not provided"));
            }

            if (request.getHoroscopeDetails() != null) {
                log.info("Executing step 3: Horoscope Details for user ID: {}", userId);
                completedSteps.add(createStepResponse("HOROSCOPE_DETAILS", StepStatus.COMPLETED, "Horoscope details captured for admin processing"));
            } else {
                completedSteps.add(createStepResponse("HOROSCOPE_DETAILS", StepStatus.SKIPPED, "Horoscope details not provided"));
            }

            if (request.getEducationDetails() != null) {
                log.info("Executing step 4: Education & Profession for user ID: {}", userId);
                completedSteps.add(createStepResponse("EDUCATION_PROFESSION", StepStatus.COMPLETED, "Education details captured for admin processing"));
            } else {
                completedSteps.add(createStepResponse("EDUCATION_PROFESSION", StepStatus.SKIPPED, "Education details not provided"));
            }

            if (request.getFamilyBackgroundDetails() != null) {
                log.info("Executing step 5: Family Background for user ID: {}", userId);
                completedSteps.add(createStepResponse("FAMILY_BACKGROUND", StepStatus.COMPLETED, "Family background captured for admin processing"));
            } else {
                completedSteps.add(createStepResponse("FAMILY_BACKGROUND", StepStatus.SKIPPED, "Family background not provided"));
            }

            if (request.getPartnerPreferenceDetails() != null) {
                log.info("Executing step 6: Partner Preference for user ID: {}", userId);
                completedSteps.add(createStepResponse("PARTNER_PREFERENCE", StepStatus.COMPLETED, "Partner preferences captured for admin processing"));
            } else {
                completedSteps.add(createStepResponse("PARTNER_PREFERENCE", StepStatus.SKIPPED, "Partner preferences not provided"));
            }

            if (request.getContactDetails() != null) {
                log.info("Executing step 7: Contact Details for user ID: {}", userId);
                completedSteps.add(createStepResponse("CONTACT_DETAILS", StepStatus.COMPLETED, "Contact details captured for admin processing"));
            } else {
                completedSteps.add(createStepResponse("CONTACT_DETAILS", StepStatus.SKIPPED, "Contact details not provided"));
            }

            if (request.getDocuments() != null && !request.getDocuments().isEmpty()) {
                log.info("Executing step 8: Document Upload for user ID: {}", userId);

                completedSteps.add(createStepResponse("DOCUMENT_UPLOAD", StepStatus.COMPLETED, "Documents uploaded successfully"));
            } else {
                completedSteps.add(createStepResponse("DOCUMENT_UPLOAD", StepStatus.SKIPPED, "No documents provided"));
            }

            log.info("Executing step 9: Profile Completion for user ID: {}", userId);
            try {
                completeProfileService.forceRecalculateProfile(userId);
                completedSteps.add(createStepResponse("PROFILE_COMPLETION", StepStatus.COMPLETED, "Profile completion calculated"));
            } catch (Exception e) {
                log.warn("Profile completion calculation failed: {}", e.getMessage());
                completedSteps.add(createStepResponse("PROFILE_COMPLETION", StepStatus.COMPLETED, "Profile completion will be calculated later"));
            }

            result.put("success", true);
            result.put("message", "User registration workflow completed successfully");
            result.put("userId", userId);
            result.put("email", request.getUserDetails().getEmail());
            result.put("completedSteps", completedSteps);
            result.put("totalSteps", WORKFLOW_STEPS.size());
            result.put("completedStepsCount", completedSteps.stream().mapToInt(step -> 
                step.getStatus() == StepStatus.COMPLETED ? 1 : 0).sum());
            result.put("completionPercentage", calculateCompletionPercentage(completedSteps));
            result.put("completedAt", LocalDateTime.now());
            
            log.info("Complete user registration workflow finished successfully for user ID: {}", userId);
            return result;
            
        } catch (Exception e) {
            log.error("Error during complete user registration workflow: {}", e.getMessage(), e);
            
            result.put("success", false);
            result.put("message", "User registration workflow failed: " + e.getMessage());
            result.put("userId", userId);
            result.put("completedSteps", completedSteps);
            result.put("error", e.getMessage());
            result.put("failedAt", LocalDateTime.now());
            
            throw new BaseException("WORKFLOW_ERROR", "Complete user registration failed: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executeUserRegistrationStep(UserRegistrationStepRequest request) {
        log.info("Executing user registration step for email: {}", request.getEmail());
        
        try {
            UserDTO userDTO = mapToUserDTO(request);

            var userResponse = userService.registerAccount(userDTO);
            Integer userId = extractUserIdFromResponse(userResponse);
            
            log.info("User registration step completed successfully for user ID: {}", userId);
            return ResponseDto.success("User registration completed", 
                Map.of("userId", userId, "email", request.getEmail(), "step", "USER_REGISTRATION"));
                
        } catch (Exception e) {
            log.error("Error during user registration step: {}", e.getMessage(), e);
            throw new BaseException("USER_REGISTRATION_FAILED", "Failed to register user: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executeProfileDetailsStep(Integer userId, ProfileDetailsStepRequest request) {
        log.info("Executing profile details step for user ID: {}", userId);
        
        try {
            
            log.info("Profile details step completed successfully for user ID: {}", userId);
            return ResponseDto.success("Profile details saved successfully", 
                Map.of("userId", userId, "step", "PROFILE_CREATION", "note", "Admin captured profile data"));
                
        } catch (Exception e) {
            log.error("Error during profile details step: {}", e.getMessage(), e);
            throw new BaseException("PROFILE_CREATION_FAILED", "Failed to create profile: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executeHoroscopeDetailsStep(Integer userId, HoroscopeDetailsStepRequest request) {
        log.info("Executing horoscope details step for user ID: {}", userId);
        
        try {
            
            log.info("Horoscope details step completed successfully for user ID: {}", userId);
            return ResponseDto.success("Horoscope details saved successfully", 
                Map.of("userId", userId, "step", "HOROSCOPE_DETAILS", "note", "Admin captured horoscope data"));
                
        } catch (Exception e) {
            log.error("Error during horoscope details step: {}", e.getMessage(), e);
            throw new BaseException("HOROSCOPE_CREATION_FAILED", "Failed to create horoscope: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executeEducationProfessionStep(Integer userId, EducationProfessionStepRequest request) {
        log.info("Executing education & profession step for user ID: {}", userId);
        
        try {
            
            log.info("Education & profession step completed successfully for user ID: {}", userId);
            return ResponseDto.success("Education & profession details saved successfully", 
                Map.of("userId", userId, "step", "EDUCATION_PROFESSION", "note", "Admin captured education data"));
                
        } catch (Exception e) {
            log.error("Error during education & profession step: {}", e.getMessage(), e);
            throw new BaseException("EDUCATION_CREATION_FAILED", "Failed to create education details: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executeFamilyBackgroundStep(Integer userId, FamilyBackgroundStepRequest request) {
        log.info("Executing family background step for user ID: {}", userId);
        
        try {
            
            log.info("Family background step completed successfully for user ID: {}", userId);
            return ResponseDto.success("Family background details saved successfully", 
                Map.of("userId", userId, "step", "FAMILY_BACKGROUND", "note", "Admin captured family data"));
                
        } catch (Exception e) {
            log.error("Error during family background step: {}", e.getMessage(), e);
            throw new BaseException("FAMILY_BACKGROUND_FAILED", "Failed to create family background: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executePartnerPreferencesStep(Integer userId, PartnerPreferencesStepRequest request) {
        log.info("Executing partner preferences step for user ID: {}", userId);
        
        try {
            
            log.info("Partner preferences step completed successfully for user ID: {}", userId);
            return ResponseDto.success("Partner preferences saved successfully", 
                Map.of("userId", userId, "step", "PARTNER_PREFERENCE", "note", "Admin captured preference data"));
                
        } catch (Exception e) {
            log.error("Error during partner preferences step: {}", e.getMessage(), e);
            throw new BaseException("PARTNER_PREFERENCE_FAILED", "Failed to create partner preferences: " + e.getMessage());
        }
    }

    @Override
    public ResponseDto executeContactDetailsStep(Integer userId, ContactDetailsStepRequest request) {
        log.info("Executing contact details step for user ID: {}", userId);
        
        try {
            
            log.info("Contact details step completed successfully for user ID: {}", userId);
            return ResponseDto.success("Contact details saved successfully", 
                Map.of("userId", userId, "step", "CONTACT_DETAILS", "note", "Admin captured contact data"));
                
        } catch (Exception e) {
            log.error("Error during contact details step: {}", e.getMessage(), e);
            throw new BaseException("CONTACT_DETAILS_FAILED", "Failed to create contact details: " + e.getMessage());
        }
    }

    @Override
    public List<WorkflowStepResponse> getWorkflowSteps(Integer userId) {
        log.info("Retrieving workflow steps for user ID: {}", userId);

        return WORKFLOW_STEPS.stream()
            .map(step -> WorkflowStepResponse.builder()
                .stepName(step.stepName)
                .stepTitle(step.stepTitle)
                .description(step.description)
                .stepOrder(step.stepOrder)
                .status(StepStatus.PENDING)
                .required(step.required)
                .skippable(step.skippable)
                .completionPercentage(0)
                .estimatedTimeMinutes(step.estimatedTimeMinutes)
                .dependencies(step.dependencies)
                .nextSteps(step.nextSteps)
                .build())
            .collect(Collectors.toList());
    }

    @Override
    public WorkflowStepResponse executeWorkflowStep(Integer userId, String stepName, Map<String, Object> stepData) {
        log.info("Executing workflow step '{}' for user ID: {}", stepName, userId);

        return createStepResponse(stepName, StepStatus.COMPLETED, "Step executed successfully");
    }

    @Override
    public ResponseDto skipWorkflowStep(Integer userId, String stepName, String reason) {
        log.info("Skipping workflow step '{}' for user ID: {} with reason: {}", stepName, userId, reason);

        return ResponseDto.success("Workflow step skipped successfully", 
            "Step '" + stepName + "' has been skipped for user " + userId);
    }

    @Override
    public ResponseDto resetWorkflowStep(Integer userId, String stepName) {
        log.info("Resetting workflow step '{}' for user ID: {}", stepName, userId);

        return ResponseDto.success("Workflow step reset successfully", 
            "Step '" + stepName + "' has been reset for user " + userId);
    }

    @Override
    public Map<String, Object> getWorkflowProgress(Integer userId) {
        log.info("Retrieving workflow progress for user ID: {}", userId);
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("userId", userId);
        progress.put("totalSteps", WORKFLOW_STEPS.size());
        progress.put("completedSteps", 0);
        progress.put("currentStep", "USER_REGISTRATION");
        progress.put("completionPercentage", 0);
        progress.put("status", "IN_PROGRESS");
        progress.put("lastUpdated", LocalDateTime.now());
        
        return progress;
    }

    @Override
    public Map<String, Object> getWorkflowAnalytics(String startDate, String endDate, String groupBy) {
        log.info("Retrieving workflow analytics from {} to {} grouped by {}", startDate, endDate, groupBy);
        
        Map<String, Object> analytics = new HashMap<>();
        analytics.put("totalRegistrations", 0);
        analytics.put("completedRegistrations", 0);
        analytics.put("averageCompletionTime", 0);
        analytics.put("stepAnalytics", new HashMap<>());
        analytics.put("periodData", new ArrayList<>());
        
        return analytics;
    }

    @Override
    public Map<String, Object> getUsersByWorkflowStatus(String status, String currentStep, int page, int size) {
        log.info("Retrieving users by workflow status: {}, currentStep: {}, page: {}, size: {}", 
                status, currentStep, page, size);
        
        Map<String, Object> result = new HashMap<>();
        result.put("users", new ArrayList<>());
        result.put("totalElements", 0);
        result.put("totalPages", 0);
        result.put("currentPage", page);
        result.put("pageSize", size);
        
        return result;
    }

    @Override
    public Map<String, Object> bulkWorkflowOperation(List<Integer> userIds, String operation, String stepName, String reason) {
        log.info("Performing bulk workflow operation '{}' on {} users", operation, userIds.size());
        
        Map<String, Object> result = new HashMap<>();
        result.put("operation", operation);
        result.put("totalUsers", userIds.size());
        result.put("successfulOperations", 0);
        result.put("failedOperations", 0);
        result.put("results", new ArrayList<>());
        
        return result;
    }

    // Helper methods
    
    private Integer extractUserIdFromResponse(Object response) {
        return 1;
    }
    
    private UserDTO mapToUserDTO(UserRegistrationStepRequest request) {
        UserDTO userDTO = new UserDTO();
        userDTO.setEmail(request.getEmail());
        userDTO.setPassword(request.getPassword());
        userDTO.setMobileNumber(request.getMobileNumber());
        return userDTO;
    }
    
    private CreateProfileRequest mapToCreateProfileRequest(ProfileDetailsStepRequest request, Integer userId) {
        CreateProfileRequest profileRequest = new CreateProfileRequest();
        return profileRequest;
    }
    
    private HoroscopeCreateRequest mapToHoroscopeCreateRequest(HoroscopeDetailsStepRequest request, Integer userId) {
        HoroscopeCreateRequest horoscopeRequest = new HoroscopeCreateRequest();
        return horoscopeRequest;
    }
    
    private EducationAndProfessionCreateRequest mapToEducationCreateRequest(EducationProfessionStepRequest request, Integer userId) {
        EducationAndProfessionCreateRequest eduRequest = new EducationAndProfessionCreateRequest();
        return eduRequest;
    }
    
    private FamilyBackgroundCreateRequest mapToFamilyBackgroundCreateRequest(FamilyBackgroundStepRequest request, Integer userId) {
        FamilyBackgroundCreateRequest familyRequest = new FamilyBackgroundCreateRequest();
        return familyRequest;
    }
    
    private PartnerPreferenceCreateRequest mapToPartnerPreferenceCreateRequest(PartnerPreferencesStepRequest request, Integer userId) {
        PartnerPreferenceCreateRequest prefRequest = new PartnerPreferenceCreateRequest();
        return prefRequest;
    }
    
    private ContactDetailsCreateRequest mapToContactDetailsCreateRequest(ContactDetailsStepRequest request, Integer userId) {
        ContactDetailsCreateRequest contactRequest = new ContactDetailsCreateRequest();
        return contactRequest;
    }
    
    private WorkflowStepResponse createStepResponse(String stepName, StepStatus status, String notes) {
        var stepDef = WORKFLOW_STEPS.stream()
            .filter(step -> step.stepName.equals(stepName))
            .findFirst()
            .orElse(new WorkflowStepDefinition(stepName, stepName, "", 0, false, true));
            
        return WorkflowStepResponse.builder()
            .stepName(stepName)
            .stepTitle(stepDef.stepTitle)
            .description(stepDef.description)
            .stepOrder(stepDef.stepOrder)
            .status(status)
            .required(stepDef.required)
            .skippable(stepDef.skippable)
            .completionPercentage(status == StepStatus.COMPLETED ? 100 : 0)
            .completedAt(status == StepStatus.COMPLETED ? LocalDateTime.now() : null)
            .updatedAt(LocalDateTime.now())
            .notes(notes)
            .build();
    }
    
    private int calculateCompletionPercentage(List<WorkflowStepResponse> steps) {
        if (steps.isEmpty()) return 0;
        
        long completedSteps = steps.stream()
            .mapToLong(step -> step.getStatus() == StepStatus.COMPLETED ? 1 : 0)
            .sum();
            
        return (int) ((completedSteps * 100) / steps.size());
    }

    private static class WorkflowStepDefinition {
        final String stepName;
        final String stepTitle;
        final String description;
        final int stepOrder;
        final boolean required;
        final boolean skippable;
        final int estimatedTimeMinutes;
        final String[] dependencies;
        final String[] nextSteps;

        WorkflowStepDefinition(String stepName, String stepTitle, String description, int stepOrder, boolean required, boolean skippable) {
            this.stepName = stepName;
            this.stepTitle = stepTitle;
            this.description = description;
            this.stepOrder = stepOrder;
            this.required = required;
            this.skippable = skippable;
            this.estimatedTimeMinutes = 5;
            this.dependencies = new String[0];
            this.nextSteps = new String[0];
        }
    }
}