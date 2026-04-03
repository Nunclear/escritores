package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryViewLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoryViewLogRepository extends JpaRepository<StoryViewLog, Integer> {
    List<StoryViewLog> findByStoryId(Integer storyId);
    List<StoryViewLog> findByChapterId(Integer chapterId);
    List<StoryViewLog> findByUserId(Integer userId);
}
