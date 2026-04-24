package com.spring.jwt.subscription.repository;

import com.spring.jwt.subscription.entity.ProfileView;
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
 * Repository for ProfileView entity.
 * Tracks profile views and contact reveals.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Repository
public interface ProfileViewRepository extends JpaRepository<ProfileView, Long> {

    /**
     * Find if viewer already viewed a profile
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.viewedProfile.id = :profileId")
    Optional<ProfileView> findByViewerAndProfile(
            @Param("viewerId") Integer viewerId,
            @Param("profileId") Integer profileId
    );

    /**
     * Get profiles viewed by user
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "ORDER BY pv.viewedAt DESC")
    Page<ProfileView> findByViewerId(@Param("viewerId") Integer viewerId, Pageable pageable);

    /**
     * Get who viewed a profile
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.viewedProfile.id = :profileId " +
           "ORDER BY pv.viewedAt DESC")
    Page<ProfileView> findByViewedProfileId(@Param("profileId") Integer profileId, Pageable pageable);

    /**
     * Count views for a profile
     */
    @Query("SELECT COUNT(pv) FROM ProfileView pv WHERE pv.viewedProfile.id = :profileId")
    long countViewsForProfile(@Param("profileId") Integer profileId);

    /**
     * Count profiles viewed by user
     */
    @Query("SELECT COUNT(pv) FROM ProfileView pv WHERE pv.viewer.id = :viewerId")
    long countProfilesViewedByUser(@Param("viewerId") Integer viewerId);

    /**
     * Find views within date range
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.viewedAt BETWEEN :startDate AND :endDate " +
           "ORDER BY pv.viewedAt DESC")
    List<ProfileView> findViewsInDateRange(
            @Param("viewerId") Integer viewerId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );

    /**
     * Find contact reveals by user
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.contactRevealed = true " +
           "ORDER BY pv.contactRevealedAt DESC")
    List<ProfileView> findContactRevealsByUser(@Param("viewerId") Integer viewerId);

    /**
     * Count contact reveals by user
     */
    @Query("SELECT COUNT(pv) FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.contactRevealed = true")
    long countContactRevealsByUser(@Param("viewerId") Integer viewerId);

    /**
     * Check if contact already revealed
     */
    @Query("SELECT CASE WHEN COUNT(pv) > 0 THEN true ELSE false END FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.viewedProfile.id = :profileId " +
           "AND pv.contactRevealed = true")
    boolean isContactRevealed(@Param("viewerId") Integer viewerId, @Param("profileId") Integer profileId);

    /**
     * Get recent views for a user
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "ORDER BY pv.viewedAt DESC")
    List<ProfileView> findRecentViewsByUser(@Param("viewerId") Integer viewerId, Pageable pageable);

    /**
     * Get view statistics for user
     */
    @Query("SELECT " +
           "COUNT(pv), " +
           "SUM(pv.creditsDeducted), " +
           "SUM(CASE WHEN pv.contactRevealed = true THEN 1 ELSE 0 END) " +
           "FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId")
    Object[] getViewStatisticsForUser(@Param("viewerId") Integer viewerId);

    /**
     * Find views by subscription
     */
    @Query("SELECT pv FROM ProfileView pv " +
           "WHERE pv.userSubscription.userSubscriptionId = :subscriptionId " +
           "ORDER BY pv.viewedAt DESC")
    List<ProfileView> findBySubscriptionId(@Param("subscriptionId") Integer subscriptionId);

    /**
     * Get daily view count for user
     */
    @Query("SELECT COUNT(pv) FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.viewedAt >= :startOfDay")
    long countDailyViewsForUser(@Param("viewerId") Integer viewerId, @Param("startOfDay") LocalDateTime startOfDay);

    /**
     * Get monthly view count for user
     */
    @Query("SELECT COUNT(pv) FROM ProfileView pv " +
           "WHERE pv.viewer.id = :viewerId " +
           "AND pv.viewedAt >= :startOfMonth")
    long countMonthlyViewsForUser(@Param("viewerId") Integer viewerId, @Param("startOfMonth") LocalDateTime startOfMonth);
}
