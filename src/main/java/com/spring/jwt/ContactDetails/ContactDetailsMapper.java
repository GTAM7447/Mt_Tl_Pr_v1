package com.spring.jwt.ContactDetails;

import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.ContactDetails.dto.ContactDetailsUpdateRequest;
import com.spring.jwt.entity.ContactDetails;
import com.spring.jwt.entity.User;
import org.springframework.stereotype.Component;

import java.util.function.BiConsumer;

/**
 * Mapper for ContactDetails entity and DTOs.
 * Uses functional programming approach for clean, maintainable code.
 */
@Component
public class ContactDetailsMapper {

    /**
     * Convert entity to response DTO.
     *
     * @param entity the contact details entity
     * @return the response DTO
     */
    public ContactDetailsResponse toResponse(ContactDetails entity) {
        if (entity == null) {
            return null;
        }

        ContactDetailsResponse response = new ContactDetailsResponse();
        response.setContactDetailsId(entity.getContactDetailsId());
        response.setFullAddress(entity.getFullAddress());
        response.setStreetAddress(entity.getStreetAddress());
        response.setCity(entity.getCity());
        response.setState(entity.getState());
        response.setCountry(entity.getCountry());
        response.setPinCode(entity.getPinCode());
        response.setMobileNumber(entity.getMobileNumber());
        response.setAlternateNumber(entity.getAlternateNumber());
        response.setWhatsappNumber(entity.getWhatsappNumber());
        response.setEmailAddress(entity.getEmailAddress());
        response.setEmergencyContactName(entity.getEmergencyContactName());
        response.setEmergencyContactNumber(entity.getEmergencyContactNumber());
        response.setEmergencyContactRelation(entity.getEmergencyContactRelation());
        response.setPreferredContactMethod(entity.getPreferredContactMethod());
        response.setContactVisibility(entity.getContactVisibility());
        response.setIsVerifiedMobile(entity.getIsVerifiedMobile());
        response.setIsVerifiedEmail(entity.getIsVerifiedEmail());
        response.setVerificationAttempts(entity.getVerificationAttempts());
        response.setLastVerificationAttempt(entity.getLastVerificationAttempt());
        response.setUserId(entity.getUser() != null ? entity.getUser().getId() : null);
        response.setCreatedAt(entity.getCreatedAt());
        response.setUpdatedAt(entity.getUpdatedAt());
        response.setCreatedBy(entity.getCreatedBy());
        response.setUpdatedBy(entity.getUpdatedBy());
        response.setVersion(entity.getVersion());

        return response;
    }

    /**
     * Convert create request to entity.
     *
     * @param request the create request
     * @param user the user entity
     * @return the contact details entity
     */
    public ContactDetails toEntity(ContactDetailsCreateRequest request, User user) {
        if (request == null) {
            return null;
        }

        ContactDetails entity = new ContactDetails();
        entity.setFullAddress(request.getFullAddress());
        entity.setStreetAddress(request.getStreetAddress());
        entity.setCity(request.getCity());
        entity.setState(request.getState());
        entity.setCountry(request.getCountry());
        entity.setPinCode(request.getPinCode());
        entity.setMobileNumber(request.getMobileNumber());
        entity.setAlternateNumber(request.getAlternateNumber());
        entity.setWhatsappNumber(request.getWhatsappNumber());
        entity.setEmailAddress(request.getEmailAddress());
        entity.setEmergencyContactName(request.getEmergencyContactName());
        entity.setEmergencyContactNumber(request.getEmergencyContactNumber());
        entity.setEmergencyContactRelation(request.getEmergencyContactRelation());
        entity.setPreferredContactMethod(request.getPreferredContactMethod());
        entity.setContactVisibility(request.getContactVisibility() != null ? 
                                   request.getContactVisibility() : "PRIVATE");
        entity.setUser(user);

        return entity;
    }

    /**
     * Apply partial updates from update request to existing entity.
     * Uses functional programming approach to avoid repetitive if-else statements.
     *
     * @param request the update request
     * @param existing the existing entity
     */
    public void applyUpdate(ContactDetailsUpdateRequest request, ContactDetails existing) {
        if (request == null || existing == null) {
            return;
        }

        updateFieldIfNotNull(request.getFullAddress(), existing::setFullAddress);
        updateFieldIfNotNull(request.getStreetAddress(), existing::setStreetAddress);
        updateFieldIfNotNull(request.getCity(), existing::setCity);
        updateFieldIfNotNull(request.getState(), existing::setState);
        updateFieldIfNotNull(request.getCountry(), existing::setCountry);
        updateFieldIfNotNull(request.getPinCode(), existing::setPinCode);
        updateFieldIfNotNull(request.getMobileNumber(), existing::setMobileNumber);
        updateFieldIfNotNull(request.getAlternateNumber(), existing::setAlternateNumber);
        updateFieldIfNotNull(request.getWhatsappNumber(), existing::setWhatsappNumber);
        updateFieldIfNotNull(request.getEmailAddress(), existing::setEmailAddress);
        updateFieldIfNotNull(request.getEmergencyContactName(), existing::setEmergencyContactName);
        updateFieldIfNotNull(request.getEmergencyContactNumber(), existing::setEmergencyContactNumber);
        updateFieldIfNotNull(request.getEmergencyContactRelation(), existing::setEmergencyContactRelation);
        updateFieldIfNotNull(request.getPreferredContactMethod(), existing::setPreferredContactMethod);
        updateFieldIfNotNull(request.getContactVisibility(), existing::setContactVisibility);
    }

    /**
     * Utility method to update field only if value is not null.
     * Eliminates repetitive if-else statements.
     *
     * @param value the value to set
     * @param setter the setter method reference
     * @param <T> the type of the value
     */
    private <T> void updateFieldIfNotNull(T value, BiConsumer<ContactDetails, T> setter) {
        if (value != null) {
            setter.accept(null, value);
        }
    }

    /**
     * Overloaded utility method for simple setters.
     *
     * @param value the value to set
     * @param setter the setter consumer
     * @param <T> the type of the value
     */
    private <T> void updateFieldIfNotNull(T value, java.util.function.Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}