package com.spring.jwt.FamilyBackground;

import com.spring.jwt.entity.FamilyBackground;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for FamilyBackground entity.
 * Includes methods that respect soft delete functionality.
 */
@Repository
public interface FamilyBackgroundRepository extends JpaRepository<FamilyBackground, Integer> {
    
    /**
     * Check if family background exists for user (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    boolean existsByUser_Id(Integer userId);

    /**
     * Find family background by user ID (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    Optional<FamilyBackground> findByUser_Id(Integer userId);

    /**
     * Find all family backgrounds with pagination (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    @Override
    Page<FamilyBackground> findAll(Pageable pageable);

    /**
     * Find family background by ID including soft deleted records (admin use).
     * Bypasses the @Where clause for administrative purposes.
     */
    @Query("SELECT fb FROM FamilyBackground fb WHERE fb.familyBackgroundId = :id")
    Optional<FamilyBackground> findByIdIncludingDeleted(@Param("id") Integer id);

    /**
     * Find family background by user ID including soft deleted records (admin use).
     * Bypasses the @Where clause for administrative purposes.
     */
    @Query("SELECT fb FROM FamilyBackground fb WHERE fb.user.id = :userId")
    Optional<FamilyBackground> findByUserIdIncludingDeleted(@Param("userId") Integer userId);

    /**
     * Count active (non-deleted) family backgrounds.
     */
    @Query("SELECT COUNT(fb) FROM FamilyBackground fb WHERE fb.deleted = false")
    long countActiveFamilyBackgrounds();

    /**
     * Count soft deleted family backgrounds.
     */
    @Query("SELECT COUNT(fb) FROM FamilyBackground fb WHERE fb.deleted = true")
    long countDeletedFamilyBackgrounds();
}
