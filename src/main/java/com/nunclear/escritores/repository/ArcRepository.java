package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Arc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ArcRepository extends JpaRepository<Arc, Integer> {

    Page<Arc> findByStoryId(Integer storyId, Pageable pageable);

    List<Arc> findByStoryIdOrderByPositionIndexAscIdAsc(Integer storyId);

    /**
     * Retrieves a paged list of arcs for the given story id that have not
     * been soft‑deleted.
     *
     * @param storyId the id of the story
     * @param pageable paging information
     * @return a page of non‑deleted arcs belonging to the story
     */
    Page<Arc> findByStoryIdAndDeletedFalse(Integer storyId, Pageable pageable);

    /**
     * Finds an arc by id only if it has not been soft‑deleted.
     *
     * @param id the arc id
     * @return an optional containing the arc when it exists and is not deleted
     */
    Optional<Arc> findByIdAndDeletedFalse(Integer id);

    /**
     * Retrieves all arcs that have not been soft‑deleted.
     *
     * @return a list of arcs with deleted = false
     */
    List<Arc> findByDeletedFalse();
}