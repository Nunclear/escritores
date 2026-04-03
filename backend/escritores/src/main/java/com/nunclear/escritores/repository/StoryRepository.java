package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Story;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StoryRepository extends JpaRepository<Story, Integer> {
    Optional<Story> findBySlugText(String slugText);
    List<Story> findByOwnerUserId(Integer userId);
    List<Story> findByVisibilityState(String visibilityState);
    List<Story> findByPublicationState(String publicationState);
    
    @Query("SELECT s FROM Story s WHERE s.visibilityState = 'public' AND s.publicationState = 'published' ORDER BY s.publishedAt DESC")
    List<Story> findPublishedAndPublicStories();
}
