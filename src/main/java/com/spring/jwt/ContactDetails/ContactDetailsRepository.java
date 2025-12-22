package com.spring.jwt.ContactDetails;

import com.spring.jwt.entity.ContactDetails;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for ContactDetails entity.
 * Provides enterprise-grade data access methods with optimized queries.
 */
@Repository
public interface ContactDetailsRepository extends JpaRepository<ContactDetails, Integer> {

    /**
     * Find contact details by user ID.
     *
     * @param userId the user ID
     * @return optional contact details
     */
    Optional<ContactDetails> findByUser_Id(Integer userId);

    /**
     * Check if contact details exist for a user.
     *
     * @param userId the user ID
     * @return true if exists
     */
    boolean existsByUser_Id(Integer userId);

    /**
     * Find contact details by mobile number.
     *
     * @param mobileNumber the mobile number
     * @return optional contact details
     */
    Optional<ContactDetails> findByMobileNumber(String mobileNumber);

    /**
     * Check if mobile number exists (excluding specific user).
     *
     * @param mobileNumber the mobile number
     * @param userId the user ID to exclude
     * @return true if exists
     */
    boolean existsByMobileNumberAndUser_IdNot(String mobileNumber, Integer userId);

    /**
     * Find all contact details with user information (for admin).
     *
     * @param pageable pagination information
     * @return page of contact details
     */
    @Query("SELECT cd FROM ContactDetails cd JOIN FETCH cd.user u ORDER BY cd.createdAt DESC")
    Page<ContactDetails> findAllWithUser(Pageable pageable);

    /**
     * Search contact details by city.
     *
     * @param city the city name
     * @param pageable pagination information
     * @return page of contact details
     */
    @Query("SELECT cd FROM ContactDetails cd WHERE LOWER(cd.city) LIKE LOWER(CONCAT('%', :city, '%'))")
    Page<ContactDetails> findByCityContainingIgnoreCase(@Param("city") String city, Pageable pageable);

    /**
     * Search contact details by state.
     *
     * @param state the state name
     * @param pageable pagination information
     * @return page of contact details
     */
    @Query("SELECT cd FROM ContactDetails cd WHERE LOWER(cd.state) LIKE LOWER(CONCAT('%', :state, '%'))")
    Page<ContactDetails> findByStateContainingIgnoreCase(@Param("state") String state, Pageable pageable);

    /**
     * Search contact details by country.
     *
     * @param country the country name
     * @param pageable pagination information
     * @return page of contact details
     */
    @Query("SELECT cd FROM ContactDetails cd WHERE LOWER(cd.country) LIKE LOWER(CONCAT('%', :country, '%'))")
    Page<ContactDetails> findByCountryContainingIgnoreCase(@Param("country") String country, Pageable pageable);

    /**
     * Search contact details by multiple location criteria.
     *
     * @param city the city name (optional)
     * @param state the state name (optional)
     * @param country the country name (optional)
     * @param pageable pagination information
     * @return page of contact details
     */
    @Query("SELECT cd FROM ContactDetails cd WHERE " +
           "(:city IS NULL OR LOWER(cd.city) LIKE LOWER(CONCAT('%', :city, '%'))) AND " +
           "(:state IS NULL OR LOWER(cd.state) LIKE LOWER(CONCAT('%', :state, '%'))) AND " +
           "(:country IS NULL OR LOWER(cd.country) LIKE LOWER(CONCAT('%', :country, '%')))")
    Page<ContactDetails> searchByLocation(@Param("city") String city, 
                                         @Param("state") String state, 
                                         @Param("country") String country, 
                                         Pageable pageable);

    /**
     * Find contact details by PIN code.
     *
     * @param pinCode the PIN code
     * @param pageable pagination information
     * @return page of contact details
     */
    Page<ContactDetails> findByPinCode(String pinCode, Pageable pageable);

    /**
     * Find contact details by contact visibility.
     *
     * @param visibility the visibility setting
     * @param pageable pagination information
     * @return page of contact details
     */
    Page<ContactDetails> findByContactVisibility(String visibility, Pageable pageable);

    /**
     * Find verified mobile contacts.
     *
     * @param isVerified the verification status
     * @param pageable pagination information
     * @return page of contact details
     */
    Page<ContactDetails> findByIsVerifiedMobile(Boolean isVerified, Pageable pageable);

    /**
     * Find verified email contacts.
     *
     * @param isVerified the verification status
     * @param pageable pagination information
     * @return page of contact details
     */
    Page<ContactDetails> findByIsVerifiedEmail(Boolean isVerified, Pageable pageable);

    /**
     * Count total contact details.
     *
     * @return total count
     */
    @Query("SELECT COUNT(cd) FROM ContactDetails cd")
    long countTotal();

    /**
     * Count verified mobile numbers.
     *
     * @return count of verified mobile numbers
     */
    @Query("SELECT COUNT(cd) FROM ContactDetails cd WHERE cd.isVerifiedMobile = true")
    long countVerifiedMobile();

    /**
     * Count verified email addresses.
     *
     * @return count of verified email addresses
     */
    @Query("SELECT COUNT(cd) FROM ContactDetails cd WHERE cd.isVerifiedEmail = true")
    long countVerifiedEmail();

    /**
     * Count by country.
     *
     * @param country the country name
     * @return count for the country
     */
    long countByCountryIgnoreCase(String country);

    /**
     * Count by contact visibility.
     *
     * @param visibility the visibility setting
     * @return count for the visibility setting
     */
    long countByContactVisibility(String visibility);
}
