package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Idea;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface IdeaRepository extends JpaRepository<Idea, Integer> {
    List<Idea> findByStoryId(Integer storyId);
}
