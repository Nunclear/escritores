package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.StoryFavorite;
import com.nunclear.escritores.service.JwtService;
import com.nunclear.escritores.service.StoryFavoriteService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/favorites")
@RequiredArgsConstructor
public class StoryFavoriteController {
    private final StoryFavoriteService favoriteService;
    private final JwtService jwtService;

    /**
     * Marcar una historia como favorita
     */
    @PostMapping("/stories/{storyId}")
    public ResponseEntity<?> addFavorite(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer storyId) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            StoryFavorite favorite = favoriteService.addFavorite(userId, storyId);
            return ResponseEntity.status(HttpStatus.CREATED).body(favorite);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remover una historia de favoritos
     */
    @DeleteMapping("/stories/{storyId}")
    public ResponseEntity<?> removeFavorite(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer storyId) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            favoriteService.removeFavorite(userId, storyId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todos los favoritos del usuario autenticado
     */
    @GetMapping
    public ResponseEntity<?> getUserFavorites(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            List<StoryFavorite> favorites = favoriteService.getUserFavorites(userId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verificar si una historia es favorita del usuario
     */
    @GetMapping("/stories/{storyId}/is-favorite")
    public ResponseEntity<?> isFavorite(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer storyId) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            boolean isFavorite = favoriteService.isFavorite(userId, storyId);
            return ResponseEntity.ok(Map.of("isFavorite", isFavorite));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Contar cuántos favoritos tiene una historia
     */
    @GetMapping("/stories/{storyId}/count")
    public ResponseEntity<?> countFavorites(@PathVariable Integer storyId) {
        try {
            long count = favoriteService.countFavorites(storyId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todos los usuarios que han marcado como favorita una historia
     */
    @GetMapping("/stories/{storyId}/favorited-by")
    public ResponseEntity<?> getStoryFavoritedBy(@PathVariable Integer storyId) {
        try {
            List<StoryFavorite> favorites = favoriteService.getStoryFavoriteBy(storyId);
            return ResponseEntity.ok(favorites);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Helper methods
    private Integer extractUserIdFromHeader(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Token no proporcionado");
        }
        String token = authHeader.substring(7);
        return jwtService.validateToken(token);
    }
}
