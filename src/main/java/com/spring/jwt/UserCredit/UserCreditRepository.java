package com.spring.jwt.UserCredit;

import com.spring.jwt.entity.UserCredit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserCreditRepository extends JpaRepository<UserCredit, Integer> {
    boolean existsByUserIdAndSubscription_SubscriptionId(Integer userId, Integer subscriptionId);

    Optional<UserCredit> findTopByUserIdOrderByUserCreditIdDesc(Integer userId);

    Optional<UserCredit> findByUserId(Integer userId);


}
