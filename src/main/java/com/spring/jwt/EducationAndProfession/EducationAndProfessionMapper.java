package com.spring.jwt.EducationAndProfession;

import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionCreateRequest;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionResponse;
import com.spring.jwt.EducationAndProfession.dto.EducationAndProfessionUpdateRequest;
import com.spring.jwt.entity.EducationAndProfession;
import com.spring.jwt.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

/**
 * Mapper for EducationAndProfession entity and DTOs.
 * Uses functional programming approach to eliminate repetitive code.
 */
@Component
@RequiredArgsConstructor
public class EducationAndProfessionMapper {

    /**
     * Convert create request to entity.
     *
     * @param request the create request
     * @param user    the associated user
     * @return the entity
     */
    public EducationAndProfession toEntity(EducationAndProfessionCreateRequest request, User user) {
        EducationAndProfession entity = new EducationAndProfession();
        
        entity.setEducation(request.getEducation());
        entity.setDegree(request.getDegree());
        entity.setOccupation(request.getOccupation());
        entity.setOccupationDetails(request.getOccupationDetails());
        entity.setIncomePerYear(request.getIncomePerYear());
        entity.setAdditionalDetails(request.getAdditionalDetails());
        entity.setWorkLocation(request.getWorkLocation());
        entity.setCompanyName(request.getCompanyName());
        entity.setExperienceYears(request.getExperienceYears());
        entity.setUser(user);
        
        return entity;
    }

    /**
     * Convert entity to response DTO.
     *
     * @param entity the entity
     * @return the response DTO
     */
    public EducationAndProfessionResponse toResponse(EducationAndProfession entity) {
        return EducationAndProfessionResponse.builder()
                .educationId(entity.getEducationId())
                .education(entity.getEducation())
                .degree(entity.getDegree())
                .occupation(entity.getOccupation())
                .occupationDetails(entity.getOccupationDetails())
                .incomePerYear(entity.getIncomePerYear())
                .additionalDetails(entity.getAdditionalDetails())
                .workLocation(entity.getWorkLocation())
                .companyName(entity.getCompanyName())
                .experienceYears(entity.getExperienceYears())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .version(entity.getVersion())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    /**
     * Apply partial updates from update request to existing entity.
     * Uses functional programming approach to eliminate repetitive if-else statements.
     *
     * @param request  the update request
     * @param existing the existing entity
     */
    public void applyUpdate(EducationAndProfessionUpdateRequest request, EducationAndProfession existing) {
        updateFieldIfNotNull(request.getEducation(), existing::setEducation);
        updateFieldIfNotNull(request.getDegree(), existing::setDegree);
        updateFieldIfNotNull(request.getOccupation(), existing::setOccupation);
        updateFieldIfNotNull(request.getOccupationDetails(), existing::setOccupationDetails);
        updateFieldIfNotNull(request.getIncomePerYear(), existing::setIncomePerYear);
        updateFieldIfNotNull(request.getAdditionalDetails(), existing::setAdditionalDetails);
        updateFieldIfNotNull(request.getWorkLocation(), existing::setWorkLocation);
        updateFieldIfNotNull(request.getCompanyName(), existing::setCompanyName);
        updateFieldIfNotNull(request.getExperienceYears(), existing::setExperienceYears);
    }

    /**
     * Functional utility method to update field only if new value is not null.
     * Eliminates repetitive if-else statements.
     *
     * @param newValue the new value (can be null)
     * @param setter   the setter method reference
     * @param <T>      the type of the field
     */
    private <T> void updateFieldIfNotNull(T newValue, Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
