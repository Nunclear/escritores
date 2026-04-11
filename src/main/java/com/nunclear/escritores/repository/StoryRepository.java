package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Story;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StoryRepository extends JpaRepository<Story, Integer> {

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
}