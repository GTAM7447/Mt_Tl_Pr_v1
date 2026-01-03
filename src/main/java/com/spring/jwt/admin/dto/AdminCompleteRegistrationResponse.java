package com.spring.jwt.admin.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Comprehensive response DTO for admin complete registration operation.
 * Provides detailed feedback about what was created and the overall profile status.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Complete registration response with all created entities and profile status")
public class AdminCompleteRegistrationResponse {
    
    @Schema(description = "Created user ID")
    private Integer userId;
    
    @Schema(description = "User email")
    private String email;
    
    @Schema(description = "User mobile number")
    private String mobileNumber;
    
    @Schema(description = "User gender")
    private String gender;
    
    @Schema(description = "Account activation status")
    private Boolean accountActive;
    
    @Schema(description = "Email verification status")
    private Boolean emailVerified;

    @Schema(description = "Created profile ID (if profile details were provided)")
    private Integer profileId;
    
    @Schema(description = "Created horoscope details ID (if horoscope details were provided)")
    private Long horoscopeDetailsId;
    
    @Schema(description = "Created education details ID (if education details were provided)")
    private Long educationDetailsId;
    
    @Schema(description = "Created family background ID (if family background was provided)")
    private Long familyBackgroundId;
    
    @Schema(description = "Created contact details ID (if contact details were provided)")
    private Long contactDetailsId;
    
    @Schema(description = "Created partner preferences ID (if partner preferences were provided)")
    private Long partnerPreferencesId;
    
    @Schema(description = "Complete profile ID (aggregate profile)")
    private Long completeProfileId;

    @Schema(description = "Profile completion percentage (0-100)")
    private Integer completionPercentage;
    
    @Schema(description = "Whether profile is considered complete")
    private Boolean profileComplete;
    
    @Schema(description = "Profile quality rating (EXCELLENT, GOOD, AVERAGE, POOR)")
    private String profileQuality;
    
    @Schema(description = "List of sections that were successfully created")
    private List<String> createdSections;
    
    @Schema(description = "List of sections that are still missing")
    private List<String> missingSections;

    @Schema(description = "Admin who created this registration")
    private String createdByAdmin;
    
    @Schema(description = "Registration timestamp")
    private LocalDateTime registrationTimestamp;
    
    @Schema(description = "Admin notes")
    private String adminNotes;
    
    @Schema(description = "Overall operation status message")
    private String message;
}
