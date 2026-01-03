package com.spring.jwt.admin.service;

import com.spring.jwt.admin.dto.AdminCompleteRegistrationRequest;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationResponse;
import com.spring.jwt.entity.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminRegistrationOrchestrationServiceImpl implements AdminRegistrationOrchestrationService {

    private final UserAccountCreationService userAccountCreationService;
    private final ProfileSectionOrchestrator profileSectionOrchestrator;
    private final ProfileCompletionCalculator profileCompletionCalculator;
    private final AdminContextService adminContextService;

    @Override
    @Transactional
    public AdminCompleteRegistrationResponse registerCompleteUser(AdminCompleteRegistrationRequest request) {
        log.info("Starting complete user registration for email: {}", request.getEmail());
        
        String adminEmail = adminContextService.getCurrentAdminEmail();
        log.info("Registration initiated by admin: {}", adminEmail);

        User user = userAccountCreationService.createUserAccount(request);
        
        AdminCompleteRegistrationResponse response = buildInitialResponse(user, request, adminEmail);
        
        profileSectionOrchestrator.createAllSections(user.getId(), request, response);
        
        profileCompletionCalculator.calculateAndUpdate(user.getId(), response);
        
        response.setMessage(buildCompletionMessage(response));

        log.info("Complete user registration finished for user ID: {} with {}% completion",
                user.getId(), response.getCompletionPercentage());

        return response;
    }

    private AdminCompleteRegistrationResponse buildInitialResponse(User user,
                                                                    AdminCompleteRegistrationRequest request,
                                                                    String adminEmail) {
        return AdminCompleteRegistrationResponse.builder()
                .userId(user.getId())
                .email(user.getEmail())
                .mobileNumber(String.valueOf(user.getMobileNumber()))
                .gender(user.getGender().name())
                .accountActive(true)
                .emailVerified(request.getSkipEmailVerification())
                .createdByAdmin(adminEmail)
                .registrationTimestamp(LocalDateTime.now())
                .adminNotes(request.getAdminNotes())
                .createdSections(new ArrayList<>())
                .missingSections(new ArrayList<>())
                .build();
    }

    private String buildCompletionMessage(AdminCompleteRegistrationResponse response) {
        int sectionsCreated = response.getCreatedSections().size();
        return sectionsCreated == 0
                ? "User registration completed successfully. No profile sections created."
                : String.format("User registration completed successfully with %d profile section%s",
                        sectionsCreated, sectionsCreated == 1 ? "" : "s");
    }
}
