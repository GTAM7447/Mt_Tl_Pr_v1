package com.spring.jwt.ExpressInterest.repository;

import com.spring.jwt.entity.ExpressInterest;
import com.spring.jwt.entity.Enums.InterestStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface ExpressInterestRepository extends JpaRepository<ExpressInterest, Long> {

    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.fromUser fu " +
           "JOIN FETCH ei.toUser tu " +
           "WHERE ei.interestId = :interestId AND ei.deleted = false")
    Optional<ExpressInterest> findByIdWithUsers(@Param("interestId") Long interestId);

    @Query("SELECT COUNT(ei) > 0 FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :fromUserId AND ei.toUser.id = :toUserId AND ei.deleted = false")
    boolean existsByFromUserIdAndToUserId(@Param("fromUserId") Integer fromUserId, 
                                         @Param("toUserId") Integer toUserId);

    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.toUser tu " +
           "WHERE ei.fromUser.id = :userId AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    Page<ExpressInterest> findSentInterestsByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.fromUser fu " +
           "WHERE ei.toUser.id = :userId AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    Page<ExpressInterest> findReceivedInterestsByUserId(@Param("userId") Integer userId, Pageable pageable);

    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.toUser tu " +
           "WHERE ei.fromUser.id = :userId AND ei.status = :status AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    Page<ExpressInterest> findSentInterestsByUserIdAndStatus(@Param("userId") Integer userId, 
                                                           @Param("status") InterestStatus status, 
                                                           Pageable pageable);

    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.fromUser fu " +
           "WHERE ei.toUser.id = :userId AND ei.status = :status AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    Page<ExpressInterest> findReceivedInterestsByUserIdAndStatus(@Param("userId") Integer userId, 
                                                               @Param("status") InterestStatus status, 
                                                               Pageable pageable);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :userId " +
           "AND DATE(ei.createdAt) = CURRENT_DATE " +
           "AND ei.deleted = false")
    Long countInterestsSentToday(@Param("userId") Integer userId);

    @Query("SELECT ei FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :userId " +
           "AND DATE(ei.createdAt) = CURRENT_DATE " +
           "AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    List<ExpressInterest> findInterestsSentToday(@Param("userId") Integer userId);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :userId AND ei.deleted = false")
    Long countTotalSentByUserId(@Param("userId") Integer userId);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.toUser.id = :userId AND ei.deleted = false")
    Long countTotalReceivedByUserId(@Param("userId") Integer userId);
    
    /**
     * Count sent interests by user ID
     */
    @Query("SELECT COUNT(ei) FROM ExpressInterest ei WHERE ei.fromUser.id = :userId AND ei.deleted = false")
    long countByFromUserId(@Param("userId") Integer userId);
    
    /**
     * Count received interests by user ID
     */
    @Query("SELECT COUNT(ei) FROM ExpressInterest ei WHERE ei.toUser.id = :userId AND ei.deleted = false")
    long countByToUserId(@Param("userId") Integer userId);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :userId AND ei.status = :status AND ei.deleted = false")
    Long countSentByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") InterestStatus status);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.toUser.id = :userId AND ei.status = :status AND ei.deleted = false")
    Long countReceivedByUserIdAndStatus(@Param("userId") Integer userId, @Param("status") InterestStatus status);


    @Query("SELECT AVG(ei.compatibilityScore) FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :userId AND ei.compatibilityScore IS NOT NULL AND ei.deleted = false")
    Double getAverageCompatibilityScoreForUser(@Param("userId") Integer userId);


    @Query("SELECT ei FROM ExpressInterest ei " +
           "WHERE ei.fromUser.id = :userId AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    List<ExpressInterest> findLastInterestSentByUser(@Param("userId") Integer userId, Pageable pageable);


    @Query("SELECT ei FROM ExpressInterest ei " +
           "WHERE ei.toUser.id = :userId AND ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    List<ExpressInterest> findLastInterestReceivedByUser(@Param("userId") Integer userId, Pageable pageable);


    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.fromUser fu " +
           "JOIN FETCH ei.toUser tu " +
           "WHERE ei.deleted = false " +
           "ORDER BY ei.createdAt DESC")
    Page<ExpressInterest> findAllWithUsers(Pageable pageable);


    @Query("SELECT ei FROM ExpressInterest ei " +
           "JOIN FETCH ei.fromUser fu " +
           "JOIN FETCH ei.toUser tu " +
           "WHERE ei.deleted = false " +
           "AND (:status IS NULL OR ei.status = :status) " +
           "AND (:fromUserId IS NULL OR ei.fromUser.id = :fromUserId) " +
           "AND (:toUserId IS NULL OR ei.toUser.id = :toUserId) " +
           "AND (:minCompatibility IS NULL OR ei.compatibilityScore >= :minCompatibility) " +
           "AND (:maxCompatibility IS NULL OR ei.compatibilityScore <= :maxCompatibility) " +
           "AND (:autoMatched IS NULL OR ei.autoMatched = :autoMatched) " +
           "AND (:sourcePlatform IS NULL OR ei.sourcePlatform = :sourcePlatform) " +
           "ORDER BY ei.createdAt DESC")
    Page<ExpressInterest> searchInterests(@Param("status") InterestStatus status,
                                        @Param("fromUserId") Integer fromUserId,
                                        @Param("toUserId") Integer toUserId,
                                        @Param("minCompatibility") Integer minCompatibility,
                                        @Param("maxCompatibility") Integer maxCompatibility,
                                        @Param("autoMatched") Boolean autoMatched,
                                        @Param("sourcePlatform") String sourcePlatform,
                                        Pageable pageable);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei WHERE ei.deleted = false")
    Long countTotalInterests();


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.status = :status AND ei.deleted = false")
    Long countByStatus(@Param("status") InterestStatus status);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE DATE(ei.createdAt) = CURRENT_DATE AND ei.deleted = false")
    Long countCreatedToday();


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.createdAt >= :startOfWeek AND ei.deleted = false")
    Long countCreatedThisWeek(@Param("startOfWeek") LocalDateTime startOfWeek);


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.createdAt >= :startOfMonth AND ei.deleted = false")
    Long countCreatedThisMonth(@Param("startOfMonth") LocalDateTime startOfMonth);


    @Query("SELECT " +
           "CAST(COUNT(CASE WHEN ei.status = 'ACCEPTED' THEN 1 END) AS DOUBLE) / " +
           "CAST(COUNT(CASE WHEN ei.status IN ('ACCEPTED', 'DECLINED') THEN 1 END) AS DOUBLE) * 100 " +
           "FROM ExpressInterest ei " +
           "WHERE ei.deleted = false")
    Double getOverallSuccessRate();


    @Query("SELECT " +
           "CAST(COUNT(CASE WHEN ei.status != 'PENDING' THEN 1 END) AS DOUBLE) / " +
           "CAST(COUNT(ei) AS DOUBLE) * 100 " +
           "FROM ExpressInterest ei " +
           "WHERE ei.deleted = false")
    Double getOverallResponseRate();


    @Query("SELECT AVG(ei.compatibilityScore) FROM ExpressInterest ei " +
           "WHERE ei.compatibilityScore IS NOT NULL AND ei.deleted = false")
    Double getAverageCompatibilityScore();


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.compatibilityScore >= 80 AND ei.deleted = false")
    Long countHighCompatibility();


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.compatibilityScore >= 60 AND ei.compatibilityScore < 80 AND ei.deleted = false")
    Long countMediumCompatibility();


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.compatibilityScore < 60 AND ei.deleted = false")
    Long countLowCompatibility();


    @Query("SELECT COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.compatibilityScore IS NULL AND ei.deleted = false")
    Long countUnknownCompatibility();


    @Modifying
    @Query("UPDATE ExpressInterest ei SET ei.status = 'EXPIRED', ei.updatedBy = :updatedBy " +
           "WHERE ei.status = 'PENDING' AND ei.expiresAt < :currentTime AND ei.deleted = false")
    int expireOldInterests(@Param("currentTime") LocalDateTime currentTime, 
                          @Param("updatedBy") Integer updatedBy);


    @Modifying
    @Query("UPDATE ExpressInterest ei SET ei.deleted = true, ei.deletedAt = :deletedAt, ei.deletedBy = :deletedBy " +
           "WHERE ei.createdAt < :cutoffDate AND ei.deleted = false")
    int softDeleteOldInterests(@Param("cutoffDate") LocalDateTime cutoffDate,
                              @Param("deletedAt") LocalDateTime deletedAt,
                              @Param("deletedBy") Integer deletedBy);


    @Query("SELECT ei.sourcePlatform, COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.deleted = false " +
           "GROUP BY ei.sourcePlatform")
    List<Object[]> countBySourcePlatform();


    @Query("SELECT HOUR(ei.createdAt), COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.deleted = false " +
           "GROUP BY HOUR(ei.createdAt) " +
           "ORDER BY HOUR(ei.createdAt)")
    List<Object[]> getHourlyPattern();


    @Query("SELECT DATE(ei.createdAt), COUNT(ei) FROM ExpressInterest ei " +
           "WHERE ei.createdAt >= :startDate AND ei.deleted = false " +
           "GROUP BY DATE(ei.createdAt) " +
           "ORDER BY DATE(ei.createdAt)")
    List<Object[]> getDailyPattern(@Param("startDate") LocalDateTime startDate);


    @Query("SELECT fu.email, fu.id, COUNT(ei) FROM ExpressInterest ei " +
           "JOIN ei.fromUser fu " +
           "WHERE ei.deleted = false " +
           "GROUP BY fu.id, fu.email " +
           "ORDER BY COUNT(ei) DESC")
    List<Object[]> getTopSenders(Pageable pageable);


    @Query("SELECT tu.email, tu.id, COUNT(ei) FROM ExpressInterest ei " +
           "JOIN ei.toUser tu " +
           "WHERE ei.deleted = false " +
           "GROUP BY tu.id, tu.email " +
           "ORDER BY COUNT(ei) DESC")
    List<Object[]> getTopReceivers(Pageable pageable);
}