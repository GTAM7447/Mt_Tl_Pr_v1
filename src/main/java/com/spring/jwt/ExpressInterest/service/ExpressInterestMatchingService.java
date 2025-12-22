package com.spring.jwt.ExpressInterest.service;

import com.spring.jwt.entity.User;
import com.spring.jwt.repository.UserRepository;
import com.spring.jwt.PartnerPreference.PartnerPreferenceService;
import com.spring.jwt.PartnerPreference.dto.PartnerPreferenceResponse;
import com.spring.jwt.profile.ProfileService;
import com.spring.jwt.profile.dto.response.ProfileResponse;
import com.spring.jwt.EducationAndProfession.EducationAndProfessionService;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.ContactDetails.ContactDetailsService;
import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.*;

/**
 * Service for calculating compatibility scores and matching logic.
 * Provides sophisticated matching algorithms based on user preferences and profiles.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExpressInterestMatchingService {

    private final UserRepository userRepository;
    private final PartnerPreferenceService partnerPreferenceService;
    private final ProfileService profileService;
    private final EducationAndProfessionService educationAndProfessionService;
    private final ContactDetailsService contactDetailsService;

    private static final int RELIGION_WEIGHT = 20;
    private static final int CASTE_WEIGHT = 15;
    private static final int EDUCATION_WEIGHT = 15;
    private static final int PROFESSION_WEIGHT = 10;
    private static final int INCOME_WEIGHT = 10;
    private static final int AGE_WEIGHT = 15;
    private static final int LOCATION_WEIGHT = 10;
    private static final int LIFESTYLE_WEIGHT = 5;

    /**
     * Calculate compatibility score between two users.
     *
     * @param userId1 first user ID
     * @param userId2 second user ID
     * @return compatibility score (0-100)
     */
    public Integer calculateCompatibilityScore(Integer userId1, Integer userId2) {
        log.debug("Calculating compatibility score between users {} and {}", userId1, userId2);

        try {

            Optional<User> user1Opt = userRepository.findById(userId1);
            Optional<User> user2Opt = userRepository.findById(userId2);

            if (user1Opt.isEmpty() || user2Opt.isEmpty()) {
                log.warn("One or both users not found: {} or {}", userId1, userId2);
                return 0;
            }

            User user1 = user1Opt.get();
            User user2 = user2Opt.get();

            ProfileResponse profile1 = null;
            ProfileResponse profile2 = null;
            
            try {
                profile1 = profileService.getProfileByUserId(userId1);
            } catch (Exception e) {
                log.warn("Profile not found for user {}: {}", userId1, e.getMessage());
            }
            
            try {
                profile2 = profileService.getProfileByUserId(userId2);
            } catch (Exception e) {
                log.warn("Profile not found for user {}: {}", userId2, e.getMessage());
            }

            if (profile1 == null || profile2 == null) {
                log.info("Cannot calculate compatibility - missing profile data for users {} and {}", userId1, userId2);
                return 25;
            }

            PartnerPreferenceResponse preferences1 = getPartnerPreferences(userId1);
            PartnerPreferenceResponse preferences2 = getPartnerPreferences(userId2);

            int totalScore = 0;
            int maxPossibleScore = 0;

            int religionScore = calculateReligionCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += religionScore;
            maxPossibleScore += RELIGION_WEIGHT;

            int casteScore = calculateCasteCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += casteScore;
            maxPossibleScore += CASTE_WEIGHT;

            int educationScore = calculateEducationCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += educationScore;
            maxPossibleScore += EDUCATION_WEIGHT;

            int professionScore = calculateProfessionCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += professionScore;
            maxPossibleScore += PROFESSION_WEIGHT;

            int incomeScore = calculateIncomeCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += incomeScore;
            maxPossibleScore += INCOME_WEIGHT;

            int ageScore = calculateAgeCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += ageScore;
            maxPossibleScore += AGE_WEIGHT;

            int locationScore = calculateLocationCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += locationScore;
            maxPossibleScore += LOCATION_WEIGHT;

            int lifestyleScore = calculateLifestyleCompatibility(profile1, profile2, preferences1, preferences2);
            totalScore += lifestyleScore;
            maxPossibleScore += LIFESTYLE_WEIGHT;

            int compatibilityScore = maxPossibleScore > 0 ? (totalScore * 100) / maxPossibleScore : 0;

            log.debug("Compatibility score calculated: {} for users {} and {}", compatibilityScore, userId1, userId2);
            return Math.min(100, Math.max(0, compatibilityScore));

        } catch (Exception e) {
            log.error("Error calculating compatibility score for users {} and {}: {}", userId1, userId2, e.getMessage(), e);
            return 0;
        }
    }

    public boolean areBasicallyCompatible(Integer userId1, Integer userId2) {
        try {
            Integer score = calculateCompatibilityScore(userId1, userId2);
            return score != null && score >= 50;
        } catch (Exception e) {
            log.error("Error checking basic compatibility for users {} and {}: {}", userId1, userId2, e.getMessage());
            return false;
        }
    }

    public Map<String, Integer> getCompatibilityBreakdown(Integer userId1, Integer userId2) {
        log.debug("Getting compatibility breakdown between users {} and {}", userId1, userId2);

        Map<String, Integer> breakdown = new HashMap<>();

        try {
            ProfileResponse profile1 = profileService.getProfileByUserId(userId1);
            ProfileResponse profile2 = profileService.getProfileByUserId(userId2);

            PartnerPreferenceResponse preferences1 = getPartnerPreferences(userId1);
            PartnerPreferenceResponse preferences2 = getPartnerPreferences(userId2);

            breakdown.put("religion", calculateReligionCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("caste", calculateCasteCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("education", calculateEducationCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("profession", calculateProfessionCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("income", calculateIncomeCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("age", calculateAgeCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("location", calculateLocationCompatibility(profile1, profile2, preferences1, preferences2));
            breakdown.put("lifestyle", calculateLifestyleCompatibility(profile1, profile2, preferences1, preferences2));

            int totalScore = breakdown.values().stream().mapToInt(Integer::intValue).sum();
            int maxScore = RELIGION_WEIGHT + CASTE_WEIGHT + EDUCATION_WEIGHT + PROFESSION_WEIGHT + 
                          INCOME_WEIGHT + AGE_WEIGHT + LOCATION_WEIGHT + LIFESTYLE_WEIGHT;
            
            breakdown.put("overall", maxScore > 0 ? (totalScore * 100) / maxScore : 0);

        } catch (Exception e) {
            log.error("Error getting compatibility breakdown for users {} and {}: {}", userId1, userId2, e.getMessage());
        }

        return breakdown;
    }

    private int calculateReligionCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                             PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            String religion1 = profile1.getReligion();
            String religion2 = profile2.getReligion();

            if (religion1 == null || religion2 == null) {
                return 0;
            }

            if (religion1.equalsIgnoreCase(religion2)) {
                return RELIGION_WEIGHT;
            }

            if (pref1 != null && pref1.getReligion() != null && 
                (pref1.getReligion().equalsIgnoreCase("Any") || pref1.getReligion().equalsIgnoreCase(religion2))) {
                return RELIGION_WEIGHT / 2;
            }

            if (pref2 != null && pref2.getReligion() != null && 
                (pref2.getReligion().equalsIgnoreCase("Any") || pref2.getReligion().equalsIgnoreCase(religion1))) {
                return RELIGION_WEIGHT / 2;
            }

            return 0;
        } catch (Exception e) {
            log.warn("Error calculating religion compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateCasteCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                          PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            String caste1 = profile1.getCaste();
            String caste2 = profile2.getCaste();

            if (caste1 == null || caste2 == null) {
                return CASTE_WEIGHT / 2;
            }

            if (caste1.equalsIgnoreCase(caste2)) {
                return CASTE_WEIGHT;
            }

            if (pref1 != null && pref1.getCaste() != null && 
                (pref1.getCaste().equalsIgnoreCase("Any") || pref1.getCaste().equalsIgnoreCase(caste2))) {
                return CASTE_WEIGHT / 2;
            }

            if (pref2 != null && pref2.getCaste() != null && 
                (pref2.getCaste().equalsIgnoreCase("Any") || pref2.getCaste().equalsIgnoreCase(caste1))) {
                return CASTE_WEIGHT / 2;
            }

            return 0;
        } catch (Exception e) {
            log.warn("Error calculating caste compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateEducationCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                              PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            EducationAndProfessionResponse edu1 = getEducationAndProfession(profile1.getUserId());
            EducationAndProfessionResponse edu2 = getEducationAndProfession(profile2.getUserId());

            if (edu1 == null || edu2 == null) {
                return 0;
            }

            String education1 = edu1.getEducation();
            String education2 = edu2.getEducation();

            if (education1 == null || education2 == null) {
                return 0;
            }

            Map<String, Integer> educationLevels = Map.of(
                "High School", 1,
                "Diploma", 2,
                "Bachelor's Degree", 3,
                "Master's Degree", 4,
                "PhD", 5
            );

            Integer level1 = educationLevels.get(education1);
            Integer level2 = educationLevels.get(education2);

            if (level1 == null || level2 == null) {
                return EDUCATION_WEIGHT / 2;
            }

            int levelDiff = Math.abs(level1 - level2);
            if (levelDiff == 0) {
                return EDUCATION_WEIGHT;
            } else if (levelDiff == 1) {
                return (EDUCATION_WEIGHT * 3) / 4;
            } else if (levelDiff == 2) {
                return EDUCATION_WEIGHT / 2;
            } else {
                return EDUCATION_WEIGHT / 4;
            }

        } catch (Exception e) {
            log.warn("Error calculating education compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateProfessionCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                               PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            EducationAndProfessionResponse edu1 = getEducationAndProfession(profile1.getUserId());
            EducationAndProfessionResponse edu2 = getEducationAndProfession(profile2.getUserId());

            if (edu1 == null || edu2 == null) {
                return 0;
            }

            String profession1 = edu1.getOccupation();
            String profession2 = edu2.getOccupation();

            if (profession1 == null || profession2 == null) {
                return 0;
            }

            if (profession1.equalsIgnoreCase(profession2)) {
                return PROFESSION_WEIGHT;
            }

            if (areSimilarProfessions(profession1, profession2)) {
                return (PROFESSION_WEIGHT * 3) / 4;
            }

            return PROFESSION_WEIGHT / 2;
        } catch (Exception e) {
            log.warn("Error calculating profession compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateIncomeCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                           PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            EducationAndProfessionResponse edu1 = getEducationAndProfession(profile1.getUserId());
            EducationAndProfessionResponse edu2 = getEducationAndProfession(profile2.getUserId());

            if (edu1 == null || edu2 == null) {
                return 0;
            }

            Integer income1 = edu1.getIncomePerYear();
            Integer income2 = edu2.getIncomePerYear();

            if (income1 == null || income2 == null) {
                return 0;
            }

            double incomeDiff = Math.abs(income1 - income2) / (double) Math.max(income1, income2);
            
            if (incomeDiff <= 0.2) {
                return INCOME_WEIGHT;
            } else if (incomeDiff <= 0.5) {
                return (INCOME_WEIGHT * 3) / 4;
            } else if (incomeDiff <= 1.0) {
                return INCOME_WEIGHT / 2;
            } else {
                return INCOME_WEIGHT / 4;
            }

        } catch (Exception e) {
            log.warn("Error calculating income compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateAgeCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                        PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            Integer age1 = profile1.getAge();
            Integer age2 = profile2.getAge();

            if (age1 == null || age2 == null) {
                return 0;
            }

            int ageDiff = Math.abs(age1 - age2);

            if (ageDiff <= 2) {
                return AGE_WEIGHT;
            } else if (ageDiff <= 5) {
                return (AGE_WEIGHT * 3) / 4;
            } else if (ageDiff <= 10) {
                return AGE_WEIGHT / 2;
            } else {
                return AGE_WEIGHT / 4;
            }

        } catch (Exception e) {
            log.warn("Error calculating age compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateLocationCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                             PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            ContactDetailsResponse contact1 = getContactDetails(profile1.getUserId());
            ContactDetailsResponse contact2 = getContactDetails(profile2.getUserId());

            if (contact1 == null || contact2 == null) {
                String city1 = profile1.getCurrentCity();
                String city2 = profile2.getCurrentCity();
                
                if (city1 != null && city2 != null && city1.equalsIgnoreCase(city2)) {
                    return LOCATION_WEIGHT / 2;
                }
                return 0;
            }

            String city1 = contact1.getCity();
            String state1 = contact1.getState();
            String country1 = contact1.getCountry();

            String city2 = contact2.getCity();
            String state2 = contact2.getState();
            String country2 = contact2.getCountry();

            if (country1 == null || country2 == null) {
                return 0;
            }

            if (!country1.equalsIgnoreCase(country2)) {
                return 0;
            }

            if (city1 != null && city2 != null && city1.equalsIgnoreCase(city2)) {
                return LOCATION_WEIGHT;
            }

            if (state1 != null && state2 != null && state1.equalsIgnoreCase(state2)) {
                return (LOCATION_WEIGHT * 3) / 4;
            }

            return LOCATION_WEIGHT / 2;

        } catch (Exception e) {
            log.warn("Error calculating location compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private int calculateLifestyleCompatibility(ProfileResponse profile1, ProfileResponse profile2, 
                                              PartnerPreferenceResponse pref1, PartnerPreferenceResponse pref2) {
        try {
            String diet1 = profile1.getDiet();
            String diet2 = profile2.getDiet();
            
            int score = 0;

            if (diet1 != null && diet2 != null) {
                if (diet1.equalsIgnoreCase(diet2)) {
                    score += LIFESTYLE_WEIGHT / 2;
                }
            }

            score += LIFESTYLE_WEIGHT / 2;
            
            return score;
        } catch (Exception e) {
            log.warn("Error calculating lifestyle compatibility: {}", e.getMessage());
            return 0;
        }
    }

    private PartnerPreferenceResponse getPartnerPreferences(Integer userId) {
        try {
            return partnerPreferenceService.getByUserId(userId);
        } catch (Exception e) {
            log.debug("Partner preferences not found for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private EducationAndProfessionResponse getEducationAndProfession(Integer userId) {
        try {
            return educationAndProfessionService.getByUserId(userId);
        } catch (Exception e) {
            log.debug("Education and profession not found for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private ContactDetailsResponse getContactDetails(Integer userId) {
        try {
            return contactDetailsService.getByUserId(userId);
        } catch (Exception e) {
            log.debug("Contact details not found for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    /**
     * Check if two professions are similar.
     */
    private boolean areSimilarProfessions(String profession1, String profession2) {
        Set<String> techProfessions = Set.of("Software Engineer", "Data Scientist", "System Administrator",
                                           "Web Developer", "Mobile Developer", "DevOps Engineer");
        Set<String> medicalProfessions = Set.of("Doctor", "Nurse", "Pharmacist", "Dentist", "Surgeon");
        Set<String> businessProfessions = Set.of("Manager", "Consultant", "Analyst", "Sales Executive", "Marketing Manager");
        Set<String> educationProfessions = Set.of("Teacher", "Professor", "Principal", "Lecturer", "Trainer");

        List<Set<String>> professionGroups = List.of(techProfessions, medicalProfessions, businessProfessions, educationProfessions);

        for (Set<String> group : professionGroups) {
            if (group.contains(profession1) && group.contains(profession2)) {
                return true;
            }
        }

        return false;
    }

    /**
     * Get suggested matches for a user based on compatibility.
     */
    public List<Integer> getSuggestedMatches(Integer userId, int limit) {
        log.debug("Getting suggested matches for user: {}", userId);

        List<Integer> suggestedMatches = new ArrayList<>();

        try {
            List<User> allUsers = userRepository.findAll();
            
            Map<Integer, Integer> userScores = new HashMap<>();
            
            for (User user : allUsers) {
                if (!user.getId().equals(userId) && user.getEmailVerified() != null && user.getEmailVerified()) {
                    Integer score = calculateCompatibilityScore(userId, user.getId());
                    if (score != null && score >= 60) {
                        userScores.put(user.getId(), score);
                    }
                }
            }

            suggestedMatches = userScores.entrySet().stream()
                    .sorted(Map.Entry.<Integer, Integer>comparingByValue().reversed())
                    .limit(limit)
                    .map(Map.Entry::getKey)
                    .toList();

        } catch (Exception e) {
            log.error("Error getting suggested matches for user {}: {}", userId, e.getMessage());
        }

        return suggestedMatches;
    }
}