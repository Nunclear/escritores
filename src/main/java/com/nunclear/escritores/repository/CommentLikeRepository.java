package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Integer> {

    Optional<CommentLike> findByCommentIdAndUserId(Integer commentId, Integer userId);

    List<CommentLike> findByCommentId(Integer commentId);

    List<CommentLike> findByUserId(Integer userId);

    long countByCommentId(Integer commentId);

    void deleteByCommentIdAndUserId(Integer commentId, Integer userId);

    boolean existsByCommentIdAndUserId(Integer commentId, Integer userId);
}
