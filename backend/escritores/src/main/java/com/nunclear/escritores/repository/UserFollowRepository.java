package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    List<UserFollow> findByFollowerUserId(Integer userId);
    List<UserFollow> findByFollowedUserId(Integer userId);
    Optional<UserFollow> findByFollowerUserIdAndFollowedUserId(Integer followerId, Integer followedId);
    boolean existsByFollowerUserIdAndFollowedUserId(Integer followerId, Integer followedId);
}
