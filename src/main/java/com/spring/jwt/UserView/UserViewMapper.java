package com.spring.jwt.UserView;

import com.spring.jwt.ContactDetails.dto.ContactDetailsResponse;
import com.spring.jwt.entity.UserView;

public class UserViewMapper {

    public static UserViewDTO toDTO(UserView entity) {
        return new UserViewDTO(
                entity.getUserViewId(),
                entity.getDate(),
                entity.getUserId(),
                entity.getContactId()
        );
    }

    public static UserView toEntity(UserViewDTO dto) {
        UserView entity = new UserView();
        entity.setUserViewId(dto.getUserViewId());
        entity.setDate(dto.getDate());
        entity.setUserId(dto.getUserId());
        entity.setContactId(dto.getContactId());
        return entity;
    }

    public static UserViewWithContactDTO toWithContactDTO(UserView view, ContactDetailsResponse contactDTO) {
        return new UserViewWithContactDTO(
                view.getUserViewId(),
                view.getDate().toString(),
                view.getUserId(),
                view.getContactId(),
                view.getDayCredit(),
                contactDTO
        );
    }

}
