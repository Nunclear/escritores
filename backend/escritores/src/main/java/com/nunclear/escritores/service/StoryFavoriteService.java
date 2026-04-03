package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.StoryFavorite;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.StoryFavoriteRepository;
import com.nunclear.escritores.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StoryFavoriteService {
    private final StoryFavoriteRepository favoriteRepository;
    private final StoryRepository storyRepository;
    private final AppUserRepository userRepository;

    /**
     * Marcar una historia como favorita
     */
    @Transactional
    public StoryFavorite addFavorite(Integer userId, Integer storyId) {
        AppUser user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new RuntimeException("Historia no encontrada"));

        if (favoriteRepository.existsByUserIdAndStoryId(userId, storyId)) {
            throw new RuntimeException("Esta historia ya está en favoritos");
        }

        StoryFavorite favorite = StoryFavorite.builder()
                .user(user)
                .story(story)
                .createdAt(LocalDateTime.now())
                .build();

        return favoriteRepository.save(favorite);
    }

    /**
     * Remover una historia de favoritos
     */
    @Transactional
    public void removeFavorite(Integer userId, Integer storyId) {
        StoryFavorite favorite = favoriteRepository.findByUserIdAndStoryId(userId, storyId)
                .orElseThrow(() -> new RuntimeException("Favorito no encontrado"));
        favoriteRepository.delete(favorite);
    }

    /**
     * Obtener todos los favoritos de un usuario
     */
    public List<StoryFavorite> getUserFavorites(Integer userId) {
        return favoriteRepository.findByUserId(userId);
    }

    /**
     * Obtener todos los usuarios que han marcado como favorita una historia
     */
    public List<StoryFavorite> getStoryFavoriteBy(Integer storyId) {
        return favoriteRepository.findByStoryId(storyId);
    }

    /**
     * Verificar si una historia es favorita del usuario
     */
    public boolean isFavorite(Integer userId, Integer storyId) {
        return favoriteRepository.existsByUserIdAndStoryId(userId, storyId);
    }

    /**
     * Contar favoritos de una historia
     */
    public long countFavorites(Integer storyId) {
        return favoriteRepository.findByStoryId(storyId).size();
    }
}
