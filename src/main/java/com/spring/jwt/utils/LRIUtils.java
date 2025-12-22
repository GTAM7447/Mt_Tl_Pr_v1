package com.spring.jwt.utils;

import com.spring.jwt.entity.Role;
import com.spring.jwt.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class LRIUtils {

    private final RoleRepository roleRepository;

    public LRIUtils(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    @Transactional
    public void initRoles() {
        Set<String> rolesToAdd = Set.of("USER", "ADMIN");

        Set<String> existingRoles = roleRepository.findAll()
                .stream()
                .map(Role::getName)
                .collect(Collectors.toSet());

        rolesToAdd.stream()
                .filter(role -> !existingRoles.contains(role))
                .forEach(role -> {
                    roleRepository.save(new Role(role));
                    System.out.println("Added Role: " + role);
                });

        existingRoles.stream()
                .filter(role -> !rolesToAdd.contains(role))
                .forEach(role -> {
                    roleRepository.deleteByName(role);
                    System.out.println("Removed Role: " + role);
                });
    }
}
