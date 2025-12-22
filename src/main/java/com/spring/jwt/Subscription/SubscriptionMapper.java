package com.spring.jwt.Subscription;

import com.spring.jwt.entity.Subscription;
import com.spring.jwt.entity.Enums.Status;

public class SubscriptionMapper {

    public static SubscriptionDTO toDTO(Subscription entity) {
        return new SubscriptionDTO(
                entity.getSubscriptionId(),
                entity.getName(),
                entity.getCredit(),
                entity.getCreatedDate(),
                entity.getDayLimit(),
                entity.getTimePeriodMonths(),
                entity.getStatus() != null ? entity.getStatus().name() : null
        );
    }

    public static Subscription toEntity(SubscriptionDTO dto) {
        Subscription entity = new Subscription();

        entity.setSubscriptionId(dto.getSubscriptionId());
        entity.setName(dto.getName());
        entity.setCredit(dto.getCredit());
        entity.setCreatedDate(dto.getCreatedDate());
        entity.setDayLimit(dto.getDayLimit());
        entity.setTimePeriodMonths(dto.getTimePeriodMonths());

        if (dto.getStatus() != null) {
            entity.setStatus(Status.valueOf(dto.getStatus().toUpperCase()));
        }

        return entity;
    }
}
