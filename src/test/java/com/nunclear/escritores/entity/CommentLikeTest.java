package com.nunclear.escritores.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentLikeTest {

    @Test
    void prePersist_deberiaAsignarTimestamp() {
        CommentLike like = new CommentLike();

        like.prePersist();

        assertNotNull(like.getCreatedAt());
    }

    @Test
    void crearCommentLike_conDatosValidos() {
        CommentLike like = new CommentLike();
        like.setCommentId(1);
        like.setUserId(10);

        assertEquals(1, like.getCommentId());
        assertEquals(10, like.getUserId());
    }

    @Test
    void comparacionDeCommentLikes_mismosIds() {
        CommentLike like1 = new CommentLike();
        like1.setCommentId(1);
        like1.setUserId(10);

        CommentLike like2 = new CommentLike();
        like2.setCommentId(1);
        like2.setUserId(10);

        // Mismo usuario, mismo comentario
        assertEquals(like1.getCommentId(), like2.getCommentId());
        assertEquals(like1.getUserId(), like2.getUserId());
    }

    @Test
    void CommentLikeDebeTenerReferenciasAEntidadesRelacionadas() {
        CommentLike like = new CommentLike();
        like.setCommentId(5);
        like.setUserId(20);

        assertNotNull(like.getCommentId());
        assertNotNull(like.getUserId());
    }
}
