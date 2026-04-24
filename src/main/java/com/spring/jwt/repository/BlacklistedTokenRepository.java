package com.spring.jwt.repository;

import com.spring.jwt.entity.BlacklistedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository for managing blacklisted tokens in database.
 * 
 * @author Matrimony Platform
 * @version 1.0
 */
@Repository
public interface BlacklistedTokenRepository extends JpaRepository<BlacklistedToken, Long> {

    /**
     * Check if a token ID is blacklisted
     */
    boolean existsByTokenId(String tokenId);

    /**
     * Find blacklisted token by token ID
     */
    Optional<BlacklistedToken> findByTokenId(String tokenId);

    /**
     * Find all blacklisted tokens for a user
     */
    List<BlacklistedToken> findByUserEmail(String userEmail);

    /**
     * Delete expired tokens (cleanup)
     */
    @Modifying
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.expiresAt < :now")
    int deleteExpiredTokens(@Param("now") LocalDateTime now);

    /**
     * Count blacklisted tokens
     */
    @Query("SELECT COUNT(bt) FROM BlacklistedToken bt")
    long countBlacklistedTokens();

    /**
     * Count tokens with reuse attempts
     */
    @Query("SELECT COUNT(bt) FROM BlacklistedToken bt WHERE bt.reuseAttempts > 0")
    long countTokensWithReuseAttempts();

    /**
     * Find tokens blacklisted for a specific reason
     */
    List<BlacklistedToken> findByReason(String reason);

    /**
     * Delete all tokens for a user (for account deletion)
     */
    @Modifying
    @Query("DELETE FROM BlacklistedToken bt WHERE bt.userEmail = :userEmail")
    int deleteByUserEmail(@Param("userEmail") String userEmail);
}
