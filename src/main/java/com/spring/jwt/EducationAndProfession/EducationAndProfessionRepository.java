package com.spring.jwt.EducationAndProfession;

import com.spring.jwt.entity.EducationAndProfession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for EducationAndProfession entity.
 * Provides data access methods with proper query optimization.
 */
@Repository
public interface EducationAndProfessionRepository extends JpaRepository<EducationAndProfession, Integer> {

    /**
     * Check if education and profession exists for a user.
     *
     * @param userId the user ID
     * @return true if exists
     */
    boolean existsByUser_Id(Integer userId);

    /**
     * Find education and profession by user ID.
     *
     * @param userId the user ID
     * @return optional education and profession
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE ep.user.id = :userId")
    Optional<EducationAndProfession> findByUser_Id(@Param("userId") Integer userId);

    /**
     * Find education and profession by ID with user information.
     *
     * @param id the education and profession ID
     * @return optional education and profession with user
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE ep.educationId = :id")
    Optional<EducationAndProfession> findByIdWithUser(@Param("id") Integer id);

    /**
     * Find all education and profession records with pagination and user information.
     *
     * @param pageable pagination information
     * @return page of education and profession records
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user")
    Page<EducationAndProfession> findAllWithUser(Pageable pageable);

    /**
     * Find education and profession records by occupation.
     *
     * @param occupation the occupation
     * @param pageable   pagination information
     * @return page of education and profession records
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE LOWER(ep.occupation) LIKE LOWER(CONCAT('%', :occupation, '%'))")
    Page<EducationAndProfession> findByOccupationContainingIgnoreCase(@Param("occupation") String occupation, Pageable pageable);

    /**
     * Find education and profession records by education level.
     *
     * @param education the education level
     * @param pageable  pagination information
     * @return page of education and profession records
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE LOWER(ep.education) LIKE LOWER(CONCAT('%', :education, '%'))")
    Page<EducationAndProfession> findByEducationContainingIgnoreCase(@Param("education") String education, Pageable pageable);

    /**
     * Find education and profession records by income range.
     *
     * @param minIncome minimum income
     * @param maxIncome maximum income
     * @param pageable  pagination information
     * @return page of education and profession records
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE ep.incomePerYear BETWEEN :minIncome AND :maxIncome")
    Page<EducationAndProfession> findByIncomeRange(@Param("minIncome") Integer minIncome, 
                                                   @Param("maxIncome") Integer maxIncome, 
                                                   Pageable pageable);

    /**
     * Find education and profession records by work location.
     *
     * @param location the work location
     * @param pageable pagination information
     * @return page of education and profession records
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE LOWER(ep.workLocation) LIKE LOWER(CONCAT('%', :location, '%'))")
    Page<EducationAndProfession> findByWorkLocationContainingIgnoreCase(@Param("location") String location, Pageable pageable);

    /**
     * Count education and profession records by occupation.
     *
     * @param occupation the occupation
     * @return count of records
     */
    @Query("SELECT COUNT(ep) FROM EducationAndProfession ep WHERE LOWER(ep.occupation) LIKE LOWER(CONCAT('%', :occupation, '%'))")
    long countByOccupationContainingIgnoreCase(@Param("occupation") String occupation);

    /**
     * Count education and profession records by education level.
     *
     * @param education the education level
     * @return count of records
     */
    @Query("SELECT COUNT(ep) FROM EducationAndProfession ep WHERE LOWER(ep.education) LIKE LOWER(CONCAT('%', :education, '%'))")
    long countByEducationContainingIgnoreCase(@Param("education") String education);

    /**
     * Find education and profession records with complex search criteria.
     *
     * @param occupation    the occupation (optional)
     * @param education     the education level (optional)
     * @param minIncome     minimum income (optional)
     * @param maxIncome     maximum income (optional)
     * @param workLocation  work location (optional)
     * @param pageable      pagination information
     * @return page of matching records
     */
    @Query("SELECT ep FROM EducationAndProfession ep JOIN FETCH ep.user WHERE " +
           "(:occupation IS NULL OR LOWER(ep.occupation) LIKE LOWER(CONCAT('%', :occupation, '%'))) AND " +
           "(:education IS NULL OR LOWER(ep.education) LIKE LOWER(CONCAT('%', :education, '%'))) AND " +
           "(:minIncome IS NULL OR ep.incomePerYear >= :minIncome) AND " +
           "(:maxIncome IS NULL OR ep.incomePerYear <= :maxIncome) AND " +
           "(:workLocation IS NULL OR LOWER(ep.workLocation) LIKE LOWER(CONCAT('%', :workLocation, '%')))")
    Page<EducationAndProfession> searchEducationAndProfession(
            @Param("occupation") String occupation,
            @Param("education") String education,
            @Param("minIncome") Integer minIncome,
            @Param("maxIncome") Integer maxIncome,
            @Param("workLocation") String workLocation,
            Pageable pageable);
}
