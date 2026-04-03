package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Volume;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface VolumeRepository extends JpaRepository<Volume, Integer> {
    List<Volume> findByStoryId(Integer storyId);
    List<Volume> findByArcId(Integer arcId);
    List<Volume> findByStoryIdOrderByPositionIndex(Integer storyId);
}
