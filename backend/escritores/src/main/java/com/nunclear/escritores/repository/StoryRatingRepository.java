package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRatingRepository extends JpaRepository<StoryRating, Integer> {
    List<StoryRating> findByStoryId(Integer storyId);
    List<StoryRating> findByAuthorUserId(Integer userId);
    Optional<StoryRating> findByStoryIdAndAuthorUserId(Integer storyId, Integer userId);
}
