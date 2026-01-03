package com.spring.jwt.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class CacheInvalidationAspect {

    private final CacheManager cacheManager;

    @AfterReturning("execution(* com.spring.jwt..service.*ServiceImpl.update*(..)) || " +
                    "execution(* com.spring.jwt..service.*ServiceImpl.delete*(..)) || " +
                    "execution(* com.spring.jwt..service.*ServiceImpl.create*(..))")
    public void invalidateCacheOnUpdate(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        
        log.debug("Cache invalidation triggered by {}.{}", className, methodName);
        
        String cacheName = determineCacheName(className);
        if (cacheName != null) {
            evictCache(cacheName);
            evictRelatedCaches(cacheName);
        }
    }

    private String determineCacheName(String className) {
        if (className.contains("Profile")) return "profiles";
        if (className.contains("Horoscope")) return "horoscopes";
        if (className.contains("Education")) return "educationProfiles";
        if (className.contains("Family")) return "familyBackgrounds";
        if (className.contains("PartnerPreference")) return "partnerPreferences";
        if (className.contains("Contact")) return "contactDetails";
        if (className.contains("Document")) return "documents";
        if (className.contains("CompleteProfile")) return "completeProfiles";
        if (className.contains("ExpressInterest")) return "expressInterests";
        if (className.contains("Subscription")) return "subscriptions";
        if (className.contains("UserCredit")) return "userCredits";
        if (className.contains("User")) return "users";
        return null;
    }

    private void evictCache(String cacheName) {
        try {
            var cache = cacheManager.getCache(cacheName);
            if (cache != null) {
                cache.clear();
                log.debug("Cache '{}' cleared", cacheName);
            }
        } catch (Exception e) {
            log.warn("Failed to clear cache '{}': {}", cacheName, e.getMessage());
        }
    }

    private void evictRelatedCaches(String cacheName) {
        switch (cacheName) {
            case "profiles":
                evictCache("publicProfiles");
                evictCache("profileStats");
                evictCache("public_profiles");
                evictCache("completeProfiles");
                break;
            case "horoscopes":
                evictCache("horoscopeStats");
                evictCache("completeProfiles");
                break;
            case "educationProfiles":
            case "familyBackgrounds":
            case "partnerPreferences":
            case "contactDetails":
                evictCache("completeProfiles");
                break;
            case "users":
                evictCache("userDetails");
                evictCache("profiles");
                evictCache("completeProfiles");
                break;
            case "documents":
                evictCache("documentMetadata");
                break;
        }
    }
}
