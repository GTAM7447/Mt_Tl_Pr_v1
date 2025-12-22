package com.spring.jwt.HoroscopeDetails;

import com.spring.jwt.entity.HoroscopeDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

/**
 * Repository interface for HoroscopeDetails entity.
 * Includes methods that respect soft delete functionality.
 */
public interface HoroscopeDetailsRepository extends JpaRepository<HoroscopeDetails, Integer> {
    
    /**
     * Check if horoscope exists for user (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    boolean existsByUser_Id(Integer userId);

    /**
     * Find horoscope by user ID (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    Optional<HoroscopeDetails> findByUser_Id(Integer userId);

    /**
     * Find all horoscopes with user details (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    @Query("SELECT h FROM HoroscopeDetails h JOIN FETCH h.user")
    java.util.List<HoroscopeDetails> findAllWithUser();

    /**
     * Find all horoscopes with pagination (excluding soft deleted records).
     * The @Where annotation on entity automatically filters deleted records.
     */
    @Override
    Page<HoroscopeDetails> findAll(Pageable pageable);

    /**
     * Find horoscope by ID including soft deleted records (admin use).
     * Bypasses the @Where clause for administrative purposes.
     */
    @Query("SELECT h FROM HoroscopeDetails h WHERE h.horoscopeDetailsId = :id")
    Optional<HoroscopeDetails> findByIdIncludingDeleted(@Param("id") Integer id);

    /**
     * Find horoscope by user ID including soft deleted records (admin use).
     * Bypasses the @Where clause for administrative purposes.
     */
    @Query("SELECT h FROM HoroscopeDetails h WHERE h.user.id = :userId")
    Optional<HoroscopeDetails> findByUserIdIncludingDeleted(@Param("userId") Integer userId);

    /**
     * Count active (non-deleted) horoscopes.
     */
    @Query("SELECT COUNT(h) FROM HoroscopeDetails h WHERE h.deleted = false")
    long countActiveHoroscopes();

    /**
     * Count soft deleted horoscopes.
     */
    @Query("SELECT COUNT(h) FROM HoroscopeDetails h WHERE h.deleted = true")
    long countDeletedHoroscopes();
}
