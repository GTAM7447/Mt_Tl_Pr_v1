package com.spring.jwt.Subscription;

import java.util.List;

public interface SubscriptionService {

    SubscriptionDTO create(SubscriptionDTO dto);

    SubscriptionDTO getById(Integer id);

    List<SubscriptionDTO> getAll();

    SubscriptionDTO update(Integer id, SubscriptionDTO dto);

    void delete(Integer id);
}
