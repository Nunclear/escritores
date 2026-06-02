package com.nunclear.escritores.repository;

import com.nunclear.escritores.config.TestDataBuilder;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Story;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DisplayName("StoryRepository Integration Tests")
class StoryRepositoryTest {

    @Autowired
    private StoryRepository storyRepository;

    @Autowired
    private AppUserRepository appUserRepository;

    private AppUser testAuthor;
    private Story testStory;

    @BeforeEach
    void setUp() {
        testAuthor = appUserRepository.save(TestDataBuilder.buildAppUser("author", "author@example.com"));
        testStory = storyRepository.save(TestDataBuilder.buildStory(testAuthor, "Test Story"));
    }

    // ============== Find Tests ==============

    @Test
    @DisplayName("findById_WithExistingStory_ReturnsStory")
    void findByIdWithExistingStoryReturnsStory() {
        // Act
        Optional<Story> result = storyRepository.findById(testStory.getId());

        // Assert
        assertTrue(result.isPresent());
        assertEquals("Test Story", result.get().getTitle());
    }

    @Test
    @DisplayName("findById_WithNonExistingStory_ReturnsEmpty")
    void findByIdWithNonExistingStoryReturnsEmpty() {
        // Act
        Optional<Story> result = storyRepository.findById(999);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("findBySlugText_WithValidSlug_ReturnsStory")
    void findBySlugTextWithValidSlugReturnsStory() {
        // Act
        Optional<Story> result = storyRepository.findBySlugText(testStory.getSlugText());

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testStory.getTitle(), result.get().getTitle());
    }

    @Test
    @DisplayName("findBySlugText_WithInvalidSlug_ReturnsEmpty")
    void findBySlugTextWithInvalidSlugReturnsEmpty() {
        // Act
        Optional<Story> result = storyRepository.findBySlugText("nonexistent-slug");

        // Assert
        assertTrue(result.isEmpty());
    }

    // ============== Existence Tests ==============

    @Test
    @DisplayName("existsBySlugText_WithExistingSlug_ReturnsTrue")
    void existsBySlugTextWithExistingSlugReturnsTrue() {
        // Act
        boolean exists = storyRepository.existsBySlugText(testStory.getSlugText());

        // Assert
        assertTrue(exists);
    }

    @Test
    @DisplayName("existsBySlugText_WithNonExistingSlug_ReturnsFalse")
    void existsBySlugTextWithNonExistingSlugReturnsFalse() {
        // Act
        boolean exists = storyRepository.existsBySlugText("nonexistent");

        // Assert
        assertFalse(exists);
    }

    // ============== Pagination Tests ==============

    @Test
    @DisplayName("findByVisibilityStateAndPublicationState_WithValidState_ReturnsPaginatedResults")
    void findByVisibilityStateAndPublicationStateWithValidStateReturnsPaginatedResults() {
        // Arrange
        testStory.setVisibilityState("public");
        testStory.setPublicationState("published");
        storyRepository.save(testStory);
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Story> result = storyRepository.findByVisibilityStateIgnoreCaseAndPublicationStateIgnoreCaseAndArchivedAtIsNull(
                "public", "published", pageable
        );

        // Assert
        assertTrue(result.getContent().stream().anyMatch(s -> s.getId().equals(testStory.getId())));
    }

    // ============== Owner Tests ==============

    @Test
    @DisplayName("findByOwnerUserId_WithValidOwnerId_ReturnsUserStories")
    void findByOwnerUserIdWithValidOwnerIdReturnsUserStories() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Story> result = storyRepository.findByOwnerUserIdAndArchivedAtIsNull(testAuthor.getId(), pageable);

        // Assert
        assertTrue(result.getContent().stream().anyMatch(s -> s.getOwnerUserId().equals(testAuthor.getId())));
    }

    @Test
    @DisplayName("findByOwnerUserId_WithInvalidOwnerId_ReturnsEmptyPage")
    void findByOwnerUserIdWithInvalidOwnerIdReturnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);

        // Act
        Page<Story> result = storyRepository.findByOwnerUserIdAndArchivedAtIsNull(999, pageable);

        // Assert
        assertEquals(0, result.getTotalElements());
    }

    // ============== Deletion Tests ==============

    @Test
    @DisplayName("delete_WithExistingStory_DeletesStory")
    void deleteWithExistingStoryDeletesStory() {
        // Act
        storyRepository.delete(testStory);

        // Assert
        Optional<Story> result = storyRepository.findById(testStory.getId());
        assertTrue(result.isEmpty());
    }
}
