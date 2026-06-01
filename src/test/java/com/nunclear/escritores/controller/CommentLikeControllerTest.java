package com.nunclear.escritores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.CommentLikeRequest;
import com.nunclear.escritores.dto.response.CommentLikeResponse;
import com.nunclear.escritores.security.JwtAuthenticationFilter;
import com.nunclear.escritores.service.CommentLikeService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CommentLikeController.class)
class CommentLikeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private CommentLikeService commentLikeService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "USER")
    void agregarLikeAlComentario() throws Exception {
        CommentLikeRequest request = new CommentLikeRequest(10);

        CommentLikeResponse response = new CommentLikeResponse(
                1, 10, 1, LocalDateTime.now()
        );

        when(commentLikeService.addLike(eq(10), any()))
                .thenReturn(response);

        mockMvc.perform(post("/comments/10/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.commentId").value(10));
    }

    @Test
    @WithMockUser(roles = "USER")
    void removerLikeDelComentario() throws Exception {
        mockMvc.perform(delete("/comments/10/likes"))
                .andExpect(status().isNoContent());
    }

    @Test
    void contarLikesDelComentario() throws Exception {
        when(commentLikeService.countLikes(10)).thenReturn(5L);

        mockMvc.perform(get("/comments/10/likes/count"))
                .andExpect(status().isOk());
    }

    @Test
    void verificarSiUsuarioDejoLike() throws Exception {
        when(commentLikeService.hasUserLiked(eq(10), any())).thenReturn(true);

        mockMvc.perform(get("/comments/10/likes/user/1"))
                .andExpect(status().isOk());
    }

    @Test
    void listarLikesDelComentario() throws Exception {
        mockMvc.perform(get("/comments/10/likes"))
                .andExpect(status().isOk());
    }

    @Test
    void agregarLike_sinAutenticacion_debeResponder401() throws Exception {
        CommentLikeRequest request = new CommentLikeRequest(10);

        mockMvc.perform(post("/comments/10/likes")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
