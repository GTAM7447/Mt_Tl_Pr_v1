package com.spring.jwt.Favorite;

import com.spring.jwt.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavoriteRepository extends JpaRepository<Favorite, Integer> {

    boolean existsByMainUserIdAndFavoriteUserId(Integer mainUserId, Integer favoriteUserId);

    List<Favorite> findByMainUserId(Integer mainUserId);

    List<Favorite> findByFavoriteUserId(Integer favoriteUserId);

    void deleteByMainUserIdAndFavoriteUserId(Integer mainUserId, Integer favoriteUserId);
}
