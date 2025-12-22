package com.spring.jwt.ContactDetails;

import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsUpdateRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Validation service for contact details business rules.
 * Provides comprehensive validation beyond basic field validation.
 */
@Service
@Slf4j
public class ContactDetailsValidationService {

    private static final Set<String> VALID_CONTACT_METHODS = Set.of(
        "Mobile", "WhatsApp", "Email", "Alternate Number"
    );

    private static final Set<String> VALID_VISIBILITY_SETTINGS = Set.of(
        "PRIVATE", "MEMBERS_ONLY", "PUBLIC"
    );

    private static final Set<String> VALID_EMERGENCY_RELATIONS = Set.of(
        "Father", "Mother", "Brother", "Sister", "Spouse", "Son", "Daughter",
        "Uncle", "Aunt", "Grandfather", "Grandmother", "Friend", "Colleague", "Other"
    );

    private static final Pattern INDIAN_MOBILE_PATTERN = Pattern.compile("^[+]?91[\\s-]?[6-9]\\d{9}$");
    private static final Pattern INDIAN_PINCODE_PATTERN = Pattern.compile("^[1-9]\\d{5}$");
    private static final Pattern INTERNATIONAL_MOBILE_PATTERN = Pattern.compile("^[+]?[1-9]\\d{1,14}$");

