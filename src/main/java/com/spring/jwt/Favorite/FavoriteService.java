package com.spring.jwt.Favorite;

import java.util.List;

public interface FavoriteService {

    FavoriteDTO addFavorite(FavoriteDTO dto);

    List<FavoriteDTO> getFavoritesByUser(Integer mainUserId);

    void removeFavorite(Integer mainUserId, Integer favoriteUserId);
}
