package com.spring.jwt.PartnerPreference;

import com.spring.jwt.entity.PartnerPreference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for PartnerPreference entity.
 * Provides data access methods with proper query optimization.
 */
@Repository
public interface PartnerPreferenceRepository extends JpaRepository<PartnerPreference, Integer> {

    /**
     * Check if partner preference exists for a user.
     *
     * @param userId the user ID
     * @return true if exists
     */
    boolean existsByUser_Id(Integer userId);

    /**
     * Find partner preference by user ID.
     *
     * @param userId the user ID
     * @return optional partner preference
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE pp.user.id = :userId")
    Optional<PartnerPreference> findByUser_Id(@Param("userId") Integer userId);

    /**
     * Find partner preference by ID with user information.
     *
     * @param id the partner preference ID
     * @return optional partner preference with user
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE pp.partnerPreferenceId = :id")
    Optional<PartnerPreference> findByIdWithUser(@Param("id") Integer id);

    /**
     * Find all partner preferences with pagination and user information.
     *
     * @param pageable pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user")
    Page<PartnerPreference> findAllWithUser(Pageable pageable);

    /**
     * Find partner preferences by religion.
     *
     * @param religion the religion
     * @param pageable pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE LOWER(pp.religion) LIKE LOWER(CONCAT('%', :religion, '%'))")
    Page<PartnerPreference> findByReligionContainingIgnoreCase(@Param("religion") String religion, Pageable pageable);

    /**
     * Find partner preferences by caste.
     *
     * @param caste    the caste
     * @param pageable pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE LOWER(pp.caste) LIKE LOWER(CONCAT('%', :caste, '%'))")
    Page<PartnerPreference> findByCasteContainingIgnoreCase(@Param("caste") String caste, Pageable pageable);

    /**
     * Find partner preferences by education level.
     *
     * @param education the education level
     * @param pageable  pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE LOWER(pp.education) LIKE LOWER(CONCAT('%', :education, '%'))")
    Page<PartnerPreference> findByEducationContainingIgnoreCase(@Param("education") String education, Pageable pageable);

    /**
     * Find partner preferences by income range.
     *
     * @param minIncome minimum income
     * @param maxIncome maximum income
     * @param pageable  pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE pp.partnerIncome BETWEEN :minIncome AND :maxIncome")
    Page<PartnerPreference> findByIncomeRange(@Param("minIncome") Integer minIncome, 
                                              @Param("maxIncome") Integer maxIncome, 
                                              Pageable pageable);

    /**
     * Find partner preferences by location.
     *
     * @param location the location (city, state, or country)
     * @param pageable pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE " +
           "LOWER(pp.cityLivingIn) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(pp.stateLivingIn) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           "LOWER(pp.countryLivingIn) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<PartnerPreference> findByLocationContainingIgnoreCase(@Param("location") String location, Pageable pageable);

    /**
     * Find partner preferences by occupation.
     *
     * @param occupation the partner occupation
     * @param pageable   pagination information
     * @return page of partner preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE LOWER(pp.partnerOccupation) LIKE LOWER(CONCAT('%', :occupation, '%'))")
    Page<PartnerPreference> findByPartnerOccupationContainingIgnoreCase(@Param("occupation") String occupation, Pageable pageable);

    /**
     * Count partner preferences by religion.
     *
     * @param religion the religion
     * @return count of preferences
     */
    @Query("SELECT COUNT(pp) FROM PartnerPreference pp WHERE LOWER(pp.religion) LIKE LOWER(CONCAT('%', :religion, '%'))")
    long countByReligionContainingIgnoreCase(@Param("religion") String religion);

    /**
     * Count partner preferences by caste.
     *
     * @param caste the caste
     * @return count of preferences
     */
    @Query("SELECT COUNT(pp) FROM PartnerPreference pp WHERE LOWER(pp.caste) LIKE LOWER(CONCAT('%', :caste, '%'))")
    long countByCasteContainingIgnoreCase(@Param("caste") String caste);

    /**
     * Count partner preferences by education level.
     *
     * @param education the education level
     * @return count of preferences
     */
    @Query("SELECT COUNT(pp) FROM PartnerPreference pp WHERE LOWER(pp.education) LIKE LOWER(CONCAT('%', :education, '%'))")
    long countByEducationContainingIgnoreCase(@Param("education") String education);

    /**
     * Find partner preferences with complex search criteria.
     *
     * @param religion      the religion (optional)
     * @param caste         the caste (optional)
     * @param education     the education level (optional)
     * @param minIncome     minimum income (optional)
     * @param maxIncome     maximum income (optional)
     * @param location      location (city/state/country) (optional)
     * @param maritalStatus marital status (optional)
     * @param pageable      pagination information
     * @return page of matching preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE " +
           "(:religion IS NULL OR LOWER(pp.religion) LIKE LOWER(CONCAT('%', :religion, '%'))) AND " +
           "(:caste IS NULL OR LOWER(pp.caste) LIKE LOWER(CONCAT('%', :caste, '%'))) AND " +
           "(:education IS NULL OR LOWER(pp.education) LIKE LOWER(CONCAT('%', :education, '%'))) AND " +
           "(:minIncome IS NULL OR pp.partnerIncome >= :minIncome) AND " +
           "(:maxIncome IS NULL OR pp.partnerIncome <= :maxIncome) AND " +
           "(:location IS NULL OR LOWER(pp.cityLivingIn) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           " LOWER(pp.stateLivingIn) LIKE LOWER(CONCAT('%', :location, '%')) OR " +
           " LOWER(pp.countryLivingIn) LIKE LOWER(CONCAT('%', :location, '%'))) AND " +
           "(:maritalStatus IS NULL OR LOWER(pp.maritalStatus) LIKE LOWER(CONCAT('%', :maritalStatus, '%')))")
    Page<PartnerPreference> searchPartnerPreferences(
            @Param("religion") String religion,
            @Param("caste") String caste,
            @Param("education") String education,
            @Param("minIncome") Integer minIncome,
            @Param("maxIncome") Integer maxIncome,
            @Param("location") String location,
            @Param("maritalStatus") String maritalStatus,
            Pageable pageable);

    /**
     * Find partner preferences by lifestyle choices.
     *
     * @param eatingHabits   eating habits (optional)
     * @param drinkingHabits drinking habits (optional)
     * @param smokingHabits  smoking habits (optional)
     * @param pageable       pagination information
     * @return page of matching preferences
     */
    @Query("SELECT pp FROM PartnerPreference pp JOIN FETCH pp.user WHERE " +
           "(:eatingHabits IS NULL OR LOWER(pp.eatingHabits) LIKE LOWER(CONCAT('%', :eatingHabits, '%'))) AND " +
           "(:drinkingHabits IS NULL OR LOWER(pp.drinkingHabits) LIKE LOWER(CONCAT('%', :drinkingHabits, '%'))) AND " +
           "(:smokingHabits IS NULL OR LOWER(pp.smokingHabits) LIKE LOWER(CONCAT('%', :smokingHabits, '%')))")
    Page<PartnerPreference> findByLifestyleChoices(
            @Param("eatingHabits") String eatingHabits,
            @Param("drinkingHabits") String drinkingHabits,
            @Param("smokingHabits") String smokingHabits,
            Pageable pageable);
}
