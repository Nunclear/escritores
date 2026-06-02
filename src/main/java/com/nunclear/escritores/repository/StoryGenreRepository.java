package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.StoryGenre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StoryGenreRepository extends JpaRepository<StoryGenre, Integer> {

    List<StoryGenre> findByStoryId(Integer storyId);

    List<StoryGenre> findByGenreId(Integer genreId);

    void deleteByStoryId(Integer storyId);

    void deleteByGenreId(Integer genreId);

    void deleteByStoryIdAndGenreId(Integer storyId, Integer genreId);

    boolean existsByStoryIdAndGenreId(Integer storyId, Integer genreId);
}
