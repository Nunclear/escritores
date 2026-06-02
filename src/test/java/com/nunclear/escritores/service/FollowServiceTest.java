package com.nunclear.escritores.service;

import com.nunclear.escritores.config.TestDataBuilder;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Follow;
import com.nunclear.escritores.exception.ConflictException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.FollowRepository;
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
@DisplayName("FollowService Unit Tests")
class FollowServiceTest {

    @Mock
    private FollowRepository followRepository;

    @Mock
    private AppUserRepository appUserRepository;

    @InjectMocks
    private FollowService followService;

    private AppUser testFollower;
    private AppUser testFollowee;
    private Follow testFollow;

    @BeforeEach
    void setUp() {
        testFollower = TestDataBuilder.buildAppUser("follower", "follower@example.com");
        testFollower.setId(1);

        testFollowee = TestDataBuilder.buildAppUser("followee", "followee@example.com");
        testFollowee.setId(2);

        testFollow = TestDataBuilder.buildFollow(testFollower, testFollowee);
        testFollow.setId(1);

        mockAuthenticatedUser(testFollower);
    }

    private void mockAuthenticatedUser(AppUser user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // ============== Follow User Tests ==============

    @Test
    @DisplayName("followUser_WithValidUser_CreatesFollow")
    void followUserWithValidUserCreatesFollow() {
        // Arrange
        when(appUserRepository.findById(2)).thenReturn(Optional.of(testFollowee));
        when(followRepository.findByFollowerIdAndFolloweeId(1, 2)).thenReturn(Optional.empty());
        when(followRepository.save(any(Follow.class))).thenAnswer(invocation -> {
            Follow follow = invocation.getArgument(0);
            follow.setId(1);
            follow.setCreatedAt(LocalDateTime.now());
            return follow;
        });

        // Act
        var response = followService.followUser(2);

        // Assert
        assertNotNull(response);
        assertEquals(2, response.followeeId());
        verify(followRepository, times(1)).save(any(Follow.class));
    }

    @Test
    @DisplayName("followUser_WithInvalidUserId_ThrowsResourceNotFoundException")
    void followUserWithInvalidUserIdThrowsResourceNotFoundException() {
        // Arrange
        when(appUserRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> followService.followUser(999));
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("followUser_WithSelfFollow_ThrowsUnauthorizedException")
    void followUserWithSelfFollowThrowsUnauthorizedException() {
        // Arrange - trying to follow self
        testFollowee.setId(1); // Same as follower

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> followService.followUser(1));
        verify(followRepository, never()).save(any(Follow.class));
    }

    @Test
    @DisplayName("followUser_WithAlreadyFollowing_ThrowsConflictException")
    void followUserWithAlreadyFollowingThrowsConflictException() {
        // Arrange
        when(appUserRepository.findById(2)).thenReturn(Optional.of(testFollowee));
        when(followRepository.findByFollowerIdAndFolloweeId(1, 2)).thenReturn(Optional.of(testFollow));

        // Act & Assert
        assertThrows(ConflictException.class, () -> followService.followUser(2));
        verify(followRepository, never()).save(any(Follow.class));
    }

    // ============== Unfollow User Tests ==============

    @Test
    @DisplayName("unfollowUser_WithValidFollow_UnfollowsUser")
    void unfollowUserWithValidFollowUnfollowsUser() {
        // Arrange
        when(followRepository.findById(1)).thenReturn(Optional.of(testFollow));

        // Act
        followService.unfollowUser(1);

        // Assert
        verify(followRepository, times(1)).deleteById(1);
    }

    @Test
    @DisplayName("unfollowUser_WithInvalidId_ThrowsResourceNotFoundException")
    void unfollowUserWithInvalidIdThrowsResourceNotFoundException() {
        // Arrange
        when(followRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> followService.unfollowUser(999));
        verify(followRepository, never()).deleteById(anyInt());
    }

    // ============== Check Follow Tests ==============

    @Test
    @DisplayName("isFollowing_WithFollowingUser_ReturnsTrue")
    void isFollowingWithFollowingUserReturnsTrue() {
        // Arrange
        when(followRepository.findByFollowerIdAndFolloweeId(1, 2)).thenReturn(Optional.of(testFollow));

        // Act
        boolean isFollowing = followService.isFollowing(1, 2);

        // Assert
        assertTrue(isFollowing);
    }

    @Test
    @DisplayName("isFollowing_WithNonFollowingUser_ReturnsFalse")
    void isFollowingWithNonFollowingUserReturnsFalse() {
        // Arrange
        when(followRepository.findByFollowerIdAndFolloweeId(1, 2)).thenReturn(Optional.empty());

        // Act
        boolean isFollowing = followService.isFollowing(1, 2);

        // Assert
        assertFalse(isFollowing);
    }

    // ============== List Following Tests ==============

    @Test
    @DisplayName("getUserFollowing_WithPagination_ReturnsPagedFollowing")
    void getUserFollowingWithPaginationReturnsPagedFollowing() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Follow> following = List.of(testFollow);
        Page<Follow> page = new PageImpl<>(following, pageable, 1);

        when(followRepository.findByFollowerId(1, pageable)).thenReturn(page);

        // Act
        var response = followService.getUserFollowing(1, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(followRepository, times(1)).findByFollowerId(1, pageable);
    }

    // ============== List Followers Tests ==============

    @Test
    @DisplayName("getUserFollowers_WithPagination_ReturnsPagedFollowers")
    void getUserFollowersWithPaginationReturnsPagedFollowers() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Follow> followers = List.of(testFollow);
        Page<Follow> page = new PageImpl<>(followers, pageable, 1);

        when(followRepository.findByFolloweeId(2, pageable)).thenReturn(page);

        // Act
        var response = followService.getUserFollowers(2, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(followRepository, times(1)).findByFolloweeId(2, pageable);
    }

    // ============== Count Tests ==============

    @Test
    @DisplayName("countFollowing_ReturnsCorrectCount")
    void countFollowingReturnsCorrectCount() {
        // Arrange
        when(followRepository.countByFollowerId(1)).thenReturn(5L);

        // Act
        long count = followService.countFollowing(1);

        // Assert
        assertEquals(5, count);
    }

    @Test
    @DisplayName("countFollowers_ReturnsCorrectCount")
    void countFollowersReturnsCorrectCount() {
        // Arrange
        when(followRepository.countByFolloweeId(2)).thenReturn(10L);

        // Act
        long count = followService.countFollowers(2);

        // Assert
        assertEquals(10, count);
    }

    @Test
    @DisplayName("countFollowers_WithZeroFollowers_ReturnsZero")
    void countFollowersWithZeroFollowersReturnsZero() {
        // Arrange
        when(followRepository.countByFolloweeId(2)).thenReturn(0L);

        // Act
        long count = followService.countFollowers(2);

        // Assert
        assertEquals(0, count);
    }
}
