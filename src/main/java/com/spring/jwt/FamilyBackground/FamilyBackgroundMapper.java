package com.spring.jwt.FamilyBackground;

import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundCreateRequest;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundResponse;
import com.spring.jwt.FamilyBackground.dto.FamilyBackgroundUpdateRequest;
import com.spring.jwt.entity.FamilyBackground;
import com.spring.jwt.entity.User;
import org.springframework.stereotype.Component;

/**
 * Mapper for converting between FamilyBackground entities and DTOs.
 * Handles entity-DTO transformations with null safety and functional approach.
 * Input sanitization is handled globally by Spring Security.
 */
@Component
public class FamilyBackgroundMapper
{

    public FamilyBackground toEntity(FamilyBackgroundCreateRequest dto, User user)
    {
        if (dto == null)
        {
            return null;
        }
        
        FamilyBackground entity = new FamilyBackground();
        entity.setUser(user);
        entity.setFathersName(dto.getFathersName());
        entity.setFatherOccupation(dto.getFatherOccupation());
        entity.setMothersName(dto.getMothersName());
        entity.setMotherOccupation(dto.getMotherOccupation());
        entity.setBrother(dto.getBrother());
        entity.setMarriedBrothers(dto.getMarriedBrothers());
        entity.setSisters(dto.getSisters());
        entity.setMarriedSisters(dto.getMarriedSisters());
        entity.setInterCasteInFamily(dto.getInterCasteInFamily());
        entity.setParentResiding(dto.getParentResiding());
        entity.setFamilyWealth(dto.getFamilyWealth());
        entity.setMamaSurname(dto.getMamaSurname());
        entity.setMamaPlace(dto.getMamaPlace());
        entity.setFamilyBackgroundCol(dto.getFamilyBackgroundCol());
        entity.setRelativeSurnames(dto.getRelativeSurnames());
        
        return entity;
    }

    public void applyUpdate(FamilyBackgroundUpdateRequest request, FamilyBackground existing)
    {
        if (request == null || existing == null)
        {
            return;
        }

        updateFieldIfNotNull(request.getFathersName(), existing::setFathersName);
        updateFieldIfNotNull(request.getFatherOccupation(), existing::setFatherOccupation);
        updateFieldIfNotNull(request.getMothersName(), existing::setMothersName);
        updateFieldIfNotNull(request.getMotherOccupation(), existing::setMotherOccupation);
        updateFieldIfNotNull(request.getBrother(), existing::setBrother);
        updateFieldIfNotNull(request.getMarriedBrothers(), existing::setMarriedBrothers);
        updateFieldIfNotNull(request.getSisters(), existing::setSisters);
        updateFieldIfNotNull(request.getMarriedSisters(), existing::setMarriedSisters);
        updateFieldIfNotNull(request.getInterCasteInFamily(), existing::setInterCasteInFamily);
        updateFieldIfNotNull(request.getParentResiding(), existing::setParentResiding);
        updateFieldIfNotNull(request.getFamilyWealth(), existing::setFamilyWealth);
        updateFieldIfNotNull(request.getMamaSurname(), existing::setMamaSurname);
        updateFieldIfNotNull(request.getMamaPlace(), existing::setMamaPlace);
        updateFieldIfNotNull(request.getFamilyBackgroundCol(), existing::setFamilyBackgroundCol);
        updateFieldIfNotNull(request.getRelativeSurnames(), existing::setRelativeSurnames);
    }

    public FamilyBackgroundResponse toResponse(FamilyBackground entity) {
        if (entity == null) {
            return null;
        }
        
        return FamilyBackgroundResponse.builder()
                .familyBackgroundId(entity.getFamilyBackgroundId())
                .version(entity.getVersion())
                .fathersName(entity.getFathersName())
                .fatherOccupation(entity.getFatherOccupation())
                .mothersName(entity.getMothersName())
                .motherOccupation(entity.getMotherOccupation())
                .brother(entity.getBrother())
                .marriedBrothers(entity.getMarriedBrothers())
                .sisters(entity.getSisters())
                .marriedSisters(entity.getMarriedSisters())
                .interCasteInFamily(entity.getInterCasteInFamily())
                .parentResiding(entity.getParentResiding())
                .familyWealth(entity.getFamilyWealth())
                .mamaSurname(entity.getMamaSurname())
                .mamaPlace(entity.getMamaPlace())
                .familyBackgroundCol(entity.getFamilyBackgroundCol())
                .relativeSurnames(entity.getRelativeSurnames())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .createdBy(entity.getCreatedBy())
                .updatedBy(entity.getUpdatedBy())
                .build();
    }

    private <T> void updateFieldIfNotNull(T newValue, java.util.function.Consumer<T> setter) {
        if (newValue != null) {
            setter.accept(newValue);
        }
    }
}
