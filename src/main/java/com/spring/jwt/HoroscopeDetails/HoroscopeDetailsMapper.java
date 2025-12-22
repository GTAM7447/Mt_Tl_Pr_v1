package com.spring.jwt.HoroscopeDetails;

import com.spring.jwt.dto.horoscope.HoroscopeCreateRequest;
import com.spring.jwt.dto.horoscope.HoroscopeResponse;
import com.spring.jwt.dto.horoscope.HoroscopeUpdateRequest;
import com.spring.jwt.entity.HoroscopeDetails;
import com.spring.jwt.entity.User;
import org.springframework.stereotype.Component;

@Component
public class HoroscopeDetailsMapper {

    public HoroscopeDetails toEntity(HoroscopeCreateRequest dto, User user) {
        if (dto == null) {
            return null;
        }

        HoroscopeDetails entity = new HoroscopeDetails();
        entity.setUser(user);
        entity.setDob(dto.getDob());
        entity.setTime(dto.getTime());
        entity.setBirthPlace(dto.getBirthPlace());
        entity.setRashi(dto.getRashi());
        entity.setNakshatra(dto.getNakshatra());
        entity.setCharan(dto.getCharan());
        entity.setNadi(dto.getNadi());
        entity.setGan(dto.getGan());
        entity.setMangal(dto.getMangal());
        entity.setGotra(dto.getGotra());
        entity.setDevak(dto.getDevak());
        return entity;
    }

    /**
     * Apply HoroscopeUpdateRequest to existing HoroscopeDetails entity (partial update).
     * Uses a functional approach to avoid repetitive if-else statements.
     *
     * @param request  the update request DTO
     * @param existing the existing entity to update
     */
    public void applyUpdate(HoroscopeUpdateRequest request, HoroscopeDetails existing) {
        if (request == null || existing == null) {
            return;
        }

        updateFieldIfNotNull(request.getDob(), existing::setDob);
        updateFieldIfNotNull(request.getTime(), existing::setTime);
        updateFieldIfNotNull(request.getBirthPlace(), existing::setBirthPlace);
        updateFieldIfNotNull(request.getRashi(), existing::setRashi);
        updateFieldIfNotNull(request.getNakshatra(), existing::setNakshatra);
        updateFieldIfNotNull(request.getCharan(), existing::setCharan);
        updateFieldIfNotNull(request.getNadi(), existing::setNadi);
        updateFieldIfNotNull(request.getGan(), existing::setGan);
        updateFieldIfNotNull(request.getMangal(), existing::setMangal);
        updateFieldIfNotNull(request.getGotra(), existing::setGotra);
        updateFieldIfNotNull(request.getDevak(), existing::setDevak);
    }

    /**
     * Utility method to update a field only if the new value is not null.
     * Eliminates repetitive if-else statements using functional programming.
     *
     * @param newValue the new value to set
     * @param setter   the setter method reference
     * @param <T>      the type of the field
     */
    private <T> void updateFieldIfNotNull(T newValue, java.util.function.Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }

    /**
     * Convert HoroscopeDetails entity to HoroscopeResponse DTO.
     *
     * @param entity the horoscope entity
     * @return HoroscopeResponse DTO
     */
    public HoroscopeResponse toResponse(HoroscopeDetails entity) {
        if (entity == null) {
            return null;
        }

        return HoroscopeResponse.builder()
                .horoscopeDetailsId(entity.getHoroscopeDetailsId())
                .version(entity.getVersion())
                .dob(entity.getDob())
                .time(entity.getTime())
                .birthPlace(entity.getBirthPlace())
                .rashi(entity.getRashi())
                .nakshatra(entity.getNakshatra())
                .charan(entity.getCharan())
                .nadi(entity.getNadi())
                .gan(entity.getGan())
                .mangal(entity.getMangal())
                .gotra(entity.getGotra())
                .devak(entity.getDevak())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }
}
