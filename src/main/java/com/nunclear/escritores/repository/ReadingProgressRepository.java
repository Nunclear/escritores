package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.ReadingProgress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadingProgressRepository extends JpaRepository<ReadingProgress, Integer> {

    Optional<ReadingProgress> findByUserIdAndStoryId(Integer userId, Integer storyId);

    List<ReadingProgress> findByUserId(Integer userId);

    List<ReadingProgress> findByStoryId(Integer storyId);

    void deleteByUserIdAndStoryId(Integer userId, Integer storyId);

    boolean existsByUserIdAndStoryId(Integer userId, Integer storyId);
}
