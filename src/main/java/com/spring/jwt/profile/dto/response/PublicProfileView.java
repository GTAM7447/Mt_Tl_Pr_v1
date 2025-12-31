package com.spring.jwt.profile.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Public profile view with limited information.
 * Used for unauthenticated users or listing views.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicProfileView {

    @Schema(description = "User profile ID", example = "1")
    private Integer userProfileId;
    
    @Schema(description = "Complete profile ID", example = "1")
    private Integer completeProfileId;
    
    @Schema(description = "First name", example = "John")
    private String firstName;
    
    @Schema(description = "Age", example = "28")
    private Integer age;
    
    @Schema(description = "Gender", example = "MALE")
    private String gender;
    
    @Schema(description = "Religion", example = "Hindu")
    private String religion;
    
    @Schema(description = "Caste", example = "Brahmin")
    private String caste;
    
    @Schema(description = "Height in feet", example = "5.8")
    private Double height;
    
    @Schema(description = "Complexion", example = "Fair")
    private String complexion;
    
    @Schema(description = "Current city", example = "Mumbai")
    private String currentCity;
    
    @Schema(description = "Marital status", example = "Single")
    private String maritalStatus;
    
    // Profile photo fields
    @Schema(description = "Profile photo as base64 encoded string", example = "data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAAAQ...")
    private String profilePhotoBase64;
    
    @Schema(description = "Profile photo content type", example = "image/jpeg")
    private String profilePhotoContentType;
    
    @Schema(description = "Whether user has uploaded a profile photo", example = "true")
    private Boolean hasProfilePhoto;
}
