package com.spring.jwt.CompleteProfile;

import com.spring.jwt.entity.CompleteProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for CompleteProfile entity.
 * Provides advanced querying capabilities for profile analytics and reporting.
 */
@Repository
public interface CompleteProfileRepository extends JpaRepository<CompleteProfile, Integer> {

    /**
     * Find complete profile by user ID.
     */
    Optional<CompleteProfile> findByUser_Id(Integer userId);

    /**
     * Check if complete profile exists for user.
     */
    boolean existsByUser_Id(Integer userId);

    /**
     * Find all complete profiles with user information (for admin operations).
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "ORDER BY cp.updatedAt DESC")
    Page<CompleteProfile> findAllWithUser(Pageable pageable);

    /**
     * Find profiles by completion percentage range.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND cp.completionPercentage BETWEEN :minPercentage AND :maxPercentage")
    Page<CompleteProfile> findByCompletionPercentageRange(
            @Param("minPercentage") Integer minPercentage,
            @Param("maxPercentage") Integer maxPercentage,
            Pageable pageable);

    /**
     * Find profiles by profile quality.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND cp.profileQuality IN :qualities")
    Page<CompleteProfile> findByProfileQualityIn(
            @Param("qualities") List<CompleteProfile.ProfileQuality> qualities,
            Pageable pageable);

    /**
     * Find profiles by verification status.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND cp.verificationStatus IN :statuses")
    Page<CompleteProfile> findByVerificationStatusIn(
            @Param("statuses") List<CompleteProfile.VerificationStatus> statuses,
            Pageable pageable);

    /**
     * Find incomplete profiles (profiles with missing sections).
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND cp.profileCompleted = false")
    Page<CompleteProfile> findIncompleteProfiles(Pageable pageable);

    /**
     * Find profiles updated within date range.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND cp.updatedAt BETWEEN :startDate AND :endDate")
    Page<CompleteProfile> findByUpdatedAtBetween(
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    /**
     * Advanced search with multiple criteria.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND (:minPercentage IS NULL OR cp.completionPercentage >= :minPercentage) " +
           "AND (:maxPercentage IS NULL OR cp.completionPercentage <= :maxPercentage) " +
           "AND (:profileQuality IS NULL OR cp.profileQuality = :profileQuality) " +
           "AND (:verificationStatus IS NULL OR cp.verificationStatus = :verificationStatus) " +
           "AND (:profileCompleted IS NULL OR cp.profileCompleted = :profileCompleted) " +
           "AND (:startDate IS NULL OR cp.updatedAt >= :startDate) " +
           "AND (:endDate IS NULL OR cp.updatedAt <= :endDate)")
    Page<CompleteProfile> findByAdvancedCriteria(
            @Param("minPercentage") Integer minPercentage,
            @Param("maxPercentage") Integer maxPercentage,
            @Param("profileQuality") CompleteProfile.ProfileQuality profileQuality,
            @Param("verificationStatus") CompleteProfile.VerificationStatus verificationStatus,
            @Param("profileCompleted") Boolean profileCompleted,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            Pageable pageable);

    // Analytics queries

    /**
     * Count profiles by completion status.
     */
    @Query("SELECT cp.profileCompleted, COUNT(cp) FROM CompleteProfile cp " +
           "WHERE cp.deleted = false " +
           "GROUP BY cp.profileCompleted")
    List<Object[]> countByCompletionStatus();

    /**
     * Count profiles by quality rating.
     */
    @Query("SELECT cp.profileQuality, COUNT(cp) FROM CompleteProfile cp " +
           "WHERE cp.deleted = false " +
           "GROUP BY cp.profileQuality")
    List<Object[]> countByProfileQuality();

    /**
     * Count profiles by verification status.
     */
    @Query("SELECT cp.verificationStatus, COUNT(cp) FROM CompleteProfile cp " +
           "WHERE cp.deleted = false " +
           "GROUP BY cp.verificationStatus")
    List<Object[]> countByVerificationStatus();

    /**
     * Get average completion percentage.
     */
    @Query("SELECT AVG(cp.completionPercentage) FROM CompleteProfile cp " +
           "WHERE cp.deleted = false")
    Double getAverageCompletionPercentage();

    /**
     * Get completion percentage distribution.
     */
    @Query("SELECT " +
           "CASE " +
           "WHEN cp.completionPercentage < 25 THEN '0-24' " +
           "WHEN cp.completionPercentage < 50 THEN '25-49' " +
           "WHEN cp.completionPercentage < 75 THEN '50-74' " +
           "WHEN cp.completionPercentage < 100 THEN '75-99' " +
           "ELSE '100' " +
           "END as range, COUNT(cp) " +
           "FROM CompleteProfile cp " +
           "WHERE cp.deleted = false " +
           "GROUP BY " +
           "CASE " +
           "WHEN cp.completionPercentage < 25 THEN '0-24' " +
           "WHEN cp.completionPercentage < 50 THEN '25-49' " +
           "WHEN cp.completionPercentage < 75 THEN '50-74' " +
           "WHEN cp.completionPercentage < 100 THEN '75-99' " +
           "ELSE '100' " +
           "END")
    List<Object[]> getCompletionPercentageDistribution();

    /**
     * Get profiles with highest completion scores.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "ORDER BY cp.completenessScore DESC")
    Page<CompleteProfile> findTopProfilesByScore(Pageable pageable);

    /**
     * Count total profiles.
     */
    @Query("SELECT COUNT(cp) FROM CompleteProfile cp WHERE cp.deleted = false")
    Long countTotalProfiles();

    /**
     * Count verified profiles.
     */
    @Query("SELECT COUNT(cp) FROM CompleteProfile cp " +
           "WHERE cp.deleted = false " +
           "AND cp.verificationStatus = 'VERIFIED'")
    Long countVerifiedProfiles();

    /**
     * Count complete profiles.
     */
    @Query("SELECT COUNT(cp) FROM CompleteProfile cp " +
           "WHERE cp.deleted = false " +
           "AND cp.profileCompleted = true")
    Long countCompleteProfiles();

    /**
     * Find public profiles for browsing (no authentication required).
     * Returns profiles that are marked as public and have sufficient completion.
     */
    @Query("SELECT cp FROM CompleteProfile cp " +
           "LEFT JOIN FETCH cp.user u " +
           "WHERE cp.deleted = false " +
           "AND cp.profileCompleted = true " +
           "AND cp.completionPercentage >= 60 " +
           "AND (cp.profileVisibility = 'PUBLIC' OR cp.profileVisibility IS NULL) " +
           "AND u.emailVerified = true " +
           "ORDER BY cp.completenessScore DESC, cp.updatedAt DESC")
    Page<CompleteProfile> findPublicProfiles(Pageable pageable);
}