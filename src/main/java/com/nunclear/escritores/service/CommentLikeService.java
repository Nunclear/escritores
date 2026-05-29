package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.CommentLike;
import com.nunclear.escritores.exception.BadRequestException;
import com.nunclear.escritores.repository.CommentLikeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentLikeService {

    private static final String ALREADY_LIKED = "Ya has dado like a este comentario";
    private static final String NOT_LIKED = "No has dado like a este comentario";

    private final CommentLikeRepository commentLikeRepository;

    public CommentLike likeComment(Integer commentId, Integer userId) {
        if (commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new BadRequestException(ALREADY_LIKED);
        }

        CommentLike like = new CommentLike();
        like.setCommentId(commentId);
        like.setUserId(userId);

        return commentLikeRepository.save(like);
    }

    public void unlikeComment(Integer commentId, Integer userId) {
        if (!commentLikeRepository.existsByCommentIdAndUserId(commentId, userId)) {
            throw new BadRequestException(NOT_LIKED);
        }

        commentLikeRepository.deleteByCommentIdAndUserId(commentId, userId);
    }

    public long getLikeCount(Integer commentId) {
        return commentLikeRepository.countByCommentId(commentId);
    }

    public boolean hasUserLiked(Integer commentId, Integer userId) {
        return commentLikeRepository.existsByCommentIdAndUserId(commentId, userId);
    }

    public List<CommentLike> getLikesByComment(Integer commentId) {
        return commentLikeRepository.findByCommentId(commentId);
    }

    public List<CommentLike> getLikesByUser(Integer userId) {
        return commentLikeRepository.findByUserId(userId);
    }

    public void deleteCommentLikes(Integer commentId) {
        List<CommentLike> likes = commentLikeRepository.findByCommentId(commentId);
        commentLikeRepository.deleteAll(likes);
    }

    public void deleteUserLikes(Integer userId) {
        List<CommentLike> likes = commentLikeRepository.findByUserId(userId);
        commentLikeRepository.deleteAll(likes);
    }
}
