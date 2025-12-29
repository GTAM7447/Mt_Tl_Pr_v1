package com.spring.jwt.service.impl;

import com.spring.jwt.CompleteProfile.CompleteProfileRepository;
import com.spring.jwt.ContactDetails.ContactDetailsRepository;
import com.spring.jwt.Document.DocumentRepository;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionRepository;
import com.spring.jwt.FamilyBackground.FamilyBackgroundRepository;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsRepository;
import com.spring.jwt.PartnerPreference.PartnerPreferenceRepository;
import com.spring.jwt.dto.*;
import com.spring.jwt.entity.*;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.exception.UserNotFoundExceptions;
import com.spring.jwt.repository.RoleRepository;
import com.spring.jwt.repository.UserProfileRepository;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.service.UserService;
import com.spring.jwt.utils.BaseResponseDTO;
import com.spring.jwt.utils.EmailService;
import com.spring.jwt.utils.EmailVerificationService.EmailVerification;
import com.spring.jwt.utils.EmailVerificationService.EmailVerificationRepo;
import com.spring.jwt.utils.ResponseDto;
import com.spring.jwt.utils.DataMaskingUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import com.spring.jwt.mapper.UserMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final ContactDetailsRepository contactRepository;

    private final DocumentRepository documentRepository;

    private final EducationAndProfessionRepository educationAndProfessionRepository;

    private final FamilyBackgroundRepository familyBackgroundRepository;

    private final HoroscopeDetailsRepository horoscopeRepository;

    private final PartnerPreferenceRepository partnerPreferenceRepository;

    private final CompleteProfileRepository completeProfileRepository;

    private final UserProfileRepository userProfileRepository;

    private final EmailVerificationRepo emailVerificationRepo;

    private final RoleRepository roleRepository;

    private final BCryptPasswordEncoder passwordEncoder;

    private final EmailService emailService;

    private final UserMapper userMapper;

    @Value("${app.url.password-reset}")
    private String passwordResetUrl;

    @Override
    @Transactional
    public BaseResponseDTO registerAccount(UserDTO userDTO) {
        BaseResponseDTO response = new BaseResponseDTO();

        validateAccount(userDTO);

        User user = insertUser(userDTO);

        try {
            userRepository.save(user);
            response.setCode(String.valueOf(HttpStatus.OK.value()));
            response.setMessage("Account Created Successfully !!");
            response.setUserID(user.getId());
        } catch (Exception e) {
            log.error("Error creating account", e);
            response.setCode(String.valueOf(HttpStatus.SERVICE_UNAVAILABLE.value()));
            response.setMessage("Service unavailable");
        }
        return response;
    }

    @Transactional
    private User insertUser(UserDTO userDTO) {
        Optional<EmailVerification> emailVerificationOpt = emailVerificationRepo.findByEmail(userDTO.getEmail());

        // if (emailVerificationOpt.isEmpty() ||
        // EmailVerification.STATUS_NOT_VERIFIED.equals(emailVerificationOpt.get().getStatus()))
        // {
        // throw new EmailNotVerifiedException("Email not verified");
        // }

        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setPassword(passwordEncoder.encode(userDTO.getPassword()));
        user.setMobileNumber(userDTO.getMobileNumber());
        user.setGender(userDTO.getGender());
        user.setEmailVerified(true);

        Set<Role> roles = new HashSet<>();

        Role role = roleRepository.findByName(userDTO.getRole());

        if (role != null) {
            roles.add(role);
        } else {
            Role defaultRole = roleRepository.findByName("USER");
            if (defaultRole != null) {
                roles.add(defaultRole);
            }
        }

        user.setRoles(roles);

        user = userRepository.save(user);

        CompleteProfile completeProfile = new CompleteProfile();
        completeProfile.setUser(user);
        completeProfileRepository.save(completeProfile);

        return user;
    }

    private void createAdminProfile(User user, UserDTO userDTO) {
    }

    private void createUserProfile(User user, UserDTO userDTO) {
        UserProfile userProfile = new UserProfile();
        userProfile.setGender(userDTO.getGender());
        // userProfile.setMail(userDTO.getEmail());
        // userProfile.setMobileNumber(userDTO.getMobileNumber());
        userProfile.setUser(user);
        userProfileRepository.save(userProfile);

        ContactDetails contactDetails = new ContactDetails();
        contactDetails.setUser(user);
        contactRepository.save(contactDetails);

        Document document = new Document();
        document.setUser(user);
        documentRepository.save(document);

        EducationAndProfession educationAndProfession = new EducationAndProfession();
        educationAndProfession.setUser(user);
        educationAndProfessionRepository.save(educationAndProfession);

        FamilyBackground familyBackground = new FamilyBackground();
        familyBackground.setUser(user);
        familyBackgroundRepository.save(familyBackground);

        HoroscopeDetails horoscopeDetails = new HoroscopeDetails();
        horoscopeDetails.setUser(user);
        horoscopeRepository.save(horoscopeDetails);

        PartnerPreference partnerPreference = new PartnerPreference();
        partnerPreference.setUser(user);
        partnerPreferenceRepository.save(partnerPreference);

        CompleteProfile completeProfile = new CompleteProfile();
        completeProfile.setUser(user);
        completeProfileRepository.save(completeProfile);

        log.info("Created  profile for user ID: {}", user.getId());
    }

    private void validateAccount(UserDTO userDTO) {
        if (ObjectUtils.isEmpty(userDTO)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Data must not be empty");
        }

        User user = userRepository.findByEmail(userDTO.getEmail());
        if (!ObjectUtils.isEmpty(user)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Email is already registered !!");
        }

        List<String> roles = roleRepository.findAll().stream().map(Role::getName).toList();
        if (!roles.contains(userDTO.getRole())) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()), "Invalid role");
        }
        Optional<User> mobileNumber = userRepository.findByMobileNumber(userDTO.getMobileNumber());
        if (!ObjectUtils.isEmpty(mobileNumber)) {
            throw new BaseException(String.valueOf(HttpStatus.BAD_REQUEST.value()),
                    "Mobile Number is already registered !!");
        }
    }

    @Override
    public ResponseDto forgotPass(String email, String resetPasswordLink, String domain) {
        User user = userRepository.findByEmail(email);
        if (user == null)
            throw new UserNotFoundExceptions("User not found");

        emailService.sendResetPasswordEmail(email, resetPasswordLink, domain);

        return new ResponseDto(HttpStatus.OK.toString(), "Email sent");
    }

    @Override
    @Transactional
    public ResponseDto handleForgotPassword(String email, String domain) {
        if (email == null || email.isEmpty()) {
            log.warn("Forgot password attempt with empty email");
            return new ResponseDto("Unsuccessful", "Email is required");
        }

        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.warn("Forgot password attempt for non-existent email: {}",
                    DataMaskingUtils.maskEmail(email));
            throw new UserNotFoundExceptions("User not found with email: " + email);
        }

        String token = RandomStringUtils.randomAlphanumeric(64);
        log.debug("Generated password reset token for user: {}",
                DataMaskingUtils.maskEmail(email));

        updateResetPassword(token, email);

        String resetPasswordLink = passwordResetUrl + "?token=" + token;
        try {
            emailService.sendResetPasswordEmail(email, resetPasswordLink, domain);
            log.info("Password reset email sent to: {}", DataMaskingUtils.maskEmail(email));
            return new ResponseDto("Successful", "Password reset instructions sent to your email");
        } catch (Exception e) {
            log.error("Failed to send password reset email to: {}", DataMaskingUtils.maskEmail(email), e);
            return new ResponseDto("Unsuccessful", "Failed to send reset instructions. Please try again later.");
        }
    }

    @Override
    @Transactional
    public void updateResetPassword(String token, String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            log.warn("Attempt to update reset password for non-existent email: {}", email);
            throw new UserNotFoundExceptions("User not found with email: " + email);
        }

        user.setResetPasswordToken(token);
        user.setResetPasswordTokenExpiry(LocalDateTime.now().plusMinutes(30));
        userRepository.save(user);
        log.debug("Reset password token updated for user: {}", email);
    }

    @Override
    @Transactional
    public ResponseDto updatePassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null || user.getResetPasswordTokenExpiry() == null ||
                LocalDateTime.now().isAfter(user.getResetPasswordTokenExpiry())) {
            log.warn("Invalid or expired reset token used: {}", token);
            throw new UserNotFoundExceptions("Invalid or expired token");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiry(null);
        userRepository.save(user);
        log.info("Password successfully reset for user: {}", user.getEmail());

        return new ResponseDto(HttpStatus.OK.toString(), "Password reset successful");
    }

    @Override
    @Transactional
    public ResponseDto processPasswordUpdate(ResetPassword request) {

        if (request.getPassword() == null || request.getConfirmPassword() == null || request.getToken() == null) {
            log.warn("Missing required fields in password reset request");
            return new ResponseDto("Unsuccessful", "Missing required fields");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            log.warn("Password mismatch in reset request");
            return new ResponseDto("Unsuccessful", "Passwords do not match");
        }

        if (!validateResetToken(request.getToken())) {
            log.warn("Invalid token used in password reset: {}", request.getToken());
            return new ResponseDto("Unsuccessful", "Invalid or expired token");
        }

        if (isSameAsOldPassword(request.getToken(), request.getPassword())) {
            log.warn("New password same as old password in reset request");
            return new ResponseDto("Unsuccessful", "New password cannot be the same as the old password");
        }

        try {
            ResponseDto response = updatePassword(request.getToken(), request.getPassword());
            return new ResponseDto("Successful", response.getMessage());
        } catch (Exception e) {
            log.error("Error during password update", e);
            return new ResponseDto("Unsuccessful", "An error occurred during password reset");
        }
    }

    @Override
    public boolean validateResetToken(String token) {
        if (token == null || token.isEmpty()) {
            return false;
        }

        User user = userRepository.findByResetPasswordToken(token);
        if (user == null) {
            log.debug("Reset token not found: {}", token);
            return false;
        }

        boolean isValid = user.getResetPasswordTokenExpiry() != null &&
                LocalDateTime.now().isBefore(user.getResetPasswordTokenExpiry());

        if (!isValid) {
            log.debug("Expired reset token used: {}", token);
        }

        return isValid;
    }

    @Override
    public boolean isSameAsOldPassword(String token, String newPassword) {
        User user = userRepository.findByResetPasswordToken(token);
        if (user == null)
            throw new UserNotFoundExceptions("Invalid or expired token");

        return passwordEncoder.matches(newPassword, user.getPassword());
    }

    @Override
    public Page<UserDTO> getAllUsers(int pageNo, int pageSize) {
        Pageable pageable = PageRequest.of(pageNo, pageSize);
        Page<User> users = userRepository.findAll(pageable);

        return users.map(user -> {
            UserDTO userDTO = userMapper.toDTO(user);
            return populateRoleSpecificData(user, userDTO);
        });
    }

    @Override
    public UserDTO getUserById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));

        UserDTO userDTO = userMapper.toDTO(user);

        return populateRoleSpecificData(user, userDTO);
    }

    private UserDTO populateRoleSpecificData(User user, UserDTO userDTO) {

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        userDTO.setRoles(roles);

        Integer userId = user.getId().intValue();

        return userDTO;
    }

    @Override
    public UserDTO updateUser(Integer id, UserUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));

        if (request.getMobileNumber() != null) {
            user.setMobileNumber(request.getMobileNumber());
        }

        User updatedUser = userRepository.save(user);
        return userMapper.toDTO(updatedUser);
    }

    @Override
    public UserProfileDTO getUserProfileById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));

        return buildUserProfileDTO(user);
    }

    @Override
    public UserProfileDTO getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BaseException(String.valueOf(HttpStatus.UNAUTHORIZED.value()), "User not authenticated");
        }

        String email = authentication.getName();
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UserNotFoundExceptions("User not found with email: " + email);
        }

        return buildUserProfileDTO(user);
    }

    private UserProfileDTO buildUserProfileDTO(User user) {
        UserProfileDTO profileDTO = new UserProfileDTO();

        profileDTO.setUser(userMapper.toDTO(user));

        Set<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toSet());
        profileDTO.setRoles(roles);

        Integer userId = user.getId().intValue();

        return profileDTO;
    }
}