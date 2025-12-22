package com.spring.jwt.admin.service;

import com.spring.jwt.admin.dto.*;
import com.spring.jwt.dto.ResponseDto;

import java.util.List;
import java.util.Map;

/**
 * Service interface for admin workflow operations
 * Handles complete user registration workflow and step management
 */
public interface AdminWorkflowService {

    @Deprecated
    Map<String, Object> completeUserRegistration(CompleteUserRegistrationRequest request);

    ResponseDto executeUserRegistrationStep(UserRegistrationStepRequest request);

    ResponseDto executeProfileDetailsStep(Integer userId, ProfileDetailsStepRequest request);

    ResponseDto executeHoroscopeDetailsStep(Integer userId, HoroscopeDetailsStepRequest request);

    ResponseDto executeEducationProfessionStep(Integer userId, EducationProfessionStepRequest request);

    ResponseDto executeFamilyBackgroundStep(Integer userId, FamilyBackgroundStepRequest request);

    ResponseDto executePartnerPreferencesStep(Integer userId, PartnerPreferencesStepRequest request);

    ResponseDto executeContactDetailsStep(Integer userId, ContactDetailsStepRequest request);

    List<WorkflowStepResponse> getWorkflowSteps(Integer userId);

    WorkflowStepResponse executeWorkflowStep(Integer userId, String stepName, Map<String, Object> stepData);

    ResponseDto skipWorkflowStep(Integer userId, String stepName, String reason);

    ResponseDto resetWorkflowStep(Integer userId, String stepName);

    Map<String, Object> getWorkflowProgress(Integer userId);

    Map<String, Object> getWorkflowAnalytics(String startDate, String endDate, String groupBy);

    Map<String, Object> getUsersByWorkflowStatus(String status, String currentStep, int page, int size);

    Map<String, Object> bulkWorkflowOperation(List<Integer> userIds, String operation, String stepName, String reason);
}