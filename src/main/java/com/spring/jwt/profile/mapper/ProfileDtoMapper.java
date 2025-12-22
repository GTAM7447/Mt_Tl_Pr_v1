package com.spring.jwt.profile.mapper;

import com.spring.jwt.entity.Enums.Gender;
import com.spring.jwt.entity.Enums.Status;
import com.spring.jwt.entity.User;
import com.spring.jwt.entity.UserProfile;
import com.spring.jwt.profile.dto.request.CreateProfileRequest;
import com.spring.jwt.profile.dto.request.UpdateProfileRequest;
import com.spring.jwt.profile.dto.response.ProfileListView;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.profile.dto.response.PublicProfileView;
import org.springframework.stereotype.Component;

@Component
public class ProfileDtoMapper {

    public UserProfile toEntity(CreateProfileRequest request, User user) {
        UserProfile profile = new UserProfile();

        profile.setFirstName(request.getFirstName());
        profile.setMiddleName(request.getMiddleName());
        profile.setLastName(request.getLastName());
        profile.setAge(request.getAge());
        profile.setGender(Gender.valueOf(request.getGender().toUpperCase()));
        profile.setStatus(Status.valueOf(request.getStatus().toUpperCase()));

        profile.setAddress(request.getAddress());
        profile.setTaluka(request.getTaluka());
        profile.setDistrict(request.getDistrict());
        profile.setPinCode(request.getPinCode());

        profile.setReligion(request.getReligion());
        profile.setCaste(request.getCaste());
        profile.setMaritalStatus(request.getMaritalStatus());
        profile.setHeight(request.getHeight());
        profile.setWeight(request.getWeight());
        profile.setBloodGroup(request.getBloodGroup());
        profile.setComplexion(request.getComplexion());
        profile.setDiet(request.getDiet());

        profile.setSpectacle(request.getSpectacle());
        profile.setLens(request.getLens());
        profile.setPhysicallyChallenged(request.getPhysicallyChallenged());

        profile.setHomeTownDistrict(request.getHomeTownDistrict());
        profile.setNativeTaluka(request.getNativeTaluka());
        profile.setCurrentCity(request.getCurrentCity());

        profile.setUser(user);

        return profile;
    }

    public void applyUpdate(UpdateProfileRequest request, UserProfile existing) {
        updateFieldIfNotNull(request.getFirstName(), existing::setFirstName);
        updateFieldIfNotNull(request.getMiddleName(), existing::setMiddleName);
        updateFieldIfNotNull(request.getLastName(), existing::setLastName);
        updateFieldIfNotNull(request.getAge(), existing::setAge);
        updateEnumFieldIfNotNull(request.getGender(), Gender.class, existing::setGender);
        updateEnumFieldIfNotNull(request.getStatus(), Status.class, existing::setStatus);

        updateFieldIfNotNull(request.getAddress(), existing::setAddress);
        updateFieldIfNotNull(request.getTaluka(), existing::setTaluka);
        updateFieldIfNotNull(request.getDistrict(), existing::setDistrict);
        updateFieldIfNotNull(request.getPinCode(), existing::setPinCode);

        updateFieldIfNotNull(request.getReligion(), existing::setReligion);
        updateFieldIfNotNull(request.getCaste(), existing::setCaste);
        updateFieldIfNotNull(request.getMaritalStatus(), existing::setMaritalStatus);
        updateFieldIfNotNull(request.getHeight(), existing::setHeight);
        updateFieldIfNotNull(request.getWeight(), existing::setWeight);
        updateFieldIfNotNull(request.getBloodGroup(), existing::setBloodGroup);
        updateFieldIfNotNull(request.getComplexion(), existing::setComplexion);
        updateFieldIfNotNull(request.getDiet(), existing::setDiet);

        updateFieldIfNotNull(request.getSpectacle(), existing::setSpectacle);
        updateFieldIfNotNull(request.getLens(), existing::setLens);
        updateFieldIfNotNull(request.getPhysicallyChallenged(), existing::setPhysicallyChallenged);

        updateFieldIfNotNull(request.getHomeTownDistrict(), existing::setHomeTownDistrict);
        updateFieldIfNotNull(request.getNativeTaluka(), existing::setNativeTaluka);
        updateFieldIfNotNull(request.getCurrentCity(), existing::setCurrentCity);
    }

    private <T> void updateFieldIfNotNull(T newValue, java.util.function.Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    private <E extends Enum<E>> void updateEnumFieldIfNotNull(String newValue, Class<E> enumClass, java.util.function.Consumer<E> setter) {
        if (newValue != null) {
            setter.accept(Enum.valueOf(enumClass, newValue.toUpperCase()));
        }
    }

    public ProfileResponse toResponse(UserProfile profile) {
        return ProfileResponse.builder()
                .userProfileId(profile.getUserProfileId())
                .version(profile.getVersion())
                .firstName(profile.getFirstName())
                .middleName(profile.getMiddleName())
                .lastName(profile.getLastName())
                .age(profile.getAge())
                .gender(profile.getGender().name())
                .status(profile.getStatus().name())
                .address(profile.getAddress())
                .taluka(profile.getTaluka())
                .district(profile.getDistrict())
                .pinCode(profile.getPinCode())
                .religion(profile.getReligion())
                .caste(profile.getCaste())
                .maritalStatus(profile.getMaritalStatus())
                .height(profile.getHeight())
                .weight(profile.getWeight())
                .bloodGroup(profile.getBloodGroup())
                .complexion(profile.getComplexion())
                .diet(profile.getDiet())
                .spectacle(profile.getSpectacle())
                .lens(profile.getLens())
                .physicallyChallenged(profile.getPhysicallyChallenged())
                .homeTownDistrict(profile.getHomeTownDistrict())
                .nativeTaluka(profile.getNativeTaluka())
                .currentCity(profile.getCurrentCity())
                .userId(profile.getUser().getId())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .createdBy(profile.getCreatedBy())
                .updatedBy(profile.getUpdatedBy())
                .build();
    }

    public PublicProfileView toPublicView(UserProfile profile) {
        return PublicProfileView.builder()
                .userProfileId(profile.getUserProfileId())
                .firstName(profile.getFirstName())
                .age(profile.getAge())
                .gender(profile.getGender().name())
                .religion(profile.getReligion())
                .caste(profile.getCaste())
                .height(profile.getHeight())
                .complexion(profile.getComplexion())
                .currentCity(profile.getCurrentCity())
                .maritalStatus(profile.getMaritalStatus())
                .build();
    }

    public ProfileListView toListView(UserProfile profile) {
        return ProfileListView.builder()
                .userProfileId(profile.getUserProfileId())
                .firstName(profile.getFirstName())
                .lastName(profile.getLastName())
                .age(profile.getAge())
                .gender(profile.getGender().name())
                .religion(profile.getReligion())
                .caste(profile.getCaste())
                .height(profile.getHeight())
                .district(profile.getDistrict())
                .currentCity(profile.getCurrentCity())
                .maritalStatus(profile.getMaritalStatus())
                .status(profile.getStatus().name())
                .build();
    }
}
