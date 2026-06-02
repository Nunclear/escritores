package com.nunclear.escritores.service;

import com.nunclear.escritores.config.TestDataBuilder;
import com.nunclear.escritores.dto.request.CreateCommentRequest;
import com.nunclear.escritores.dto.request.UpdateCommentRequest;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Comment;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.exception.UnauthorizedException;
import com.nunclear.escritores.repository.CommentRepository;
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
@DisplayName("CommentService Unit Tests")
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private StoryRepository storyRepository;

    @InjectMocks
    private CommentService commentService;

    private AppUser testAuthor;
    private AppUser testCommentator;
    private Story testStory;
    private Comment testComment;

    @BeforeEach
    void setUp() {
        testAuthor = TestDataBuilder.buildAppUser("author", "author@example.com");
        testAuthor.setId(1);

        testCommentator = TestDataBuilder.buildAppUser("commentator", "commentator@example.com");
        testCommentator.setId(2);

        testStory = TestDataBuilder.buildStory(testAuthor, "Test Story");
        testStory.setId(1);

        testComment = TestDataBuilder.buildComment(testStory, testCommentator, "Test comment");
        testComment.setId(1);

        mockAuthenticatedUser(testCommentator);
    }

    private void mockAuthenticatedUser(AppUser user) {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        CustomUserDetails userDetails = new CustomUserDetails(user);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // ============== Create Comment Tests ==============

    @Test
    @DisplayName("createComment_WithValidData_ReturnsCreatedComment")
    void createCommentWithValidDataReturnsCreatedComment() {
        // Arrange
        CreateCommentRequest request = CreateCommentRequest.builder()
                .storyId(1)
                .content("Great story!")
                .build();

        when(storyRepository.findById(1)).thenReturn(Optional.of(testStory));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(1);
            comment.setCreatedAt(LocalDateTime.now());
            return comment;
        });

        // Act
        var response = commentService.createComment(request);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("Great story!", response.content());
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    @DisplayName("createComment_WithInvalidStoryId_ThrowsResourceNotFoundException")
    void createCommentWithInvalidStoryIdThrowsResourceNotFoundException() {
        // Arrange
        CreateCommentRequest request = CreateCommentRequest.builder()
                .storyId(999)
                .content("Comment")
                .build();

        when(storyRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.createComment(request));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("createComment_WithEmptyContent_ThrowsBadRequestException")
    void createCommentWithEmptyContentThrowsBadRequestException() {
        // Arrange
        CreateCommentRequest request = CreateCommentRequest.builder()
                .storyId(1)
                .content("")
                .build();

        // Act & Assert
        assertThrows(BadRequestException.class, () -> commentService.createComment(request));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    @Test
    @DisplayName("createComment_WithReplyTo_CreatesNestedComment")
    void createCommentWithReplyToCreatesNestedComment() {
        // Arrange
        CreateCommentRequest request = CreateCommentRequest.builder()
                .storyId(1)
                .content("Reply to comment")
                .replyToId(1)
                .build();

        when(storyRepository.findById(1)).thenReturn(Optional.of(testStory));
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> {
            Comment comment = invocation.getArgument(0);
            comment.setId(2);
            comment.setCreatedAt(LocalDateTime.now());
            return comment;
        });

        // Act
        var response = commentService.createComment(request);

        // Assert
        assertNotNull(response);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    // ============== Get Comment Tests ==============

    @Test
    @DisplayName("getCommentById_WithValidId_ReturnsComment")
    void getCommentByIdWithValidIdReturnsComment() {
        // Arrange
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        // Act
        var response = commentService.getCommentById(1);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.id());
        assertEquals("Test comment", response.content());
        verify(commentRepository, times(1)).findById(1);
    }

    @Test
    @DisplayName("getCommentById_WithInvalidId_ThrowsResourceNotFoundException")
    void getCommentByIdWithInvalidIdThrowsResourceNotFoundException() {
        // Arrange
        when(commentRepository.findById(999)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> commentService.getCommentById(999));
    }

    // ============== Update Comment Tests ==============

    @Test
    @DisplayName("updateComment_WithOwnership_UpdatesComment")
    void updateCommentWithOwnershipUpdatesComment() {
        // Arrange
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("Updated comment")
                .build();

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        var response = commentService.updateComment(1, request);

        // Assert
        assertNotNull(response);
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertEquals("Updated comment", captor.getValue().getContent());
    }

    @Test
    @DisplayName("updateComment_WithoutOwnership_ThrowsUnauthorizedException")
    void updateCommentWithoutOwnershipThrowsUnauthorizedException() {
        // Arrange
        testComment.setAuthor(testAuthor); // Different author
        UpdateCommentRequest request = UpdateCommentRequest.builder()
                .content("Updated")
                .build();

        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.updateComment(1, request));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    // ============== Delete Comment Tests ==============

    @Test
    @DisplayName("deleteComment_WithOwnership_SoftDeletesComment")
    void deleteCommentWithOwnershipSoftDeletesComment() {
        // Arrange
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        commentService.deleteComment(1);

        // Assert
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertNotNull(captor.getValue().getDeletedAt());
    }

    @Test
    @DisplayName("deleteComment_WithoutOwnership_ThrowsUnauthorizedException")
    void deleteCommentWithoutOwnershipThrowsUnauthorizedException() {
        // Arrange
        testComment.setAuthor(testAuthor); // Different author
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.deleteComment(1));
        verify(commentRepository, never()).save(any(Comment.class));
    }

    // ============== List Comments Tests ==============

    @Test
    @DisplayName("getStoryComments_WithPagination_ReturnsPagedComments")
    void getStoryCommentsWithPaginationReturnsPagedComments() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        List<Comment> comments = List.of(testComment);
        Page<Comment> page = new PageImpl<>(comments, pageable, 1);

        when(commentRepository.findByStoryIdAndDeletedAtIsNull(1, pageable)).thenReturn(page);

        // Act
        var response = commentService.getStoryComments(1, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        verify(commentRepository, times(1)).findByStoryIdAndDeletedAtIsNull(1, pageable);
    }

    @Test
    @DisplayName("getCommentReplies_WithValidCommentId_ReturnsReplies")
    void getCommentRepliesWithValidCommentIdReturnsReplies() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Comment reply = TestDataBuilder.buildComment(testStory, testCommentator, "Reply");
        reply.setId(2);
        reply.setReplyTo(testComment);
        List<Comment> replies = List.of(reply);
        Page<Comment> page = new PageImpl<>(replies, pageable, 1);

        when(commentRepository.findByReplyToIdAndDeletedAtIsNull(1, pageable)).thenReturn(page);

        // Act
        var response = commentService.getCommentReplies(1, 0, 10);

        // Assert
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
    }

    // ============== Hide/Show Comment Tests ==============

    @Test
    @DisplayName("hideComment_WithModeratorRole_HidesComment")
    void hideCommentWithModeratorRoleHidesComment() {
        // Arrange
        AppUser moderator = TestDataBuilder.buildModeratorUser();
        mockAuthenticatedUser(moderator);
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));
        when(commentRepository.save(any(Comment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        commentService.hideComment(1);

        // Assert
        ArgumentCaptor<Comment> captor = ArgumentCaptor.forClass(Comment.class);
        verify(commentRepository).save(captor.capture());
        assertTrue(captor.getValue().isHidden());
    }

    @Test
    @DisplayName("hideComment_WithoutModeratorRole_ThrowsUnauthorizedException")
    void hideCommentWithoutModeratorRoleThrowsUnauthorizedException() {
        // Arrange
        when(commentRepository.findById(1)).thenReturn(Optional.of(testComment));

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> commentService.hideComment(1));
    }
}
