package com.spring.jwt.admin.dto;

import com.spring.jwt.dto.UserDTO;

import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * Complete user registration request for admin workflow
 * Contains all necessary data to create a user with full profile
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompleteUserRegistrationRequest {

    @NotNull(message = "User details are required")
    @Valid
    private UserDTO userDetails;

    @Valid
    private CreateProfileRequest profileDetails;

    @Valid
    private HoroscopeCreateRequest horoscopeDetails;

    @Valid
    private EducationAndProfessionCreateRequest educationDetails;

    @Valid
    private FamilyBackgroundCreateRequest familyBackgroundDetails;

    @Valid
    private PartnerPreferenceCreateRequest partnerPreferenceDetails;

    @Valid
    private ContactDetailsCreateRequest contactDetails;

    /**
     * Document information for file uploads
     * Key: document type, Value: document metadata
     */
    private Map<String, DocumentInfo> documents;

    /**
     * Additional workflow options
     */
    private WorkflowOptions workflowOptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DocumentInfo {
        private String fileName;
        private String description;
        private String documentType;
        private boolean verified;
        private String notes;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkflowOptions {
        
        /**
         * Whether to skip email verification
         */
        @Builder.Default
        private boolean skipEmailVerification = false;

        /**
         * Whether to auto-verify all documents
         */
        @Builder.Default
        private boolean autoVerifyDocuments = false;

        /**
         * Whether to send welcome email
         */
        @Builder.Default
        private boolean sendWelcomeEmail = true;

        /**
         * Whether to activate user account immediately
         */
        @Builder.Default
        private boolean activateAccount = true;

        /**
         * Steps to skip during registration
         */
        private List<String> skipSteps;

        /**
         * Additional notes for the registration
         */
        private String notes;

        /**
         * Whether to force complete all optional steps
         */
        @Builder.Default
        private boolean forceCompleteOptionalSteps = false;
    }
}