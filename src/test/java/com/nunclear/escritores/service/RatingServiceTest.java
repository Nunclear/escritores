package com.nunclear.escritores.service;

import com.nunclear.escritores.config.TestDataBuilder;
import com.nunclear.escritores.dto.request.CreateRatingRequest;
import com.nunclear.escritores.dto.request.UpdateRatingRequest;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Rating;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ConflictException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.RatingRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("RatingService Unit Tests")
class RatingServiceTest {

    @Mock
    private RatingRepository ratingRepository;

    @Mock
    private StoryRepository storyRepository;

    @InjectMocks
    private RatingService ratingService;

    private AppUser testAuthor;
    private AppUser testRater;
    private Story testStory;
    private Rating testRating;

    @BeforeEach
    void setUp() {
        testAuthor = TestDataBuilder.buildAppUser("author", "author@example.com");
        testAuthor.setId(1);

        testRater = TestDataBuilder.buildAppUser("rater", "rater@example.com");
        testRater.setId(2);

        testStory = TestDataBuilder.buildStory(testAuthor, "Test Story");
        testStory.setId(1);

        testRating = TestDataBuilder.buildRating(testStory, testRater, 5);
        testRating.setId(1);

        mockAuthenticatedUser(testRater);
    }

    private void mockAuthenticatedUser(AppUser user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // ============== Create Rating Tests ==============

    @Test
    @DisplayName("createRating_WithValidData_ReturnsCreatedRating")
    void createRatingWithValidDataReturnsCreatedRating() {
        // Arrange
        CreateRatingRequest request = CreateRatingRequest.builder()
                .storyId(1)
                .rating(5)
                .build();

        when(storyRepository.findById(1)).thenReturn(Optional.of(testStory));
        when(ratingRepository.findByStoryIdAndAuthorId(1, 2)).thenReturn(Optional.empty());
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> {
            Rating rating = invocation.getArgument(0);
            rating.setId(1);
            rating.setCreatedAt(LocalDateTime.now());
            return rating;
        });

        // Act
        var response = ratingService.createRating(request);

        // Assert
        assertNotNull(response);
        assertEquals(5, response.rating());
        verify(ratingRepository, times(1)).save(any(Rating.class));
    }

    @Test
    @DisplayName("createRating_WithInvalidStoryId_ThrowsResourceNotFoundException")
    void createRatingWithInvalidStoryIdThrowsResourceNotFoundException() {
        // Arrange
        CreateRatingRequest request = CreateRatingRequest.builder()
                .storyId(999)
                .rating(5)
                .build();

        when(storyRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ratingService.createRating(request));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("createRating_WithInvalidRatingValue_ThrowsBadRequestException")
    void createRatingWithInvalidRatingValueThrowsBadRequestException() {
        // Arrange
        CreateRatingRequest request = CreateRatingRequest.builder()
                .storyId(1)
                .rating(6) // Invalid: must be 1-5
                .build();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> ratingService.createRating(request));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("createRating_WithZeroRating_ThrowsBadRequestException")
    void createRatingWithZeroRatingThrowsBadRequestException() {
        // Arrange
        CreateRatingRequest request = CreateRatingRequest.builder()
                .storyId(1)
                .rating(0) // Invalid: must be 1-5
                .build();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> ratingService.createRating(request));
    }

