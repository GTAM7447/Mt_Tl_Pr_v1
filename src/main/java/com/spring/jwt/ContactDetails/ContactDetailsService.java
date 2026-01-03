package com.spring.jwt.ContactDetails;

import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.ContactDetails.dto.ContactDetailsUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for contact details management.
 * Defines enterprise-grade operations with proper security and business logic.
 */
public interface ContactDetailsService {

    /**
     * Create contact details for the current authenticated user.
     *
     * @param request the create request
     * @return the created contact details response
     */
    ContactDetailsResponse createForCurrentUser(ContactDetailsCreateRequest request);

    /**
     * Create contact details for a specific user (Admin only).
     *
     * @param userId the target user ID
     * @param request the create request
     * @return the created contact details response
     */
    ContactDetailsResponse createForUser(Integer userId, ContactDetailsCreateRequest request);

    /**
     * Get contact details for the current authenticated user.
     *
     * @return the contact details response
     */
    ContactDetailsResponse getCurrentUserContactDetails();

    /**
     * Update contact details for the current authenticated user.
     *
     * @param request the update request
     * @return the updated contact details response
     */
    ContactDetailsResponse updateCurrentUserContactDetails(ContactDetailsUpdateRequest request);

    /**
     * Delete contact details for the current authenticated user (soft delete).
     */
    void deleteCurrentUserContactDetails();

    /**
     * Get contact details by user ID (Admin only).
     *
     * @param userId the user ID
     * @return the contact details response
     */
    ContactDetailsResponse getByUserId(Integer userId);

    /**
     * Get all contact details with pagination (Admin only).
     *
     * @param pageable pagination information
     * @return page of contact details
     */
    Page<ContactDetailsResponse> getAllContactDetails(Pageable pageable);

    /**
     * Search contact details by location criteria (Admin only).
     *
     * @param city the city name (optional)
     * @param state the state name (optional)
     * @param country the country name (optional)
     * @param pageable pagination information
     * @return page of contact details matching criteria
     */
    Page<ContactDetailsResponse> searchByLocation(String city, String state, String country, Pageable pageable);

    /**
     * Search contact details by verification status (Admin only).
     *
     * @param mobileVerified mobile verification status (optional)
     * @param emailVerified email verification status (optional)
     * @param pageable pagination information
     * @return page of contact details matching criteria
     */
    Page<ContactDetailsResponse> searchByVerificationStatus(Boolean mobileVerified, Boolean emailVerified, Pageable pageable);

    /**
     * Get contact details statistics (Admin only).
     *
     * @return statistics object
     */
    ContactDetailsStats getStatistics();

    /**
     * Verify mobile number for current user.
     *
     * @param verificationCode the verification code
     * @return true if verification successful
     */
    boolean verifyMobileNumber(String verificationCode);

    /**
     * Verify email address for current user.
     *
     * @param verificationCode the verification code
     * @return true if verification successful
     */
    boolean verifyEmailAddress(String verificationCode);

    /**
     * Send mobile verification code to current user.
     *
     * @return true if code sent successfully
     */
    boolean sendMobileVerificationCode();

    /**
     * Send email verification code to current user.
     *
     * @return true if code sent successfully
     */
    boolean sendEmailVerificationCode();

    /**
     * Statistics class for contact details.
     */
    class ContactDetailsStats {
        private long totalContacts;
        private long verifiedMobileCount;
        private long verifiedEmailCount;
        private long privateContactsCount;
        private long publicContactsCount;
        private long membersOnlyContactsCount;
        private String mostCommonCountry;
        private String mostCommonCity;
        private double mobileVerificationRate;
        private double emailVerificationRate;

        // Getters and setters
        public long getTotalContacts() { return totalContacts; }
        public void setTotalContacts(long totalContacts) { this.totalContacts = totalContacts; }

        public long getVerifiedMobileCount() { return verifiedMobileCount; }
        public void setVerifiedMobileCount(long verifiedMobileCount) { this.verifiedMobileCount = verifiedMobileCount; }

        public long getVerifiedEmailCount() { return verifiedEmailCount; }
        public void setVerifiedEmailCount(long verifiedEmailCount) { this.verifiedEmailCount = verifiedEmailCount; }

        public long getPrivateContactsCount() { return privateContactsCount; }
        public void setPrivateContactsCount(long privateContactsCount) { this.privateContactsCount = privateContactsCount; }

        public long getPublicContactsCount() { return publicContactsCount; }
        public void setPublicContactsCount(long publicContactsCount) { this.publicContactsCount = publicContactsCount; }

        public long getMembersOnlyContactsCount() { return membersOnlyContactsCount; }
        public void setMembersOnlyContactsCount(long membersOnlyContactsCount) { this.membersOnlyContactsCount = membersOnlyContactsCount; }

        public String getMostCommonCountry() { return mostCommonCountry; }
        public void setMostCommonCountry(String mostCommonCountry) { this.mostCommonCountry = mostCommonCountry; }

        public String getMostCommonCity() { return mostCommonCity; }
        public void setMostCommonCity(String mostCommonCity) { this.mostCommonCity = mostCommonCity; }

        public double getMobileVerificationRate() { return mobileVerificationRate; }
        public void setMobileVerificationRate(double mobileVerificationRate) { this.mobileVerificationRate = mobileVerificationRate; }

        public double getEmailVerificationRate() { return emailVerificationRate; }
        public void setEmailVerificationRate(double emailVerificationRate) { this.emailVerificationRate = emailVerificationRate; }
    }
}
