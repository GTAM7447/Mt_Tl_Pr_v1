package com.spring.jwt.CompleteProfile;

import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import lombok.Data;

@Data
public class CompleteProfileDTO {

    private Integer completeProfileId;

    private com.spring.jwt.dto.horoscope.HoroscopeResponse horoscopeDetails;
    private EducationAndProfessionResponse educationAndProfession;
    private FamilyBackgroundResponse familyBackground;
    private PartnerPreferenceResponse partnerPreference;
    private ContactDetailsResponse contactDetails;

    private boolean profileCompleted;
    private String statusCol;
    private String documentIds;
}
