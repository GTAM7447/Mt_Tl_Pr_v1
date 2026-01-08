package com.spring.jwt.repository;

import com.spring.jwt.entity.Enums.Gender;
import com.spring.jwt.entity.Enums.Status;
import com.spring.jwt.entity.UserProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for UserProfile entity with optimized queries and ownership-based
 * access patterns.
 * All queries automatically filter out soft-deleted records via @Where clause
 * on entity.
 */
@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Integer> {

    /**
     * Find profile by user ID with user entity eagerly fetched to avoid N+1
     * queries.
     * Only returns non-deleted profiles.
     *
     * @param userId the user ID
     * @return Optional containing the profile if found and not deleted
     */
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.user.id = :userId AND up.deleted = false")
    Optional<UserProfile> findByUser_Id(@Param("userId") Integer userId);

    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.user.id = :userId AND up.deleted = false")
    Optional<UserProfile> findByUserId(@Param("userId") Integer userId);

    /**
     * Check if a profile exists for the given user ID (excluding deleted profiles).
     *
     * @param userId the user ID
     * @return true if profile exists and is not deleted
     */
    @Query("SELECT CASE WHEN COUNT(up) > 0 THEN true ELSE false END FROM UserProfile up WHERE up.user.id = :userId AND up.deleted = false")
    boolean existsByUser_Id(@Param("userId") Integer userId);

    /**
     * Find profiles by gender with pagination.
     * Eagerly fetches user to prevent N+1 queries.
     *
     * @param gender   the gender to filter by
     * @param pageable pagination information
     * @return page of profiles matching the gender
     */
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.gender = :gender AND up.deleted = false AND up.status = 'ACTIVE'")
    Page<UserProfile> findByGender(@Param("gender") Gender gender, Pageable pageable);

    /**
     * Find profiles by gender and status with pagination.
     *
     * @param gender   the gender to filter by
     * @param status   the status to filter by
     * @param pageable pagination information
     * @return page of profiles matching criteria
     */
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.gender = :gender AND up.status = :status AND up.deleted = false")
    Page<UserProfile> findByGenderAndStatus(@Param("gender") Gender gender, @Param("status") Status status,
            Pageable pageable);

    /**
     * Find profile by ID with user eagerly fetched.
     *
     * @param id the profile ID
     * @return Optional containing the profile if found
     */
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.userProfileId = :id AND up.deleted = false")
    Optional<UserProfile> findByIdWithUser(@Param("id") Integer id);

    /**
     * Find all profiles with user eagerly fetched for admin view.
     *
     * @param pageable pagination information
     * @return page of all profiles
     */
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE up.deleted = false")
    Page<UserProfile> findAllWithUser(Pageable pageable);

    /**
     * Search profiles with multiple criteria.
     *
     * @param gender   optional gender filter
     * @param religion optional religion filter
     * @param caste    optional caste filter
     * @param district optional district filter
     * @param minAge   optional minimum age
     * @param maxAge   optional maximum age
     * @param pageable pagination information
     * @return page of profiles matching criteria
     */
    @Query("SELECT up FROM UserProfile up JOIN FETCH up.user WHERE " +
            "(:gender IS NULL OR up.gender = :gender) AND " +
            "(:religion IS NULL OR up.religion = :religion) AND " +
            "(:caste IS NULL OR up.caste = :caste) AND " +
            "(:district IS NULL OR up.district = :district) AND " +
            "(:minAge IS NULL OR up.age >= :minAge) AND " +
            "(:maxAge IS NULL OR up.age <= :maxAge) AND " +
            "up.deleted = false AND up.status = 'ACTIVE'")
    Page<UserProfile> searchProfiles(
            @Param("gender") Gender gender,
            @Param("religion") String religion,
            @Param("caste") String caste,
            @Param("district") String district,
            @Param("minAge") Integer minAge,
            @Param("maxAge") Integer maxAge,
            Pageable pageable);

    /**
     * Count profiles by gender for statistics.
     *
     * @param gender the gender
     * @return count of profiles
     */
    @Query("SELECT COUNT(up) FROM UserProfile up WHERE up.gender = :gender AND up.deleted = false AND up.status = 'ACTIVE'")
    long countByGenderAndActiveStatus(@Param("gender") Gender gender);
}