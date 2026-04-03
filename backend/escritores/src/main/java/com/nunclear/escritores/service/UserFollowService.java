package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.UserFollow;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.UserFollowRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserFollowService {
    private final UserFollowRepository followRepository;
    private final AppUserRepository userRepository;

    /**
     * Seguir a un usuario
     */
    @Transactional
    public UserFollow followUser(Integer followerId, Integer followedId) {
        if (followerId.equals(followedId)) {
            throw new RuntimeException("No puedes seguirte a ti mismo");
        }

        AppUser follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Usuario seguidor no encontrado"));
        AppUser followed = userRepository.findById(followedId)
                .orElseThrow(() -> new RuntimeException("Usuario a seguir no encontrado"));

        if (followRepository.existsByFollowerUserIdAndFollowedUserId(followerId, followedId)) {
            throw new RuntimeException("Ya estás siguiendo a este usuario");
        }

        UserFollow follow = UserFollow.builder()
                .followerUser(follower)
                .followedUser(followed)
                .createdAt(LocalDateTime.now())
                .build();

        return followRepository.save(follow);
    }

    /**
     * Dejar de seguir a un usuario
     */
    @Transactional
    public void unfollowUser(Integer followerId, Integer followedId) {
        UserFollow follow = followRepository.findByFollowerUserIdAndFollowedUserId(followerId, followedId)
                .orElseThrow(() -> new RuntimeException("No estás siguiendo a este usuario"));
        followRepository.delete(follow);
    }

    /**
     * Obtener lista de usuarios seguidos por un usuario
     */
    public List<UserFollow> getFollowing(Integer userId) {
        return followRepository.findByFollowerUserId(userId);
    }

    /**
     * Obtener lista de seguidores de un usuario
     */
    public List<UserFollow> getFollowers(Integer userId) {
        return followRepository.findByFollowedUserId(userId);
    }

    /**
     * Verificar si un usuario sigue a otro
     */
    public boolean isFollowing(Integer followerId, Integer followedId) {
        return followRepository.existsByFollowerUserIdAndFollowedUserId(followerId, followedId);
    }

    /**
     * Contar seguidores de un usuario
     */
    public long countFollowers(Integer userId) {
        return followRepository.findByFollowedUserId(userId).size();
    }

    /**
     * Contar usuarios siendo seguidos
     */
    public long countFollowing(Integer userId) {
        return followRepository.findByFollowerUserId(userId).size();
    }
}
