package com.spring.jwt.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.spring.jwt.dto.ResponseAllUsersDto;
import com.spring.jwt.dto.UserDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.util.List;
import java.util.Map;

/**
 * This class intercepts responses containing UserDTO and decrypts sensitive data.
 * Only processes responses from user-related controllers to avoid unnecessary overhead.
 */
@ControllerAdvice(basePackages = {
    "com.spring.jwt.controller",
    "com.spring.jwt.admin"
})
@RequiredArgsConstructor
@Slf4j
public class DecryptionResponseProcessor implements ResponseBodyAdvice<Object> {

    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        Class<?> returnClass = returnType.getParameterType();

        if (returnClass.getName().contains("Document") ||
            returnClass.getName().contains("Horoscope") ||
            returnClass.getName().contains("Education") ||
            returnClass.getName().contains("Family") ||
            returnClass.getName().contains("Partner") ||
            returnClass.getName().contains("Contact") ||
            returnClass.getName().contains("Subscription") ||
            returnClass.getName().contains("ExpressInterest") ||
            returnClass.getName().contains("Profile") && !returnClass.getName().contains("UserProfile")) {
            return false;
        }

        return UserDTO.class.isAssignableFrom(returnClass) ||
               ResponseAllUsersDto.class.isAssignableFrom(returnClass) ||
               returnClass.getName().contains("User") ||
               List.class.isAssignableFrom(returnClass) ||
               Map.class.isAssignableFrom(returnClass);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        if (body == null) {
            return null;
        }

        try {
            return processResponse(body);
        } catch (Exception e) {
            log.warn("Error processing response for decryption: {}", e.getMessage());
            return body;
        }
    }

    private Object processResponse(Object body) {
        if (body == null) {
            return null;
        }

        String className = body.getClass().getName();
        if (className.contains("Document") || className.contains("Horoscope") ||
            className.contains("Education") || className.contains("Family") ||
            className.contains("Partner") || className.contains("Contact") ||
            className.contains("Subscription") || className.contains("ExpressInterest")) {
            return body;
        }

        if (body instanceof ResponseAllUsersDto) {
            ResponseAllUsersDto responseDto = (ResponseAllUsersDto) body;
            if (responseDto.getList() != null) {
                for (UserDTO user : responseDto.getList()) {
                    decryptUserDTO(user);
                }
            }
            return body;
        }

        if (body instanceof UserDTO) {
            decryptUserDTO((UserDTO) body);
            return body;
        }

        if (body instanceof List<?>) {
            List<?> list = (List<?>) body;
            for (Object item : list) {
                if (item instanceof UserDTO) {
                    decryptUserDTO((UserDTO) item);
                }
            }
            return body;
        }

        return body;
    }

    private void decryptUserDTO(UserDTO user) {
        // Decryption logic - currently disabled
        // Uncomment and implement when encryption is enabled
        /*
        try {
            if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
                user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
            }
            if (user.getLastName() != null && !user.getLastName().isEmpty()) {
                user.setLastName(encryptionUtil.decrypt(user.getLastName()));
            }
            if (user.getAddress() != null && !user.getAddress().isEmpty()) {
                user.setAddress(encryptionUtil.decrypt(user.getAddress()));
            }
        } catch (Exception e) {
            log.warn("Error decrypting user data for user: {}", user.getEmail());
        }
        */
    }
}