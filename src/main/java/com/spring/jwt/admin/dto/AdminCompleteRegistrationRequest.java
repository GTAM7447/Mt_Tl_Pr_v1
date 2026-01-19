package com.spring.jwt.admin.dto;

import com.spring.jwt.admin.validation.AdminValidation;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Comprehensive request DTO for admin-driven user registration.
 * Allows admins to register a user with all profile sections in a single
 * operation.
 * All nested sections are optional - admin can provide partial data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Complete user registration request for admin operations")
public class AdminCompleteRegistrationRequest {

    @Schema(description = "User email address", example = "user@example.com", required = true)
    @NotBlank(message = "Email is required", groups = AdminValidation.class)
    @Email(message = "Invalid email format", groups = AdminValidation.class)
    private String email;

    @Schema(description = "User password (must contain uppercase, lowercase, digit, and special character)", example = "SecurePass123!", required = true)
    @NotBlank(message = "Password is required", groups = AdminValidation.class)
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters", groups = AdminValidation.class)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character (@$!%*?&)", groups = AdminValidation.class)
    private String password;

    @Schema(description = "User mobile number (Indian format starting with 6-9)", example = "9876543210", required = true)
    @NotBlank(message = "Mobile number is required", groups = AdminValidation.class)
    @Pattern(regexp = "^[6-9][0-9]{9}$", message = "Mobile number must be 10 digits starting with 6, 7, 8, or 9", groups = AdminValidation.class)
    private String mobileNumber;

    @Schema(description = "User gender", example = "MALE", required = true)
    @NotBlank(message = "Gender is required", groups = AdminValidation.class)
    @Pattern(regexp = "MALE|FEMALE|OTHER", message = "Gender must be MALE, FEMALE, or OTHER", groups = AdminValidation.class)
    private String gender;

    @Schema(description = "Basic profile details (optional)")
    @Valid
    private ProfileDetailsStepRequest profileDetails;

    @Schema(description = "Horoscope details (optional)")
    @Valid
    private HoroscopeDetailsStepRequest horoscopeDetails;

    @Schema(description = "Education and profession details (optional)")
    @Valid
    private EducationProfessionStepRequest educationDetails;

    @Schema(description = "Family background details (optional)")
    @Valid
    private FamilyBackgroundStepRequest familyBackground;

    @Schema(description = "Contact details (optional)")
    @Valid
    private ContactDetailsStepRequest contactDetails;

    @Schema(description = "Partner preferences (optional)")
    @Valid
    private PartnerPreferencesStepRequest partnerPreferences;

    @Schema(description = "Admin notes/comments about this registration", example = "Registered during offline event")
    @Size(max = 1000, message = "Admin notes cannot exceed 1000 characters", groups = AdminValidation.class)
    private String adminNotes;

    @Schema(description = "Skip email verification", example = "true")
    @Builder.Default
    private Boolean skipEmailVerification = false;

    @Schema(description = "Auto-activate account", example = "true")
    @Builder.Default
    private Boolean autoActivate = true;

    @Schema(description = "Gender specification (required if gender is OTHER)", example = "Non-binary")
    @Size(max = 50, message = "Gender specification cannot exceed 50 characters", groups = AdminValidation.class)
    private String genderSpecification;
}
