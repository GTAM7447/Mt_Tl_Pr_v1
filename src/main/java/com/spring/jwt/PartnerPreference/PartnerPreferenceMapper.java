package com.spring.jwt.PartnerPreference;

import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceCreateRequest;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceUpdateRequest;
import com.spring.jwt.entity.PartnerPreference;
import com.spring.jwt.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Component
@RequiredArgsConstructor
public class PartnerPreferenceMapper {

    public PartnerPreference toEntity(PartnerPreferenceCreateRequest request, User user) {
        PartnerPreference entity = new PartnerPreference();
        
        entity.setAgeRange(request.getAgeRange());
        entity.setLookingFor(request.getLookingFor());
        entity.setHeightRange(request.getHeightRange());
        entity.setEatingHabits(request.getEatingHabits());
        entity.setDrinkingHabits(request.getDrinkingHabits());
        entity.setSmokingHabits(request.getSmokingHabits());
        entity.setCountryLivingIn(request.getCountryLivingIn());
        entity.setCityLivingIn(request.getCityLivingIn());
        entity.setStateLivingIn(request.getStateLivingIn());
        entity.setComplexion(request.getComplexion());
        entity.setReligion(request.getReligion());
        entity.setCaste(request.getCaste());
        entity.setSubCaste(request.getSubCaste());
        entity.setEducation(request.getEducation());
        entity.setMangal(request.getMangal());
        entity.setResidentStatus(request.getResidentStatus());
        entity.setPartnerOccupation(request.getPartnerOccupation());
        entity.setPartnerIncome(request.getPartnerIncome());
        entity.setMaritalStatus(request.getMaritalStatus());
        entity.setMotherTongue(request.getMotherTongue());
        entity.setAdditionalPreferences(request.getAdditionalPreferences());
        entity.setUser(user);
        
        return entity;
    }

    public PartnerPreferenceResponse toResponse(PartnerPreference entity) {
        return PartnerPreferenceResponse.builder()
                .partnerPreferenceId(entity.getPartnerPreferenceId())
                .ageRange(entity.getAgeRange())
                .lookingFor(entity.getLookingFor())
                .heightRange(entity.getHeightRange())
                .eatingHabits(entity.getEatingHabits())
                .drinkingHabits(entity.getDrinkingHabits())
                .smokingHabits(entity.getSmokingHabits())
                .countryLivingIn(entity.getCountryLivingIn())
                .cityLivingIn(entity.getCityLivingIn())
                .stateLivingIn(entity.getStateLivingIn())
                .complexion(entity.getComplexion())
                .religion(entity.getReligion())
                .caste(entity.getCaste())
                .subCaste(entity.getSubCaste())
                .education(entity.getEducation())
                .mangal(entity.getMangal())
                .residentStatus(entity.getResidentStatus())
                .partnerOccupation(entity.getPartnerOccupation())
                .partnerIncome(entity.getPartnerIncome())
                .maritalStatus(entity.getMaritalStatus())
                .motherTongue(entity.getMotherTongue())
                .additionalPreferences(entity.getAdditionalPreferences())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    public void applyUpdate(PartnerPreferenceUpdateRequest request, PartnerPreference existing) {
        updateFieldIfNotNull(request.getAgeRange(), existing::setAgeRange);
        updateFieldIfNotNull(request.getLookingFor(), existing::setLookingFor);
        updateFieldIfNotNull(request.getHeightRange(), existing::setHeightRange);
        updateFieldIfNotNull(request.getEatingHabits(), existing::setEatingHabits);
        updateFieldIfNotNull(request.getDrinkingHabits(), existing::setDrinkingHabits);
        updateFieldIfNotNull(request.getSmokingHabits(), existing::setSmokingHabits);
        updateFieldIfNotNull(request.getCountryLivingIn(), existing::setCountryLivingIn);
        updateFieldIfNotNull(request.getCityLivingIn(), existing::setCityLivingIn);
        updateFieldIfNotNull(request.getStateLivingIn(), existing::setStateLivingIn);
        updateFieldIfNotNull(request.getComplexion(), existing::setComplexion);
        updateFieldIfNotNull(request.getReligion(), existing::setReligion);
        updateFieldIfNotNull(request.getCaste(), existing::setCaste);
        updateFieldIfNotNull(request.getSubCaste(), existing::setSubCaste);
        updateFieldIfNotNull(request.getEducation(), existing::setEducation);
        updateFieldIfNotNull(request.getMangal(), existing::setMangal);
        updateFieldIfNotNull(request.getResidentStatus(), existing::setResidentStatus);
        updateFieldIfNotNull(request.getPartnerOccupation(), existing::setPartnerOccupation);
        updateFieldIfNotNull(request.getPartnerIncome(), existing::setPartnerIncome);
        updateFieldIfNotNull(request.getMaritalStatus(), existing::setMaritalStatus);
        updateFieldIfNotNull(request.getMotherTongue(), existing::setMotherTongue);
        updateFieldIfNotNull(request.getAdditionalPreferences(), existing::setAdditionalPreferences);
    }

    private <T> void updateFieldIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
