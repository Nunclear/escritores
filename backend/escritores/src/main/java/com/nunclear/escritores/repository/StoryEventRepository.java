package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoryEventRepository extends JpaRepository<StoryEvent, Integer> {
    List<StoryEvent> findByStoryId(Integer storyId);
    List<StoryEvent> findByChapterId(Integer chapterId);
    List<StoryEvent> findByStoryIdAndEventKind(Integer storyId, String eventKind);
}
