package com.spring.jwt.Favorite;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FavoriteDTO {

    private Integer favoriteId;
    private Integer mainUserId;
    private Integer favoriteUserId;
}
