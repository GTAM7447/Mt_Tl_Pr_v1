package com.spring.jwt.Favorite;

import com.spring.jwt.dto.ResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
@RequiredArgsConstructor
public class FavoriteController {

    private final FavoriteService favoriteService;

    @PostMapping("add")
    public ResponseDto<FavoriteDTO> add(@RequestBody FavoriteDTO dto) {
        return ResponseDto.success("Added to favorites", favoriteService.addFavorite(dto));
    }

    @GetMapping("/{userId}")
    public ResponseDto<List<FavoriteDTO>> getByUser(@PathVariable Integer userId) {
        return ResponseDto.success("Favorites List", favoriteService.getFavoritesByUser(userId));
    }

    @DeleteMapping("/{mainUserId}/{favoriteUserId}")
    public ResponseDto<?> remove(@PathVariable Integer mainUserId, @PathVariable Integer favoriteUserId) {
        favoriteService.removeFavorite(mainUserId, favoriteUserId);
        return ResponseDto.success("Removed from favorites", null);
    }
}
