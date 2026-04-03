package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryComment;
import com.nunclear.escritores.entity.Visibility;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoryCommentRepository extends JpaRepository<StoryComment, Integer> {
    List<StoryComment> findByStoryId(Integer storyId);
    List<StoryComment> findByChapterId(Integer chapterId);
    List<StoryComment> findByStoryIdAndVisibility(Integer storyId, Visibility visibility);
    List<StoryComment> findByParentCommentId(Integer parentCommentId);
}
