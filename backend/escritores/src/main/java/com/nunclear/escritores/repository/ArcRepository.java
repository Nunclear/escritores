package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Arc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ArcRepository extends JpaRepository<Arc, Integer> {
    List<Arc> findByStoryId(Integer storyId);
    List<Arc> findByStoryIdOrderByPositionIndex(Integer storyId);
}
