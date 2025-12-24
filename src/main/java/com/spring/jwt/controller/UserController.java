package com.spring.jwt.controller;


import com.spring.jwt.dto.*;
import com.spring.jwt.jwt.JwtService;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.EncryptionUtil;
import com.spring.jwt.utils.ErrorResponseDto;
import com.spring.jwt.utils.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

///////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : UserController
//      Description  : Used to perform user operation
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////
@Tag(
        name = "User Management API",
        description = "APIs for user registration, profile management, and authentication operations"
)
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Validated
@Slf4j
@CrossOrigin(origins = "${app.cors.allowed-origins}", maxAge = 3600)
public class UserController {

    private final UserService userService;
    private final UserRepository userRepository;
    private final EncryptionUtil encryptionUtil;
    private final JwtService jwtService;

    @Value("${app.url.password-reset}")
    private String passwordResetUrl;

    ///////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : UserController
//      Description  : Used to create user
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////
    @Operation(
            summary = "Register a new user account",
            description = "Creates a new user account with the provided user details",
            tags = {"Authentication"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    description = "User account created successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input or account already exists",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponseDTO> registerUser(@Valid @RequestBody UserDTO userDTO) {
        BaseResponseDTO response = userService.registerAccount(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


////////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : UserController
//      Description  : Used to reset password
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////

    @Operation(
            summary = "Request password reset",
            description = "Sends a password reset link to the user's email address",
            tags = {"Authentication"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset email sent successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid email address",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/password/forgot")
    public ResponseEntity<ResponseDto> requestPasswordReset(
            @RequestParam @Email(message = "Invalid email format") @NotBlank(message = "Email is required") String email,
            HttpServletRequest request) {

        ResponseDto response = userService.handleForgotPassword(email, request.getServerName());
        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get password reset page",
            description = "Returns the HTML form for password reset",
            tags = {"Authentication"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset form returned successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid or expired token",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string")
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            mediaType = "text/plain",
                            schema = @Schema(type = "string")
                    )
            )
    }
    )

    @GetMapping("/password/reset")
    public ResponseEntity<String> getResetPasswordPage(
            @RequestParam @NotBlank(message = "Token is required") String token) {
        try {
            if (!userService.validateResetToken(token)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired token");
            }

            ClassPathResource resource = new ClassPathResource("templates/reset-password.html");
            String htmlContent = Files.readString(Paths.get(resource.getURI()), StandardCharsets.UTF_8);
            return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlContent);
        } catch (IOException e) {
            log.error("Error loading reset password template", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error loading password reset form");
        }
    }

    @Operation(
            summary = "Reset password",
            description = "Resets the user's password using the provided token and new password",
            tags = {"Authentication"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Password reset successful"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input, token expired, or password validation failed",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PostMapping("/password/reset")
    public ResponseEntity<ResponseDto> resetPassword(@Valid @RequestBody ResetPassword request) {
        ResponseDto response = userService.processPasswordUpdate(request);
        return ResponseEntity.ok(response);
    }


////////////////////////////////////////////////////////////////////////////////////
//
//      File Name    : UserController
//      Description  : Used to get all users
//      Author       : Ashutosh Shedge
//      Date         : 28/04/2025
//
//////////////////////////////////////////////////////////////////////////////////
    @Operation(
            summary = "Get all users",
            description = "Returns a paginated list of all users with optional filtering",
            tags = {"User Management"},
            security = {@SecurityRequirement(name = "bearer-jwt")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Users list retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Forbidden - Insufficient permissions",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("getAllUsers")
    public ResponseEntity<ResponseAllUsersDto> getAllUsers(
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be at least 1") int size) {

        Page<UserDTO> userPage = userService.getAllUsers(page, size);

        List<UserDTO> decryptedUsers = userPage.getContent().stream()
                .map(user -> {
//                try {
//                    if (user.getFirstName() != null) {
//                        user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
//                    }
//                    if (user.getLastName() != null) {
//                        user.setLastName(encryptionUtil.decrypt(user.getLastName()));
//                    }
//                    if (user.getAddress() != null) {
//                        user.setAddress(encryptionUtil.decrypt(user.getAddress()));
//                    }
//                } catch (Exception e) {
//                    log.error("Error decrypting user data: {}", e.getMessage());
//                }
                    return user;
                })
                .toList();

        ResponseAllUsersDto response = new ResponseAllUsersDto("success", decryptedUsers);
        response.setTotalPages(userPage.getTotalPages());
        response.setTotalElements(userPage.getTotalElements());
        response.setPageSize(userPage.getSize());
        response.setCurrentPage(userPage.getNumber());
        response.setFirst(userPage.isFirst());
        response.setLast(userPage.isLast());

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "Get user by ID",
            description = "Returns user details for the specified ID",
            tags = {"User Management"},
            security = {@SecurityRequirement(name = "bearer-jwt")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User details retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/{id}")
//    @PreAuthorize("hasAuthority('STUDENT')")
    public ResponseEntity<UserDTO> getUserById(
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer id) {
        UserDTO user = userService.getUserById(id);

//        try {
//            if (user.getFirstName() != null) {
//                user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
//            }
//            if (user.getLastName() != null) {
//                user.setLastName(encryptionUtil.decrypt(user.getLastName()));
//            }
//            if (user.getAddress() != null) {
//                user.setAddress(encryptionUtil.decrypt(user.getAddress()));
//            }

//        } catch (Exception e) {
//            log.error("Error decrypting user data: {}", e.getMessage());
//        }
//
        return ResponseEntity.ok(user);
    }

    @Operation(
            summary = "Get user profile by ID",
            description = "Returns detailed user profile with role-specific data",
            tags = {"User Management"},
            security = {@SecurityRequirement(name = "bearer-jwt")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/profile/{id}")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer id) {
        UserProfileDTO userProfileById = userService.getUserProfileById(id);

//        try {
//            UserDTO user = profile.getUser();
//            if (user != null) {
//                if (user.getFirstName() != null) {
//                    user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
//                }
//                if (user.getLastName() != null) {
//                    user.setLastName(encryptionUtil.decrypt(user.getLastName()));
//                }
//                if (user.getAddress() != null) {
//                    user.setAddress(encryptionUtil.decrypt(user.getAddress()));
//                }
//            }
//        } catch (Exception e) {
//            log.error("Error decrypting user data: {}", e.getMessage());
//        }

        return ResponseEntity.ok(userProfileById);
    }

    @Operation(
            summary = "Get current user's profile",
            description = "Returns the profile of the currently authenticated user",
            tags = {"User Management"},
            security = {@SecurityRequirement(name = "bearer-jwt")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User profile retrieved successfully"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @GetMapping("/profile/me")
    public ResponseEntity<UserProfileDTO> getCurrentUserProfile() {
        UserProfileDTO profile = userService.getCurrentUserProfile();

//        try {
//            UserDTO user = profile.getUser();
//            if (user != null) {
//                if (user.getFirstName() != null) {
//                    user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
//                }
//                if (user.getLastName() != null) {
//                    user.setLastName(encryptionUtil.decrypt(user.getLastName()));
//                }
//                if (user.getAddress() != null) {
//                    user.setAddress(encryptionUtil.decrypt(user.getAddress()));
//                }
//            }
//        } catch (Exception e) {
//            log.error("Error decrypting user data: {}", e.getMessage());
//        }

        return ResponseEntity.ok(profile);
    }

    @Operation(
            summary = "Update user details",
            description = "Updates the details of an existing user",
            tags = {"User Management"},
            security = {@SecurityRequirement(name = "bearer-jwt")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "User details updated successfully"
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Invalid input data",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    @PatchMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer id,
            @Valid @RequestBody UserUpdateRequest request) {
        UserDTO updatedUser = userService.updateUser(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
            summary = "Check device fingerprint",
            description = "Compares the current device fingerprint with the stored one for security verification",
            tags = {"Security"},
            security = {@SecurityRequirement(name = "bearer-jwt")}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "Device fingerprint check completed"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "Unauthorized",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "User not found",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "Internal server error",
                    content = @Content(
                            schema = @Schema(implementation = ErrorResponseDto.class)
                    )
            )
    })
    // @GetMapping("/security/device-fingerprint")
    public ResponseEntity<Map<String, Object>> checkDeviceFingerprint(HttpServletRequest request) {
        try {
//            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//            if (authentication == null || !authentication.isAuthenticated() ||
//                    authentication instanceof AnonymousAuthenticationToken) {
//                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                        .body(Map.of("error", "User not authenticated"));
//            }
//
//            String username = authentication.getName();
//            User user = userRepository.findByEmail(username);
//
//            if (user == null) {
//                return ResponseEntity.status(HttpStatus.NOT_FOUND)
//                        .body(Map.of("error", "User not found"));
//            }
//
//          //  String storedFingerprint = user.getDeviceFingerprint();
//            String currentFingerprint = jwtService.generateDeviceFingerprint(request);
//
//            Map<String, Object> result = new HashMap<>();
//            result.put("email", user.getEmail());
//            result.put("lastLogin", user.getLastLogin());
//
//            if (storedFingerprint != null) {
//                result.put("storedFingerprint", storedFingerprint.substring(0, 8) + "...");
//            } else {
//                result.put("storedFingerprint", null);
//            }
//
//            if (currentFingerprint != null) {
//                result.put("currentFingerprint", currentFingerprint.substring(0, 8) + "...");
//            } else {
//                result.put("currentFingerprint", null);
//            }
//
//            boolean fingerprintsMatch = storedFingerprint != null &&
//                    currentFingerprint != null &&
//                    storedFingerprint.equals(currentFingerprint);
//
//            result.put("fingerprintsMatch", fingerprintsMatch);
//
//            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error checking device fingerprint: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error checking device fingerprint: " + e.getMessage()));
        }
        return null;
    }

}


