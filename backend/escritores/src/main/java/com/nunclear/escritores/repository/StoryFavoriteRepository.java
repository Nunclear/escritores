package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryFavorite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryFavoriteRepository extends JpaRepository<StoryFavorite, Integer> {
    List<StoryFavorite> findByUserId(Integer userId);
    List<StoryFavorite> findByStoryId(Integer storyId);
    Optional<StoryFavorite> findByUserIdAndStoryId(Integer userId, Integer storyId);
    boolean existsByUserIdAndStoryId(Integer userId, Integer storyId);
}
