package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryCharacter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface StoryCharacterRepository extends JpaRepository<StoryCharacter, Integer> {
    List<StoryCharacter> findByStoryId(Integer storyId);
    List<StoryCharacter> findByName(String name);
    
    @Query("SELECT c FROM StoryCharacter c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    List<StoryCharacter> findByNameContaining(@Param("name") String name);
}
