package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.response.CommentLikeResponse;
import com.nunclear.escritores.entity.CommentLike;
import com.nunclear.escritores.security.CustomUserDetails;
import com.nunclear.escritores.service.CommentLikeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comments/{commentId}/likes")
@RequiredArgsConstructor
public class CommentLikeController {

    private final CommentLikeService commentLikeService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public CommentLikeResponse likeComment(@PathVariable Integer commentId) {
        Integer userId = getCurrentUserId();
        CommentLike like = commentLikeService.likeComment(commentId, userId);
        return mapToResponse(like);
    }

    @DeleteMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public void unlikeComment(@PathVariable Integer commentId) {
        Integer userId = getCurrentUserId();
        commentLikeService.unlikeComment(commentId, userId);
    }

    @GetMapping("/count")
    public long getLikeCount(@PathVariable Integer commentId) {
        return commentLikeService.getLikeCount(commentId);
    }

    @GetMapping("/user/{userId}")
    public boolean hasUserLiked(
            @PathVariable Integer commentId,
            @PathVariable Integer userId
    ) {
        return commentLikeService.hasUserLiked(commentId, userId);
    }

    @GetMapping
    public List<CommentLikeResponse> getLikes(@PathVariable Integer commentId) {
        List<CommentLike> likes = commentLikeService.getLikesByComment(commentId);
        return likes.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private CommentLikeResponse mapToResponse(CommentLike like) {
        return new CommentLikeResponse(
                like.getId(),
                like.getCommentId(),
                like.getUserId(),
                like.getCreatedAt()
        );
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.id();
    }
}
