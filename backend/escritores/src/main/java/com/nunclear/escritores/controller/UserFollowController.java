package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.UserFollow;
import com.nunclear.escritores.service.JwtService;
import com.nunclear.escritores.service.UserFollowService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/follows")
@RequiredArgsConstructor
public class UserFollowController {
    private final UserFollowService followService;
    private final JwtService jwtService;

    /**
     * Seguir a un usuario
     */
    @PostMapping("/users/{followedId}")
    public ResponseEntity<?> followUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer followedId) {
        try {
            Integer followerId = extractUserIdFromHeader(authHeader);
            UserFollow follow = followService.followUser(followerId, followedId);
            return ResponseEntity.status(HttpStatus.CREATED).body(follow);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Dejar de seguir a un usuario
     */
    @DeleteMapping("/users/{followedId}")
    public ResponseEntity<?> unfollowUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer followedId) {
        try {
            Integer followerId = extractUserIdFromHeader(authHeader);
            followService.unfollowUser(followerId, followedId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener usuarios seguidos por el usuario autenticado
     */
    @GetMapping("/following")
    public ResponseEntity<?> getFollowing(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            List<UserFollow> following = followService.getFollowing(userId);
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener seguidores del usuario autenticado
     */
    @GetMapping("/followers")
    public ResponseEntity<?> getFollowers(@RequestHeader("Authorization") String authHeader) {
        try {
            Integer userId = extractUserIdFromHeader(authHeader);
            List<UserFollow> followers = followService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener usuarios seguidos por un usuario específico
     */
    @GetMapping("/users/{userId}/following")
    public ResponseEntity<?> getUserFollowing(@PathVariable Integer userId) {
        try {
            List<UserFollow> following = followService.getFollowing(userId);
            return ResponseEntity.ok(following);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener seguidores de un usuario específico
     */
    @GetMapping("/users/{userId}/followers")
    public ResponseEntity<?> getUserFollowers(@PathVariable Integer userId) {
        try {
            List<UserFollow> followers = followService.getFollowers(userId);
            return ResponseEntity.ok(followers);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verificar si el usuario autenticado sigue a otro usuario
     */
    @GetMapping("/users/{followedId}/is-following")
    public ResponseEntity<?> isFollowing(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Integer followedId) {
        try {
            Integer followerId = extractUserIdFromHeader(authHeader);
            boolean isFollowing = followService.isFollowing(followerId, followedId);
            return ResponseEntity.ok(Map.of("isFollowing", isFollowing));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Contar seguidores de un usuario
     */
    @GetMapping("/users/{userId}/followers-count")
    public ResponseEntity<?> countFollowers(@PathVariable Integer userId) {
        try {
            long count = followService.countFollowers(userId);
            return ResponseEntity.ok(Map.of("count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Contar usuarios siendo seguidos
     */
    @GetMapping("/users/{userId}/following-count")
    public ResponseEntity<?> countFollowing(@PathVariable Integer userId) {
        try {
            long count = followService.countFollowing(userId);
            return ResponseEntity.ok(Map.of("count", count));
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
