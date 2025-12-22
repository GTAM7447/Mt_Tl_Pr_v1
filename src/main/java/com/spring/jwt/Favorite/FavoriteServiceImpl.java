package com.spring.jwt.Favorite;

import com.spring.jwt.entity.Favorite;
import com.spring.jwt.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final FavoriteRepository repo;

    @Override
    public FavoriteDTO addFavorite(FavoriteDTO dto) {

        Integer mainUserId = dto.getMainUserId();
        Integer favoriteUserId = dto.getFavoriteUserId();

        // Check if already in favorites
        boolean exists = repo.existsByMainUserIdAndFavoriteUserId(mainUserId, favoriteUserId);

        if (exists) {
            // REMOVE FAVORITE
            repo.deleteByMainUserIdAndFavoriteUserId(mainUserId, favoriteUserId);
            return new FavoriteDTO(null, mainUserId, favoriteUserId); // Indicate removed
        }

        // ADD FAVORITE
        Favorite fav = new Favorite();
        fav.setMainUserId(mainUserId);
        fav.setFavoriteUserId(favoriteUserId);

        Favorite saved = repo.save(fav);

        return new FavoriteDTO(saved.getFavoriteId(), saved.getMainUserId(), saved.getFavoriteUserId());
    }


    @Override
    public List<FavoriteDTO> getFavoritesByUser(Integer mainUserId) {

        return repo.findByMainUserId(mainUserId)
                .stream()
                .map(f -> new FavoriteDTO(f.getFavoriteId(), f.getMainUserId(), f.getFavoriteUserId()))
                .toList();
    }

    @Override
    public void removeFavorite(Integer mainUserId, Integer favoriteUserId) {

        if (!repo.existsByMainUserIdAndFavoriteUserId(mainUserId, favoriteUserId)) {
            throw new ResourceNotFoundException("Favorite does not exist");
        }

        repo.deleteByMainUserIdAndFavoriteUserId(mainUserId, favoriteUserId);
    }
}
