package com.spring.jwt.mapper;

import com.spring.jwt.dto.UserDTO;
import com.spring.jwt.entity.Role;
import com.spring.jwt.entity.User;
import com.spring.jwt.utils.EncryptionUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserMapper {

    private final EncryptionUtil encryptionUtil;

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        try {
            UserDTO dto = new UserDTO();
            dto.setEmail(user.getEmail());
            dto.setUserId(user.getId());
            
            // Set the roles as a Set<String>
            if (user.getRoles() != null) {
                dto.setRoles(user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toSet()));
            }

//            if (user.getFirstName() != null) {
//                String decrypted = ensureDecrypted(user.getFirstName());
//                dto.setFirstName(decrypted);
//                log.debug("Mapped firstName: {} -> {}", user.getFirstName().substring(0, Math.min(10, user.getFirstName().length())),
//                         decrypted.substring(0, Math.min(10, decrypted.length())));
//            }
//
//            if (user.getLastName() != null) {
//                String decrypted = ensureDecrypted(user.getLastName());
//                dto.setLastName(decrypted);
//                log.debug("Mapped lastName: {} -> {}", user.getLastName().substring(0, Math.min(10, user.getLastName().length())),
//                         decrypted.substring(0, Math.min(10, decrypted.length())));
//            }
//
//            if (user.getAddress() != null) {
//                String decrypted = ensureDecrypted(user.getAddress());
//                dto.setAddress(decrypted);
//                log.debug("Mapped address: {} -> {}", user.getAddress().substring(0, Math.min(10, user.getAddress().length())),
//                         decrypted.substring(0, Math.min(10, decrypted.length())));
//            }

            
            dto.setMobileNumber(user.getMobileNumber());
            
            log.debug("Mapped User to DTO - email: {}", dto.getEmail());
            return dto;
        } catch (Exception e) {
            log.error("Error converting User to DTO: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting User to DTO", e);
        }
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        try {
            User user = new User();
            user.setEmail(dto.getEmail());
//            user.setFirstName(dto.getFirstName());
//            user.setLastName(dto.getLastName());
//            user.setAddress(dto.getAddress());
            user.setMobileNumber(dto.getMobileNumber());
            
            return user;
        } catch (Exception e) {
            log.error("Error converting DTO to User: {}", e.getMessage(), e);
            throw new RuntimeException("Error converting DTO to User", e);
        }
    }
    
    /**
     * Ensures a string is decrypted if it appears to be encrypted
     */
    private String ensureDecrypted(String value) {
        if (value == null || value.isEmpty()) {
            return value;
        }
        
        try {
            String decrypted = encryptionUtil.decrypt(value);
            log.debug("Decryption result: {} chars -> {} chars", value.length(), decrypted.length());
            return decrypted;
        } catch (Exception e) {
            log.warn("Decryption failed, returning original value: {}", e.getMessage());
            return value;
        }
    }
} 