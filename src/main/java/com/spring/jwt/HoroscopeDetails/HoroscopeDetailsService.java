package com.spring.jwt.HoroscopeDetails;

import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;
import com.spring.jwt.dto.horoscope.HoroscopeUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for horoscope details management.
 * All operations are secured and scoped to the authenticated user's data.
 */
public interface HoroscopeDetailsService {
    
    /**
     * Create horoscope details for the current authenticated user.
     * 
     * @param request the horoscope creation request
     * @return created horoscope response
     * @throws ResourceAlreadyExistsException if horoscope already exists for user
     */
    HoroscopeResponse createForCurrentUser(HoroscopeCreateRequest request);

    /**
     * Create horoscope details for a specific user (Admin only).
     * 
     * @param userId the target user ID
     * @param request the horoscope creation request
     * @return created horoscope response
     * @throws ResourceAlreadyExistsException if horoscope already exists for user
     */
    HoroscopeResponse createForUser(Integer userId, HoroscopeCreateRequest request);

    /**
     * Get horoscope details for the current authenticated user.
     * 
     * @return current user's horoscope details
     * @throws ResourceNotFoundException if horoscope not found
     */
    HoroscopeResponse getCurrentUserHoroscope();

    /**
     * Get horoscope details by user ID (Admin only).
     * 
     * @param userId the user ID
     * @return horoscope details for the specified user
     * @throws ResourceNotFoundException if horoscope not found
     */
    HoroscopeResponse getByUserId(Integer userId);

    /**
     * Get all horoscope details with pagination (Admin only).
     * 
     * @param pageable pagination information
     * @return paginated horoscope details
     */
    Page<HoroscopeResponse> getAllHoroscopes(Pageable pageable);

    /**
     * Update horoscope details for the current authenticated user.
     * 
     * @param request the update request
     * @return updated horoscope response
     * @throws ResourceNotFoundException if horoscope not found
     */
    HoroscopeResponse updateCurrentUserHoroscope(HoroscopeUpdateRequest request);

    /**
     * Delete horoscope details for the current authenticated user.
     * 
     * @throws ResourceNotFoundException if horoscope not found
     */
    void deleteCurrentUserHoroscope();
}
