package com.nunclear.escritores.service;

import com.nunclear.escritores.config.TestDataBuilder;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Favorite;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.exception.ConflictException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.FavoriteRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
@DisplayName("FavoriteService Unit Tests")
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private StoryRepository storyRepository;

    @InjectMocks
    private FavoriteService favoriteService;

    private AppUser testUser;
    private AppUser testAuthor;
    private Story testStory;
    private Favorite testFavorite;

    @BeforeEach
    void setUp() {
        testUser = TestDataBuilder.buildAppUser("user", "user@example.com");
        testUser.setId(1);

        testAuthor = TestDataBuilder.buildAppUser("author", "author@example.com");
        testAuthor.setId(2);

        testStory = TestDataBuilder.buildStory(testAuthor, "Test Story");
        testStory.setId(1);

        testFavorite = TestDataBuilder.buildFavorite(testUser, testStory);
        testFavorite.setId(1);

        mockAuthenticatedUser(testUser);
    }

    private void mockAuthenticatedUser(AppUser user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // ============== Add Favorite Tests ==============

    @Test
    @DisplayName("addFavorite_WithValidStory_CreatesFavorite")
    void addFavoriteWithValidStoryCreatesFavorite() {
        // Arrange
        when(storyRepository.findById(1)).thenReturn(Optional.of(testStory));
        when(favoriteRepository.findByUserIdAndStoryId(testUser.getId(), 1)).thenReturn(Optional.empty());
        when(favoriteRepository.save(any(Favorite.class))).thenAnswer(invocation -> {
            Favorite favorite = invocation.getArgument(0);
            favorite.setId(1);
            favorite.setCreatedAt(LocalDateTime.now());
            return favorite;
        });

        // Act
        var response = favoriteService.addFavorite(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.storyId());
        verify(favoriteRepository, times(1)).save(any(Favorite.class));
    }

    @Test
    @DisplayName("addFavorite_WithInvalidStoryId_ThrowsResourceNotFoundException")
    void addFavoriteWithInvalidStoryIdThrowsResourceNotFoundException() {
        // Arrange
        when(storyRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> favoriteService.addFavorite(999));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    @Test
    @DisplayName("addFavorite_WithAlreadyFavoritedStory_ThrowsConflictException")
    void addFavoriteWithAlreadyFavoritedStoryThrowsConflictException() {
        // Arrange
        when(storyRepository.findById(1)).thenReturn(Optional.of(testStory));
        when(favoriteRepository.findByUserIdAndStoryId(testUser.getId(), 1))
                .thenReturn(Optional.of(testFavorite));

        // Act & Assert
        assertThrows(ConflictException.class, () -> favoriteService.addFavorite(1));
        verify(favoriteRepository, never()).save(any(Favorite.class));
    }

    // ============== Remove Favorite Tests ==============

    @Test
    @DisplayName("removeFavorite_WithValidFavorite_RemovesFavorite")
    void removeFavoriteWithValidFavoriteRemovesFavorite() {
        // Arrange
        when(favoriteRepository.findById(1)).thenReturn(Optional.of(testFavorite));

        // Act
        favoriteService.removeFavorite(1);

        // Assert
        verify(favoriteRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("removeFavorite_WithInvalidId_ThrowsResourceNotFoundException")
    void removeFavoriteWithInvalidIdThrowsResourceNotFoundException() {
        // Arrange
        when(favoriteRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> favoriteService.removeFavorite(999));
        verify(favoriteRepository, never()).deleteById(anyInt());
    }

    // ============== Check Favorite Tests ==============

    @Test
    @DisplayName("isFavorited_WithFavoritedStory_ReturnsTrue")
    void isFavoritedWithFavoritedStoryReturnsTrue() {
        // Arrange
        when(favoriteRepository.findByUserIdAndStoryId(testUser.getId(), 1))
                .thenReturn(Optional.of(testFavorite));

        // Act
        boolean isFavorited = favoriteService.isFavorited(testUser.getId(), 1);

        // Assert
        assertTrue(isFavorited);
    }

    @Test
    @DisplayName("isFavorited_WithNonFavoritedStory_ReturnsFalse")
    void isFavoritedWithNonFavoritedStoryReturnsFalse() {
        // Arrange
        when(favoriteRepository.findByUserIdAndStoryId(testUser.getId(), 1))
                .thenReturn(Optional.empty());

        // Act
        boolean isFavorited = favoriteService.isFavorited(testUser.getId(), 1);

        // Assert
        assertFalse(isFavorited);
    }

    // ============== List Favorites Tests ==============

    @Test
    @DisplayName("getUserFavorites_WithPagination_ReturnsPagedFavorites")
    void getUserFavoritesWithPaginationReturnsPagedFavorites() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Favorite> favorites = List.of(testFavorite);
        Page<Favorite> page = new PageImpl<>(favorites, pageable, 1);

        when(favoriteRepository.findByUserId(testUser.getId(), pageable)).thenReturn(page);

        // Act
        var response = favoriteService.getUserFavorites(testUser.getId(), 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(favoriteRepository, times(1)).findByUserId(testUser.getId(), pageable);
    }

    @Test
    @DisplayName("getUserFavorites_WithEmptyList_ReturnsEmptyPage")
    void getUserFavoritesWithEmptyListReturnsEmptyPage() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Favorite> emptyPage = new PageImpl<>(List.of(), pageable, 0);

        when(favoriteRepository.findByUserId(testUser.getId(), pageable)).thenReturn(emptyPage);

        // Act
        var response = favoriteService.getUserFavorites(testUser.getId(), 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(0, response.getTotalElements());
    }

    // ============== Count Favorites Tests ==============

    @Test
    @DisplayName("countFavorites_ReturnsCorrectCount")
    void countFavoritesReturnsCorrectCount() {
        // Arrange
        when(favoriteRepository.countByStoryId(1)).thenReturn(5L);

        // Act
        long count = favoriteService.countFavorites(1);

        // Assert
        assertEquals(5, count);
    }

    @Test
    @DisplayName("countFavorites_WithZeroFavorites_ReturnsZero")
    void countFavoritesWithZeroFavoritesReturnsZero() {
        // Arrange
        when(favoriteRepository.countByStoryId(1)).thenReturn(0L);

        // Act
        long count = favoriteService.countFavorites(1);

        // Assert
        assertEquals(0, count);
    }
}
