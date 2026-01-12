package com.spring.jwt.repository;

import com.spring.jwt.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

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
}
