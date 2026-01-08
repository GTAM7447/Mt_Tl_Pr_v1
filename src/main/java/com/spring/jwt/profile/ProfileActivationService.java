package com.spring.jwt.profile;

import com.spring.jwt.entity.Enums.Status;
import com.spring.jwt.entity.UserProfile;

import com.spring.jwt.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProfileActivationService {

    private final UserProfileRepository profileRepository;

    @Transactional
    @CacheEvict(value = {"profiles", "publicProfiles", "completeProfiles"}, allEntries = true)
    public void activateProfile(Integer userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        
        if (profile.getStatus() == Status.ACTIVE) {
            log.info("Profile already active for user: {}", userId);
            return;
        }
        
        profile.setStatus(Status.ACTIVE);
        profileRepository.save(profile);
        
        log.info("Profile activated for user: {}", userId);
    }

    @Transactional
    @CacheEvict(value = {"profiles", "publicProfiles", "completeProfiles"}, allEntries = true)
    public void deactivateProfile(Integer userId) {
        UserProfile profile = profileRepository.findByUserId(userId)
            .orElseThrow(() -> new RuntimeException("Profile not found for user: " + userId));
        
        if (profile.getStatus() == Status.DEACTIVE) {
            log.info("Profile already inactive for user: {}", userId);
            return;
        }
        
        profile.setStatus(Status.DEACTIVE);
        profileRepository.save(profile);
        
        log.info("Profile deactivated for user: {}", userId);
    }

    @Transactional(readOnly = true)
    public boolean isProfileActive(Integer userId) {
        return profileRepository.findByUserId(userId)
            .map(profile -> profile.getStatus() == Status.ACTIVE)
            .orElse(false);
    }

    @Transactional(readOnly = true)
    public Status getProfileStatus(Integer userId) {
        return profileRepository.findByUserId(userId)
            .map(UserProfile::getStatus)
            .orElse(Status.DEACTIVE);
    }
}
