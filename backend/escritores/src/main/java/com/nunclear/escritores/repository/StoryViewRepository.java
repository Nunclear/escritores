package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StoryViewRepository extends JpaRepository<StoryView, Integer> {
    List<StoryView> findByStoryId(Integer storyId);
    List<StoryView> findByUserId(Integer userId);
    List<StoryView> findByStoryIdAndCreatedAtAfter(Integer storyId, LocalDateTime dateTime);
    
    @Query("SELECT COUNT(v) FROM StoryView v WHERE v.story.id = ?1")
    long countViewsByStoryId(Integer storyId);
    
    @Query("SELECT COUNT(DISTINCT v.user.id) FROM StoryView v WHERE v.story.id = ?1 AND v.user IS NOT NULL")
    long countUniqueUsersByStoryId(Integer storyId);
}
