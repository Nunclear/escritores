package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StoryRepository extends JpaRepository<Story, Integer> {

    /**
     * Retrieves all stories that have not been soft‑deleted.  This method is
     * used by services to exclude logically removed records from
     * listing operations.
     *
     * @return a list of stories with {@code deleted = false}
     */
    List<Story> findByDeletedFalse();

    /**
     * Retrieves a paged collection of stories that have not been
     * soft‑deleted.  Useful for pageable listing endpoints.
     *
     * @param pageable paging information
     * @return a page of non‑deleted stories
     */
    Page<Story> findByDeletedFalse(Pageable pageable);

    /**
     * Finds a story by its id only if it has not been soft‑deleted.
     *
     * @param id the story id
     * @return an optional containing the story when it exists and is
     *         not deleted, otherwise empty
     */
    Optional<Story> findByIdAndDeletedFalse(Integer id);

    /**
     * Finds a story by its slug text only if it has not been soft‑deleted.
     *
     * @param slugText the slug of the story
     * @return an optional containing the story when it exists and is
     *         not deleted
     */
    Optional<Story> findBySlugTextAndDeletedFalse(String slugText);

    /**
     * Finds stories matching the given visibility and publication state
     * predicates while ensuring they are not archived and not soft‑deleted.
     *
     * @param visibilityState the visibility state to match (case insensitive)
     * @param publicationState the publication state to match (case insensitive)
     * @param pageable paging information
     * @return a page of non‑deleted stories matching the criteria
     */
    Page<Story> findByVisibilityStateIgnoreCaseAndPublicationStateIgnoreCaseAndArchivedAtIsNullAndDeletedFalse(
            String visibilityState,
            String publicationState,
            Pageable pageable
    );

    Optional<Story> findBySlugText(String slugText);

    boolean existsBySlugText(String slugText);

    long countByOwnerUserIdAndVisibilityStateIgnoreCaseAndPublicationStateIgnoreCase(
            Integer ownerUserId,
            String visibilityState,
            String publicationState
    );

    Page<Story> findByOwnerUserIdAndVisibilityStateIgnoreCaseAndPublicationStateIgnoreCase(
            Integer ownerUserId,
            String visibilityState,
            String publicationState,
            Pageable pageable
    );

    Page<Story> findByVisibilityStateIgnoreCaseAndPublicationStateIgnoreCaseAndArchivedAtIsNull(
            String visibilityState,
            String publicationState,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM Story s
            WHERE s.archivedAt IS NULL
              AND s.deleted = false
              AND LOWER(s.visibilityState) = LOWER(:visibilityState)
              AND LOWER(s.publicationState) = LOWER(:publicationState)
              AND (
                    LOWER(s.title) LIKE LOWER(CONCAT('%', :q, '%'))
                 OR LOWER(COALESCE(s.description, '')) LIKE LOWER(CONCAT('%', :q, '%'))
              )
            """)
    Page<Story> searchPublicStories(
            @Param("q") String q,
            @Param("visibilityState") String visibilityState,
            @Param("publicationState") String publicationState,
            Pageable pageable
    );

    @Query("""
            SELECT s
            FROM Story s
            WHERE s.ownerUserId = :ownerUserId
              AND s.archivedAt IS NULL
              AND s.deleted = false
              AND (
                    LOWER(s.visibilityState) = 'public'
                AND LOWER(s.publicationState) = 'published'
              )
            """)
    Page<Story> findPublicPublishedByOwner(@Param("ownerUserId") Integer ownerUserId, Pageable pageable);

    @Query("""
            SELECT s
            FROM Story s
            WHERE s.ownerUserId = :ownerUserId
              AND s.archivedAt IS NULL
              AND s.deleted = false
            """)
    Page<Story> findAllVisibleForOwner(@Param("ownerUserId") Integer ownerUserId, Pageable pageable);

    Page<Story> findByOwnerUserIdAndPublicationStateIgnoreCaseAndArchivedAtIsNull(
            Integer ownerUserId,
            String publicationState,
            Pageable pageable
    );

    Page<Story> findByOwnerUserIdAndArchivedAtIsNotNull(
            Integer ownerUserId,
            Pageable pageable
    );
    List<Story> findByOwnerUserId(Integer ownerUserId);


    long countByOwnerUserId(Integer ownerUserId);

    long countByOwnerUserIdAndPublicationStateIgnoreCaseAndArchivedAtIsNull(Integer ownerUserId, String publicationState);

    long countByArchivedAtIsNull();
}