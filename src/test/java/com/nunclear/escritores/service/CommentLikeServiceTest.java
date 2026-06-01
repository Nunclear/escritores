package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.response.CommentLikeResponse;
import com.nunclear.escritores.entity.CommentLike;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.CommentLikeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentLikeServiceTest {

    @Mock
    private CommentLikeRepository commentLikeRepository;

    @InjectMocks
    private CommentLikeService commentLikeService;

    private CommentLike like;

    @BeforeEach
    void setUp() {
        like = new CommentLike();
        like.setId(1);
        like.setCommentId(10);
        like.setUserId(5);
        like.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void agregarLike_alComentario() {
        when(commentLikeRepository.findByCommentIdAndUserId(10, 5))
                .thenReturn(Optional.empty());
        when(commentLikeRepository.save(any(CommentLike.class))).thenReturn(like);

        CommentLikeResponse response = commentLikeService.addLike(10, 5);

        assertNotNull(response);
        verify(commentLikeRepository, times(1)).save(any(CommentLike.class));
    }

    @Test
    void agregarLike_yaExiste() {
        when(commentLikeRepository.findByCommentIdAndUserId(10, 5))
                .thenReturn(Optional.of(like));

        assertThrows(IllegalArgumentException.class, () -> {
            commentLikeService.addLike(10, 5);
        });
    }

    @Test
    void eliminarLike_delComentario() {
        when(commentLikeRepository.findByCommentIdAndUserId(10, 5))
                .thenReturn(Optional.of(like));

        commentLikeService.removeLike(10, 5);

        verify(commentLikeRepository, times(1)).delete(any(CommentLike.class));
    }

    @Test
    void eliminarLike_noExiste() {
        when(commentLikeRepository.findByCommentIdAndUserId(999, 999))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            commentLikeService.removeLike(999, 999);
        });
    }

    @Test
    void contarLikesDelComentario() {
        when(commentLikeRepository.countByCommentId(10)).thenReturn(5L);

        long count = commentLikeService.countLikes(10);

        assertEquals(5L, count);
    }

    @Test
    void verificarSiUsuarioYaDejoLike() {
        when(commentLikeRepository.existsByCommentIdAndUserId(10, 5))
                .thenReturn(true);

        boolean liked = commentLikeService.hasUserLiked(10, 5);

        assertTrue(liked);
    }

    @Test
    void verificarSiUsuarioNODejoLike() {
        when(commentLikeRepository.existsByCommentIdAndUserId(999, 999))
                .thenReturn(false);

        boolean liked = commentLikeService.hasUserLiked(999, 999);

        assertFalse(liked);
    }

    @Test
    void listarLikesDelComentario_conPaginacion() {
        List<CommentLike> likes = new ArrayList<>();
        likes.add(like);
        Page<CommentLike> page = new PageImpl<>(likes, PageRequest.of(0, 10), 1);

        when(commentLikeRepository.findByCommentIdOrderByCreatedAtDesc(eq(10), any()))
                .thenReturn(page);

        Page<CommentLikeResponse> result = commentLikeService.getCommentLikes(
                10, PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }
}
