package com.spring.jwt.repository;

import com.spring.jwt.entity.Role;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    
    @Cacheable(value = "roles", key = "#name")
    Role findByName(String name);

    void deleteByName(String role);
    
    @Cacheable(value = "roleExists", key = "#name")
    boolean existsByName(String name);
}
