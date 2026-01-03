package com.spring.jwt.admin.mapper;

import com.spring.jwt.ContactDetails.dto.ContactDetailsCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.admin.dto.*;
import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import org.springframework.stereotype.Component;

import java.sql.Date;

@Component
public class AdminRequestMapper {

    public CreateProfileRequest toProfileRequest(ProfileDetailsStepRequest source) {
        CreateProfileRequest target = new CreateProfileRequest();
        target.setFirstName(source.getFirstName());
        target.setLastName(source.getLastName());
        target.setAge(source.getAge());
        target.setGender(source.getGender());
        target.setStatus(source.getStatus());
        target.setAddress(source.getAddress());
        target.setTaluka(source.getTaluka());
        target.setDistrict(source.getDistrict());
        target.setPinCode(source.getPinCode());
        target.setReligion(source.getReligion());
        target.setCaste(source.getCaste());
        target.setMaritalStatus(source.getMaritalStatus());
        target.setHeight(source.getHeight().doubleValue());
        target.setWeight(source.getWeight());
        target.setBloodGroup(source.getBloodGroup());
        target.setComplexion(source.getComplexion());
        target.setDiet(source.getDiet());
        target.setSpectacle(source.getSpectacle());
        target.setLens(source.getLens());
        target.setPhysicallyChallenged(source.getPhysicallyChallenged());
        target.setHomeTownDistrict(source.getHomeTownDistrict());
        target.setNativeTaluka(source.getNativeTaluka());
        target.setCurrentCity(source.getCurrentCity());
        return target;
    }

    public HoroscopeCreateRequest toHoroscopeRequest(HoroscopeDetailsStepRequest source) {
        return HoroscopeCreateRequest.builder()
                .dob(Date.valueOf(source.getDob()))
                .time(source.getTime().toString())
                .birthPlace(source.getBirthPlace())
                .nakshatra(source.getNakshatra())
                .rashi(source.getRashi())
                .charan(source.getCharan())
                .nadi(source.getNadi())
                .devak(source.getDevak())
                .gan(source.getGan())
                .gotra(source.getGotra())
                .mangal(source.getMangal())
                .build();
    }

    public EducationAndProfessionCreateRequest toEducationRequest(EducationProfessionStepRequest source) {
        return EducationAndProfessionCreateRequest.builder()
                .education(source.getEducation())
                .degree(source.getDegree())
                .occupation(source.getOccupation())
                .occupationDetails(source.getOccupationDetailsValid())
                .incomePerYear(source.getIncomePerYear().intValue())
                .build();
    }

    public FamilyBackgroundCreateRequest toFamilyBackgroundRequest(FamilyBackgroundStepRequest source) {
        return FamilyBackgroundCreateRequest.builder()
                .fathersName(source.getFathersName())
                .mothersName(source.getMothersName())
                .fatherOccupation(source.getFatherOccupation())
                .motherOccupation(source.getMotherOccupation())
                .brother(source.getBrother())
                .sisters(source.getSisters())
                .marriedBrothers(source.getMarriedBrothers())
                .marriedSisters(source.getMarriedSisters())
                .mamaSurname(source.getMamaSurname())
                .mamaPlace(source.getMamaPlace())
                .parentResiding(source.getParentResiding())
                .interCasteInFamily(source.getInterCasteInFamily())
                .build();
    }

    public ContactDetailsCreateRequest toContactDetailsRequest(ContactDetailsStepRequest source) {
        return ContactDetailsCreateRequest.builder()
                .mobileNumber(String.valueOf(source.getMobileNumber()))
                .emailAddress(source.getEmail())
                .fullAddress(source.getAddress())
                .city(source.getCity())
                .state(source.getState())
                .country(source.getCountry())
                .pinCode(String.valueOf(source.getPinCode()))
                .contactVisibility(source.getVisibility())
                .build();
    }

    public PartnerPreferenceCreateRequest toPartnerPreferencesRequest(PartnerPreferencesStepRequest source) {
        return PartnerPreferenceCreateRequest.builder()
                .lookingFor(source.getLookingFor())
                .ageRange(source.getAgeRange())
                .heightRange(source.getHeightRange())
                .religion(source.getReligion())
                .caste(source.getCaste())
                .education(source.getEducation())
                .partnerOccupation(source.getOccupation())
                .cityLivingIn(source.getLocation())
                .maritalStatus(source.getMaritalStatus())
                .eatingHabits(source.getEatingHabits())
                .drinkingHabits(source.getDrinkingHabits())
                .smokingHabits(source.getSmokingHabits())
                .build();
    }
}