    @Test
    @DisplayName("createRating_WithExistingRating_ThrowsConflictException")
    void createRatingWithExistingRatingThrowsConflictException() {
        // Arrange
        CreateRatingRequest request = CreateRatingRequest.builder()
                .storyId(1)
                .rating(4)
                .build();

        when(storyRepository.findById(1)).thenReturn(Optional.of(testStory));
        when(ratingRepository.findByStoryIdAndAuthorId(1, 2)).thenReturn(Optional.of(testRating));

        // Act & Assert
        assertThrows(ConflictException.class, () -> ratingService.createRating(request));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    // ============== Get Rating Tests ==============

    @Test
    @DisplayName("getRatingById_WithValidId_ReturnsRating")
    void getRatingByIdWithValidIdReturnsRating() {
        // Arrange
        when(ratingRepository.findById(1)).thenReturn(Optional.of(testRating));

        // Act
        var response = ratingService.getRatingById(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals(5, response.rating());
        verify(ratingRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getRatingById_WithInvalidId_ThrowsResourceNotFoundException")
    void getRatingByIdWithInvalidIdThrowsResourceNotFoundException() {
        // Arrange
        when(ratingRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> ratingService.getRatingById(999));
    }

    // ============== Update Rating Tests ==============

    @Test
    @DisplayName("updateRating_WithOwnership_UpdatesRating")
    void updateRatingWithOwnershipUpdatesRating() {
        // Arrange
        UpdateRatingRequest request = UpdateRatingRequest.builder()
                .rating(4)
                .build();

        when(ratingRepository.findById(1)).thenReturn(Optional.of(testRating));
        when(ratingRepository.save(any(Rating.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = ratingService.updateRating(1, request);

        // Assert
        assertNotNull(response);
        ArgumentCaptor<Rating> captor = ArgumentCaptor.forClass(Rating.class);
        verify(ratingRepository).save(captor.capture());
        assertEquals(4, captor.getValue().getRating());
    }

    @Test
    @DisplayName("updateRating_WithoutOwnership_ThrowsUnauthorizedException")
    void updateRatingWithoutOwnershipThrowsUnauthorizedException() {
        // Arrange
        testRating.setAuthor(testAuthor); // Different author
        UpdateRatingRequest request = UpdateRatingRequest.builder()
                .rating(3)
                .build();

        when(ratingRepository.findById(1)).thenReturn(Optional.of(testRating));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> ratingService.updateRating(1, request));
        verify(ratingRepository, never()).save(any(Rating.class));
    }

    @Test
    @DisplayName("updateRating_WithInvalidRating_ThrowsBadRequestException")
    void updateRatingWithInvalidRatingThrowsBadRequestException() {
        // Arrange
        UpdateRatingRequest request = UpdateRatingRequest.builder()
                .rating(10) // Invalid
                .build();

        when(ratingRepository.findById(1)).thenReturn(Optional.of(testRating));

        // Act & Assert
        assertThrows(BadRequestException.class, () -> ratingService.updateRating(1, request));
    }

    // ============== Delete Rating Tests ==============

    @Test
    @DisplayName("deleteRating_WithOwnership_DeletesRating")
    void deleteRatingWithOwnershipDeletesRating() {
        // Arrange
        when(ratingRepository.findById(1)).thenReturn(Optional.of(testRating));

        // Act
        ratingService.deleteRating(1);

        // Assert
        verify(ratingRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("deleteRating_WithoutOwnership_ThrowsUnauthorizedException")
    void deleteRatingWithoutOwnershipThrowsUnauthorizedException() {
        // Arrange
        testRating.setAuthor(testAuthor); // Different author
        when(ratingRepository.findById(1)).thenReturn(Optional.of(testRating));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> ratingService.deleteRating(1));
        verify(ratingRepository, never()).deleteById(1);
    }

    // ============== List Ratings Tests ==============

    @Test
    @DisplayName("getStoryRatings_WithPagination_ReturnsPagedRatings")
    void getStoryRatingsWithPaginationReturnsPagedRatings() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Rating> ratings = List.of(testRating);
        Page<Rating> page = new PageImpl<>(ratings, pageable, 1);

        when(ratingRepository.findByStoryId(1, pageable)).thenReturn(page);

        // Act
        var response = ratingService.getStoryRatings(1, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(ratingRepository, times(1)).findByStoryId(1, pageable);
    }

    @Test
    @DisplayName("getAverageRating_WithMultipleRatings_CalculatesAverage")
    void getAverageRatingWithMultipleRatingsCalculatesAverage() {
        // Arrange
        when(ratingRepository.getAverageRatingByStoryId(1)).thenReturn(4.5);

        // Act
        double average = ratingService.getAverageRating(1);

        // Assert
        assertEquals(4.5, average);
    }

    @Test
    @DisplayName("getUserRating_WithValidUserAndStory_ReturnsRating")
    void getUserRatingWithValidUserAndStoryReturnsRating() {
        // Arrange
        when(ratingRepository.findByStoryIdAndAuthorId(1, 2)).thenReturn(Optional.of(testRating));

        // Act
        var response = ratingService.getUserRating(1, 2);

        // Assert
        assertTrue(response.isPresent());
        assertEquals(5, response.get().rating());
    }

    @Test
    @DisplayName("getUserRating_WithNoRating_ReturnsEmpty")
    void getUserRatingWithNoRatingReturnsEmpty() {
        // Arrange
        when(ratingRepository.findByStoryIdAndAuthorId(1, 2)).thenReturn(Optional.empty());

        // Act
        var response = ratingService.getUserRating(1, 2);

        // Assert
        assertTrue(response.isEmpty());
    }
}
