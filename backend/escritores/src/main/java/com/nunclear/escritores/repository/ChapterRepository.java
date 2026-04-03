package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Chapter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ChapterRepository extends JpaRepository<Chapter, Integer> {
    List<Chapter> findByStoryId(Integer storyId);
    List<Chapter> findByVolumeId(Integer volumeId);
    List<Chapter> findByPublicationState(String publicationState);
    List<Chapter> findByStoryIdOrderByPositionIndex(Integer storyId);
    List<Chapter> findByStoryIdAndPublicationState(Integer storyId, String publicationState);
}
