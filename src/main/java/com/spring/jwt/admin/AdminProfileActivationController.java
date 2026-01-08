package com.spring.jwt.admin;

import com.spring.jwt.entity.Enums.Status;
import com.spring.jwt.profile.ProfileActivationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/admin/profile-activation")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Admin Profile Activation", description = "Admin operations for activating/deactivating user profiles")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminProfileActivationController {

    private final ProfileActivationService profileActivationService;

    @PostMapping("/activate/{userId}")
    @Operation(summary = "Activate user profile", 
               description = "Activate a user profile to make it visible in search results and allow interactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile activated successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Map<String, Object>> activateProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.info("Admin activating profile for user: {}", userId);
        
        profileActivationService.activateProfile(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile activated successfully");
        response.put("userId", userId);
        response.put("status", Status.ACTIVE);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/deactivate/{userId}")
    @Operation(summary = "Deactivate user profile", 
               description = "Deactivate a user profile to hide it from search results and prevent interactions")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profile deactivated successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Map<String, Object>> deactivateProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.info("Admin deactivating profile for user: {}", userId);
        
        profileActivationService.deactivateProfile(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile deactivated successfully");
        response.put("userId", userId);
        response.put("status", Status.DEACTIVE);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/status/{userId}")
    @Operation(summary = "Get profile activation status", 
               description = "Check if a user profile is active or inactive")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Map<String, Object>> getProfileStatus(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        Status status = profileActivationService.getProfileStatus(userId);
        boolean isActive = status == Status.ACTIVE;
        
        Map<String, Object> response = new HashMap<>();
        response.put("userId", userId);
        response.put("status", status);
        response.put("isActive", isActive);
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/toggle/{userId}")
    @Operation(summary = "Toggle profile activation status", 
               description = "Toggle profile between ACTIVE and DEACTIVE status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status toggled successfully"),
        @ApiResponse(responseCode = "404", description = "Profile not found"),
        @ApiResponse(responseCode = "403", description = "Access denied")
    })
    public ResponseEntity<Map<String, Object>> toggleProfileStatus(
            @Parameter(description = "User ID", required = true)
            @PathVariable Integer userId) {
        
        log.info("Admin toggling profile status for user: {}", userId);
        
        Status currentStatus = profileActivationService.getProfileStatus(userId);
        
        if (currentStatus == Status.ACTIVE) {
            profileActivationService.deactivateProfile(userId);
        } else {
            profileActivationService.activateProfile(userId);
        }
        
        Status newStatus = profileActivationService.getProfileStatus(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Profile status toggled successfully");
        response.put("userId", userId);
        response.put("previousStatus", currentStatus);
        response.put("currentStatus", newStatus);
        
        return ResponseEntity.ok(response);
    }
}
