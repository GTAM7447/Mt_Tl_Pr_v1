package com.spring.jwt.repository;

import com.spring.jwt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    User findByEmail(String email);

    User findByResetPasswordToken(String token);

    Optional<User> findByMobileNumber(@Param("mobileNumber") Long mobileNumber);
    
    @Query(value = "SELECT * FROM users WHERE user_id = :id", nativeQuery = true)
    Map<String, Object> findRawUserById(@Param("id") Long id);
    
    /**
     * Check if email or mobile number already exists - single query for validation
     */
    @Query("SELECT CASE WHEN COUNT(u) > 0 THEN true ELSE false END FROM User u WHERE u.email = :email OR u.mobileNumber = :mobileNumber")
    boolean existsByEmailOrMobileNumber(@Param("email") String email, @Param("mobileNumber") Long mobileNumber);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if mobile number exists
     */
    boolean existsByMobileNumber(Long mobileNumber);
    
    /**
     * Fetch all users with roles in a single query to avoid N+1 problem
     */
    @Query(value = "SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.roles",
           countQuery = "SELECT COUNT(u) FROM User u")
    Page<User> findAllWithRoles(Pageable pageable);
    
    /**
     * Fetch user by email with roles eagerly loaded
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.email = :email")
    User findByEmailWithRoles(@Param("email") String email);
    
    /**
     * Fetch user by ID with roles eagerly loaded
     */
    @Query("SELECT u FROM User u LEFT JOIN FETCH u.roles WHERE u.id = :id")
    Optional<User> findByIdWithRoles(@Param("id") Integer id);
    
    /**
     * Update last login timestamp without fetching the entire user entity
     */
    @Modifying
    @Query("UPDATE User u SET u.lastLogin = :lastLogin, u.updatedAt = :updatedAt WHERE u.email = :email")
    void updateLastLoginByEmail(@Param("email") String email, @Param("lastLogin") LocalDateTime lastLogin, @Param("updatedAt") LocalDateTime updatedAt);
}
