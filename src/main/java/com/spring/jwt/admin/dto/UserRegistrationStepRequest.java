package com.spring.jwt.admin.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Step 1: User Registration Request
 * Contains only the essential fields needed to create a user account
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationStepRequest {

    @NotBlank(message = "Email cannot be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password cannot be blank")
    @Size(min = 8, max = 50, message = "Password must be between 8 and 50 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter, one digit, and one special character")
    private String password;

    @NotBlank(message = "First name cannot be blank")
    @Size(max = 50, message = "First name cannot exceed 50 characters")
    private String firstName;

    @NotBlank(message = "Last name cannot be blank")
    @Size(max = 50, message = "Last name cannot exceed 50 characters")
    private String lastName;

    @NotNull(message = "Mobile number is required")
    private Long mobileNumber;

    @NotBlank(message = "Address cannot be blank")
    @Size(max = 500, message = "Address cannot exceed 500 characters")
    private String address;

    /**
     * Admin-specific options for user creation
     */
    private AdminUserOptions adminOptions;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AdminUserOptions {
        
        /**
         * Whether to skip email verification
         */
        @Builder.Default
        private boolean skipEmailVerification = true;

        /**
         * Whether to activate account immediately
         */
        @Builder.Default
        private boolean activateAccount = true;

        /**
         * Whether to send welcome email
         */
        @Builder.Default
        private boolean sendWelcomeEmail = false;

        /**
         * Admin notes for this user creation
         */
        private String adminNotes;
    }
}