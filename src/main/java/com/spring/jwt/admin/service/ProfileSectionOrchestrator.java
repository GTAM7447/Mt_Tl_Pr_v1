package com.spring.jwt.admin.service;

import com.spring.jwt.ContactDetails.ContactDetailsService;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionService;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.FamilyBackground.FamilyBackgroundService;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.HoroscopeDetails.HoroscopeDetailsService;
import com.spring.jwt.PartnerPreference.PartnerPreferenceService;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationRequest;
import com.spring.jwt.admin.dto.AdminCompleteRegistrationResponse;
import com.spring.jwt.admin.mapper.AdminRequestMapper;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;
import com.spring.jwt.exception.BaseException;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileSectionOrchestrator {

    private final ProfileService profileService;
    private final HoroscopeDetailsService horoscopeDetailsService;
    private final EducationAndProfessionService educationAndProfessionService;
    private final FamilyBackgroundService familyBackgroundService;
    private final ContactDetailsService contactDetailsService;
    private final PartnerPreferenceService partnerPreferenceService;
    private final AdminRequestMapper mapper;

    public void createAllSections(Integer userId, AdminCompleteRegistrationRequest request,
                                   AdminCompleteRegistrationResponse response) {
        
        createSection(
                () -> request.getProfileDetails(),
                dto -> profileService.createProfileForUser(userId, mapper.toProfileRequest(dto)),
                resp -> response.setProfileId(resp.getUserProfileId()),
                "Profile Details",
                response
        );

        createSection(
                () -> request.getHoroscopeDetails(),
                dto -> horoscopeDetailsService.createForUser(userId, mapper.toHoroscopeRequest(dto)),
                resp -> response.setHoroscopeDetailsId(resp.getHoroscopeDetailsId().longValue()),
                "Horoscope Details",
                response
        );

        createSection(
                () -> request.getEducationDetails(),
                dto -> educationAndProfessionService.createForUser(userId, mapper.toEducationRequest(dto)),
                resp -> response.setEducationDetailsId(resp.getEducationId().longValue()),
                "Education & Profession",
                response
        );

        createSection(
                () -> request.getFamilyBackground(),
                dto -> familyBackgroundService.createForUser(userId, mapper.toFamilyBackgroundRequest(dto)),
                resp -> response.setFamilyBackgroundId(resp.getFamilyBackgroundId().longValue()),
                "Family Background",
                response
        );

        createSection(
                () -> request.getContactDetails(),
                dto -> contactDetailsService.createForUser(userId, mapper.toContactDetailsRequest(dto)),
                resp -> response.setContactDetailsId(resp.getContactDetailsId().longValue()),
                "Contact Details",
                response
        );

        createSection(
                () -> request.getPartnerPreferences(),
                dto -> partnerPreferenceService.createForUser(userId, mapper.toPartnerPreferencesRequest(dto)),
                resp -> response.setPartnerPreferencesId(resp.getPartnerPreferenceId().longValue()),
                "Partner Preferences",
                response
        );
    }

    private <T, R> void createSection(Supplier<T> requestSupplier,
                                      Function<T, R> serviceCall,
                                      Consumer<R> responseUpdater,
                                      String sectionName,
                                      AdminCompleteRegistrationResponse response) {
        Optional.ofNullable(requestSupplier.get())
                .ifPresent(dto -> {
                    try {
                        R result = serviceCall.apply(dto);
                        responseUpdater.accept(result);
                        response.getCreatedSections().add(sectionName);
                        log.info("{} created for user", sectionName);
                    } catch (IllegalArgumentException e) {
                        log.error("Validation error creating {}: {}", sectionName, e.getMessage());
                        throw new BaseException(
                                String.valueOf(HttpStatus.BAD_REQUEST.value()),
                                sectionName + " validation failed: " + e.getMessage()
                        );
                    } catch (Exception e) {
                        log.error("Failed to create {}", sectionName, e);
                        throw new BaseException(
                                String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value()),
                                "Failed to create " + sectionName + ": " + e.getMessage()
                        );
                    }
                });
    }
}
