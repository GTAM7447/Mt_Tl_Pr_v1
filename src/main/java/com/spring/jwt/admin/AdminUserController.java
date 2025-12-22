package com.spring.jwt.admin;

import com.spring.jwt.dto.UserDTO;
import com.spring.jwt.dto.UserProfileDTO;
import com.spring.jwt.dto.UserUpdateRequest;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.dto.ResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Admin User Management", description = "Admin operations for user registration and management")
@SecurityRequirement(name = "bearerAuth")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @Operation(
        summary = "Register new user (Admin)",
        description = "Admin can register a new user without authentication restrictions"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User registered successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "409", description = "User already exists"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/register")
    public ResponseEntity<BaseResponseDTO> registerUser(
            @Valid @RequestBody UserDTO userDTO) {
        log.info("Admin registering new user with email: {}", userDTO.getEmail());
        BaseResponseDTO response = userService.registerAccount(userDTO);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get all users (Admin)",
        description = "Admin can retrieve all users with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/all")
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") @Min(value = 0, message = "Page number cannot be negative") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Page size must be positive") int size) {
        
        log.info("Admin retrieving all users - page: {}, size: {}", page, size);
        Page<UserDTO> response = userService.getAllUsers(page, size);
        return ResponseEntity.ok(response);
    }

    @Operation(
        summary = "Get user by ID (Admin)",
        description = "Admin can retrieve any user by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving user by ID: {}", userId);
        UserDTO user = userService.getUserById(userId);
        return ResponseEntity.ok(user);
    }

    @Operation(
        summary = "Get user profile (Admin)",
        description = "Admin can retrieve any user's profile"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User profile retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @GetMapping("/{userId}/profile")
    public ResponseEntity<UserProfileDTO> getUserProfile(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId) {
        
        log.info("Admin retrieving user profile for ID: {}", userId);
        UserProfileDTO profile = userService.getUserProfileById(userId);
        return ResponseEntity.ok(profile);
    }

    @Operation(
        summary = "Update user (Admin)",
        description = "Admin can update any user's basic information"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PatchMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Valid @RequestBody UserUpdateRequest userUpdateRequest) {
        
        log.info("Admin updating user ID: {}", userId);
        UserDTO updatedUser = userService.updateUser(userId, userUpdateRequest);
        return ResponseEntity.ok(updatedUser);
    }

    @Operation(
        summary = "Reset user password (Admin)",
        description = "Admin can reset any user's password"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Password reset successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "403", description = "Access denied - Admin role required")
    })
    @PostMapping("/{userId}/password/reset")
    public ResponseEntity<ResponseDto<String>> resetUserPassword(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "New password", required = true)
            @RequestParam @NotBlank(message = "Password is required") String newPassword) {
        
        log.info("Admin resetting password for user ID: {}", userId);
        ResponseDto<String> response = ResponseDto.success("Password reset not supported", 
            "Admin password reset functionality not available in current service");
        return ResponseEntity.status(501).body(response);
    }

    @PatchMapping("/{userId}/account-status")
    public ResponseEntity<ResponseDto<String>> updateAccountStatus(
            @Parameter(description = "User ID", required = true)
            @PathVariable @Min(value = 1, message = "Invalid user ID") Integer userId,
            @Parameter(description = "Lock account (true/false)", required = true)
            @RequestParam boolean locked) {
        
        log.info("Admin {} user account for ID: {}", locked ? "locking" : "unlocking", userId);
        ResponseDto<String> response = ResponseDto.success("Account status update not supported",
            "Admin account status update functionality not available in current service");
        return ResponseEntity.status(501).body(response);
    }
}