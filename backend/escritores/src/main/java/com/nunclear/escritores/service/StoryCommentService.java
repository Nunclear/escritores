package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.StoryCommentDTO;
import com.nunclear.escritores.entity.StoryComment;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.Chapter;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.entity.Visibility;
import com.nunclear.escritores.repository.StoryCommentRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.ChapterRepository;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryCommentService {

    private final StoryCommentRepository commentRepository;
    private final StoryRepository storyRepository;
    private final ChapterRepository chapterRepository;
    private final AppUserRepository userRepository;

    public StoryCommentDTO createComment(StoryCommentDTO commentDTO, Integer userId) {
        Story story = storyRepository.findById(commentDTO.getStoryId())
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (!story.getAllowFeedback()) {
            throw new IllegalArgumentException("Comments are disabled for this story");
        }

        AppUser author = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        StoryComment comment = StoryComment.builder()
                .story(story)
                .authorUser(author)
                .content(commentDTO.getContent())
                .visibility(Visibility.VISIBLE)
                .build();

        if (commentDTO.getChapterId() != null) {
            Chapter chapter = chapterRepository.findById(commentDTO.getChapterId())
                    .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
            comment.setChapter(chapter);
        }

        if (commentDTO.getParentCommentId() != null) {
            StoryComment parentComment = commentRepository.findById(commentDTO.getParentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Parent comment not found"));
            comment.setParentComment(parentComment);
        }

        StoryComment savedComment = commentRepository.save(comment);
        return mapToDTO(savedComment);
    }

    public StoryCommentDTO getCommentById(Integer id) {
        return commentRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
    }

    public List<StoryCommentDTO> getCommentsByStoryId(Integer storyId) {
        return commentRepository.findByStoryIdAndVisibility(storyId, Visibility.VISIBLE).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<StoryCommentDTO> getReplies(Integer parentCommentId) {
        return commentRepository.findByParentCommentId(parentCommentId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StoryCommentDTO updateComment(Integer id, StoryCommentDTO commentDTO, Integer userId) {
        StoryComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthorUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of this comment");
        }

        if (commentDTO.getContent() != null) {
            comment.setContent(commentDTO.getContent());
            comment.setEditedAt(LocalDateTime.now());
        }

        StoryComment updatedComment = commentRepository.save(comment);
        return mapToDTO(updatedComment);
    }

    public void deleteComment(Integer id, Integer userId) {
        StoryComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getAuthorUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the author of this comment");
        }

        comment.setDeletedAt(LocalDateTime.now());
        comment.setVisibility(Visibility.DELETED);
        commentRepository.save(comment);
    }

    public void hideComment(Integer id) {
        StoryComment comment = commentRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        comment.setVisibility(Visibility.HIDDEN);
        commentRepository.save(comment);
    }

    private StoryCommentDTO mapToDTO(StoryComment comment) {
        return StoryCommentDTO.builder()
                .id(comment.getId())
                .storyId(comment.getStory().getId())
                .chapterId(comment.getChapter() != null ? comment.getChapter().getId() : null)
                .authorUserId(comment.getAuthorUser().getId())
                .authorUserName(comment.getAuthorUser().getDisplayName())
                .parentCommentId(comment.getParentComment() != null ? comment.getParentComment().getId() : null)
                .content(comment.getContent())
                .visibility(comment.getVisibility())
                .editedAt(comment.getEditedAt())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getUpdatedAt())
                .deletedAt(comment.getDeletedAt())
                .build();
    }
}
