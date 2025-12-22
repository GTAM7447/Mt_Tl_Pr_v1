package com.spring.jwt.UserView;

import com.spring.jwt.entity.UserView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface UserViewRepository extends JpaRepository<UserView, Integer> {

    List<UserView> findByUserId(Integer userId);

    List<UserView> findByUserIdAndDate(Integer userId, LocalDate date);

    long countByUserId(Integer userId);

    Integer countByUserIdAndDate(Integer userId, LocalDate date);


    List<UserView> findByUserIdOrderByDateDescUserViewIdDesc(Integer userId);


    List<UserView> findByUserIdOrderByDateDesc(Integer userId);
}
