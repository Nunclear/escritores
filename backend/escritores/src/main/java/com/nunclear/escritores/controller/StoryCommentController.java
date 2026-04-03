package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.StoryCommentDTO;
import com.nunclear.escritores.service.StoryCommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class StoryCommentController {

    private final StoryCommentService commentService;

    @PostMapping
    public ResponseEntity<StoryCommentDTO> createComment(
            @RequestBody StoryCommentDTO commentDTO,
            @RequestHeader("X-User-Id") Integer userId) {
        StoryCommentDTO createdComment = commentService.createComment(commentDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryCommentDTO> getCommentById(@PathVariable Integer id) {
        StoryCommentDTO comment = commentService.getCommentById(id);
        return ResponseEntity.ok(comment);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<StoryCommentDTO>> getCommentsByStoryId(@PathVariable Integer storyId) {
        List<StoryCommentDTO> comments = commentService.getCommentsByStoryId(storyId);
        return ResponseEntity.ok(comments);
    }

    @GetMapping("/{parentCommentId}/replies")
    public ResponseEntity<List<StoryCommentDTO>> getReplies(@PathVariable Integer parentCommentId) {
        List<StoryCommentDTO> replies = commentService.getReplies(parentCommentId);
        return ResponseEntity.ok(replies);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryCommentDTO> updateComment(
            @PathVariable Integer id,
            @RequestBody StoryCommentDTO commentDTO,
            @RequestHeader("X-User-Id") Integer userId) {
        StoryCommentDTO updatedComment = commentService.updateComment(id, commentDTO, userId);
        return ResponseEntity.ok(updatedComment);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer userId) {
        commentService.deleteComment(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/hide")
    public ResponseEntity<Void> hideComment(@PathVariable Integer id) {
        commentService.hideComment(id);
        return ResponseEntity.ok().build();
    }
}
