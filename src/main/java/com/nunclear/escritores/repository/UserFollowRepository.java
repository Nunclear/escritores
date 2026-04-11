package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.UserFollow;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserFollowRepository extends JpaRepository<UserFollow, Integer> {
    long countByFollowedUserId(Integer followedUserId);
}