    /**
     * Validate create request with comprehensive business rules.
     *
     * @param request the create request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateCreateRequest(ContactDetailsCreateRequest request) {
        log.debug("Validating contact details create request");
        
        List<String> errors = new ArrayList<>();
        
        validateMobileNumbers(request.getMobileNumber(), request.getAlternateNumber(), 
                             request.getWhatsappNumber(), errors);
        validateAddressConsistency(request.getFullAddress(), request.getStreetAddress(), 
                                 request.getCity(), request.getState(), request.getCountry(), errors);
        validatePinCode(request.getPinCode(), request.getCountry(), errors);
        validateEmergencyContact(request.getEmergencyContactName(), request.getEmergencyContactNumber(), 
                               request.getEmergencyContactRelation(), errors);
        validateContactPreferences(request.getPreferredContactMethod(), request.getContactVisibility(), errors);
        validateEmailAddress(request.getEmailAddress(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Contact details create validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Contact details create request validation passed");
    }

    /**
     * Validate update request with comprehensive business rules.
     *
     * @param request the update request to validate
     * @throws IllegalArgumentException if validation fails
     */
    public void validateUpdateRequest(ContactDetailsUpdateRequest request) {
        log.debug("Validating contact details update request");
        
        List<String> errors = new ArrayList<>();

        if (!request.hasAnyFieldToUpdate()) {
            errors.add("At least one field must be provided for update");
        }
        
        validateMobileNumbers(request.getMobileNumber(), request.getAlternateNumber(), 
                             request.getWhatsappNumber(), errors);
        validateAddressConsistency(request.getFullAddress(), request.getStreetAddress(), 
                                 request.getCity(), request.getState(), request.getCountry(), errors);
        
        if (request.getPinCode() != null) {
            validatePinCode(request.getPinCode(), request.getCountry(), errors);
        }
        
        validateEmergencyContact(request.getEmergencyContactName(), request.getEmergencyContactNumber(), 
                               request.getEmergencyContactRelation(), errors);
        validateContactPreferences(request.getPreferredContactMethod(), request.getContactVisibility(), errors);
        validateEmailAddress(request.getEmailAddress(), errors);
        
        if (!errors.isEmpty()) {
            String errorMessage = "Validation failed: " + String.join(", ", errors);
            log.warn("Contact details update validation failed: {}", errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        log.debug("Contact details update request validation passed");
    }

    /**
     * Validate mobile numbers for uniqueness and format.
     */
    private void validateMobileNumbers(String mobileNumber, String alternateNumber, 
                                     String whatsappNumber, List<String> errors) {
        
        // Check for duplicate numbers
        if (mobileNumber != null && alternateNumber != null && 
            normalizePhoneNumber(mobileNumber).equals(normalizePhoneNumber(alternateNumber))) {
            errors.add("Mobile number and alternate number cannot be the same");
        }
        
        if (mobileNumber != null && whatsappNumber != null && 
            normalizePhoneNumber(mobileNumber).equals(normalizePhoneNumber(alternateNumber))) {
            errors.add("Mobile number and WhatsApp number cannot be the same");
        }

        if (mobileNumber != null && !isValidMobileNumber(mobileNumber)) {
            errors.add("Mobile number format is invalid");
        }
        
        if (alternateNumber != null && !isValidMobileNumber(alternateNumber)) {
            errors.add("Alternate number format is invalid");
        }
        
        if (whatsappNumber != null && !isValidMobileNumber(whatsappNumber)) {
            errors.add("WhatsApp number format is invalid");
        }
    }

    /**
     * Validate address consistency and completeness.
     */
    private void validateAddressConsistency(String fullAddress, String streetAddress, 
                                          String city, String state, String country, List<String> errors) {

        if (streetAddress != null && !streetAddress.trim().isEmpty() && 
            fullAddress != null && !fullAddress.toLowerCase().contains(streetAddress.toLowerCase())) {
            log.info("Street address not found in full address - this may be acceptable");
        }

        if ("India".equalsIgnoreCase(country) && (state == null || state.trim().isEmpty())) {
            errors.add("State is required for Indian addresses");
        }

        if (fullAddress != null && fullAddress.trim().length() < 10) {
            errors.add("Full address seems too short (minimum 10 characters)");
        }
    }

    /**
     * Validate PIN code based on country.
     */
    private void validatePinCode(String pinCode, String country, List<String> errors) {
        if (pinCode == null || pinCode.trim().isEmpty()) {
            return;
        }
        
        if ("India".equalsIgnoreCase(country)) {
            if (!INDIAN_PINCODE_PATTERN.matcher(pinCode.replaceAll("\\s", "")).matches()) {
                errors.add("Invalid Indian PIN code format (should be 6 digits, not starting with 0)");
            }
        } else {
            if (pinCode.length() < 3 || pinCode.length() > 20) {
                errors.add("PIN/ZIP code should be between 3 and 20 characters");
            }
        }
    }

    /**
     * Validate emergency contact information.
     */
    private void validateEmergencyContact(String name, String number, String relation, List<String> errors) {

        boolean hasEmergencyInfo = (name != null && !name.trim().isEmpty()) ||
                                  (number != null && !number.trim().isEmpty()) ||
                                  (relation != null && !relation.trim().isEmpty());
        
        if (hasEmergencyInfo) {
            if (name == null || name.trim().isEmpty()) {
                errors.add("Emergency contact name is required when emergency contact information is provided");
            }
            
            if (number == null || number.trim().isEmpty()) {
                errors.add("Emergency contact number is required when emergency contact information is provided");
            } else if (!isValidMobileNumber(number)) {
                errors.add("Emergency contact number format is invalid");
            }
            
            if (relation != null && !relation.trim().isEmpty() && 
                !VALID_EMERGENCY_RELATIONS.contains(relation)) {
                errors.add("Invalid emergency contact relation. Valid options: " + 
                          String.join(", ", VALID_EMERGENCY_RELATIONS));
            }
        }
    }

    /**
     * Validate contact preferences.
     */
    private void validateContactPreferences(String preferredMethod, String visibility, List<String> errors) {
        
        if (preferredMethod != null && !preferredMethod.trim().isEmpty() && 
            !VALID_CONTACT_METHODS.contains(preferredMethod)) {
            errors.add("Invalid preferred contact method. Valid options: " + 
                      String.join(", ", VALID_CONTACT_METHODS));
        }
        
        if (visibility != null && !visibility.trim().isEmpty() && 
            !VALID_VISIBILITY_SETTINGS.contains(visibility)) {
            errors.add("Invalid contact visibility setting. Valid options: " + 
                      String.join(", ", VALID_VISIBILITY_SETTINGS));
        }
    }

    /**
     * Validate email address format and domain.
     */
    private void validateEmailAddress(String email, List<String> errors) {
        if (email == null || email.trim().isEmpty()) {
            return;
        }

        if (email.length() > 100) {
            errors.add("Email address is too long (maximum 100 characters)");
        }

        String lowerEmail = email.toLowerCase();
        if (lowerEmail.contains("..") || lowerEmail.startsWith(".") || lowerEmail.endsWith(".")) {
            errors.add("Email address contains invalid dot placement");
        }

        if (lowerEmail.endsWith("@test.com") || lowerEmail.endsWith("@example.com") || lowerEmail.endsWith("xyz.com")) {
            log.warn("Suspicious email domain detected: {}", email);
        }
    }

    /**
     * Validate mobile number format.
     */
    private boolean isValidMobileNumber(String number) {
        if (number == null || number.trim().isEmpty()) {
            return false;
        }
        
        String cleanNumber = number.replaceAll("[\\s()-]", "");
        
        // Check Indian mobile number format
        if (INDIAN_MOBILE_PATTERN.matcher(cleanNumber).matches()) {
            return true;
        }
        
        // Check international mobile number format
        return INTERNATIONAL_MOBILE_PATTERN.matcher(cleanNumber).matches();
    }

    /**
     * Normalize phone number for comparison.
     */
    private String normalizePhoneNumber(String number) {
        if (number == null) {
            return "";
        }
        return number.replaceAll("[\\s()+-]", "").toLowerCase();
    }

    /**
     * Validate contact completeness for profile scoring.
     */
    public void validateContactCompleteness(ContactDetailsCreateRequest request) {
        List<String> missingOptionalFields = new ArrayList<>();
        
        if (request.getAlternateNumber() == null || request.getAlternateNumber().trim().isEmpty()) {
            missingOptionalFields.add("Alternate number");
        }
        
        if (request.getEmailAddress() == null || request.getEmailAddress().trim().isEmpty()) {
            missingOptionalFields.add("Email address");
        }
        
        if (request.getEmergencyContactName() == null || request.getEmergencyContactName().trim().isEmpty()) {
            missingOptionalFields.add("Emergency contact");
        }
        
        if (!missingOptionalFields.isEmpty()) {
            log.info("Optional contact fields missing (affects profile completeness): {}", 
                    String.join(", ", missingOptionalFields));
        }
    }
}