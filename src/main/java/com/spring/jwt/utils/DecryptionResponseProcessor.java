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
 * This class intercepts all responses from controllers and ensures sensitive
 * data is decrypted
 */
@ControllerAdvice
@RequiredArgsConstructor
@Slf4j
public class DecryptionResponseProcessor implements ResponseBodyAdvice<Object> {

    private final EncryptionUtil encryptionUtil;
    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {

        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
            Class<? extends HttpMessageConverter<?>> selectedConverterType,
            ServerHttpRequest request, ServerHttpResponse response) {

        try {
            log.debug("Processing response for decryption: {}", body.getClass().getName());
            return processResponse(body);
        } catch (Exception e) {
            log.error("Error processing response for decryption: {}", e.getMessage(), e);
            return body;
        }
    }

    private Object processResponse(Object body) {
        if (body == null) {
            return null;
        }

        // PERF: Skip expensive graph traversal for DocumentResponse classes
        if (body.getClass().getName().contains("DocumentResponseDTO") ||
                body.getClass().getName().contains("DocumentDetailResponseDTO") ||
                body.getClass().getName().contains("PaginatedDocumentResponseDTO")) {
            return body;
        }

        // PERF: Handle ApiResponse wrapper containing Document types
        if (body instanceof ApiResponse) {
            ApiResponse<?> response = (ApiResponse<?>) body;
            Object data = response.getData();
            if (data != null) {
                String className = data.getClass().getName();
                if (className.contains("DocumentResponseDTO") ||
                        className.contains("DocumentDetailResponseDTO") ||
                        className.contains("PaginatedDocumentResponseDTO")) {
                    return body;
                }
            }
        }

        if (body instanceof ResponseAllUsersDto) {
            ResponseAllUsersDto responseDto = (ResponseAllUsersDto) body;
            if (responseDto.getList() != null) {
                log.debug("Processing ResponseAllUsersDto with {} items", responseDto.getList().size());
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
                } else {
                    processResponse(item);
                }
            }
            return body;
        }

        if (body instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) body;
            for (Object value : map.values()) {
                if (value instanceof UserDTO) {
                    decryptUserDTO((UserDTO) value);
                } else if (value instanceof List) {
                    processResponse(value);
                } else if (value instanceof Map) {
                    processResponse(value);
                }
            }
            return body;
        }

        try {
            Map<String, Object> objectMap = objectMapper.convertValue(body, Map.class);

            for (String key : objectMap.keySet()) {
                Object value = objectMap.get(key);

                if (value instanceof Map || value instanceof List) {
                    processResponse(value);
                }
            }

            if (objectMap.containsKey("list")) {
                Object listObj = objectMap.get("list");
                if (listObj instanceof List) {
                    log.debug("Found 'list' field in response object, processing it");
                    processResponse(listObj);
                }
            }

            return body;
        } catch (Exception e) {
            log.debug("Could not process complex object for decryption: {}", e.getMessage());
            return body;
        }
    }

    private void decryptUserDTO(UserDTO user) {
        // try {
        // if (user.getFirstName() != null && !user.getFirstName().isEmpty()) {
        // user.setFirstName(encryptionUtil.decrypt(user.getFirstName()));
        // }
        //
        // if (user.getLastName() != null && !user.getLastName().isEmpty()) {
        // user.setLastName(encryptionUtil.decrypt(user.getLastName()));
        // }
        //
        // if (user.getAddress() != null && !user.getAddress().isEmpty()) {
        // user.setAddress(encryptionUtil.decrypt(user.getAddress()));
        // }
        // } catch (Exception e) {
        // log.error("Error decrypting user data: {}", e.getMessage());
        // }
    }
}