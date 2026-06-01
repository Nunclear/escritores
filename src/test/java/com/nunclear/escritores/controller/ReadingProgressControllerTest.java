package com.nunclear.escritores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.SaveReadingProgressRequest;
import com.nunclear.escritores.dto.response.ReadingProgressResponse;
import com.nunclear.escritores.security.JwtAuthenticationFilter;
import com.nunclear.escritores.service.ReadingProgressService;
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

@WebMvcTest(ReadingProgressController.class)
class ReadingProgressControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ReadingProgressService readingProgressService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "USER")
    void guardarProgresDeLectura_exitosamente() throws Exception {
        SaveReadingProgressRequest request = new SaveReadingProgressRequest(
                5, 3, 50.0, false
        );

        ReadingProgressResponse response = new ReadingProgressResponse(
                1, 5, 3, 50.0, false, LocalDateTime.now()
        );

        when(readingProgressService.saveReadingProgress(eq(1), any(SaveReadingProgressRequest.class)))
                .thenReturn(response);

        mockMvc.perform(post("/reading-progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.percentageRead").value(50.0));
    }

    @Test
    @WithMockUser(roles = "USER")
    void obtenerProgresoDelUsuario() throws Exception {
        mockMvc.perform(get("/reading-progress/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void obtenerProgresoDeHistoriaDelUsuario() throws Exception {
        ReadingProgressResponse response = new ReadingProgressResponse(
                1, 5, 3, 75.0, false, LocalDateTime.now()
        );

        when(readingProgressService.getReadingProgress(1, 5)).thenReturn(response);

        mockMvc.perform(get("/reading-progress/story/5/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentageRead").value(75.0));
    }

    @Test
    void obtenerProgresoDeHistoriaSinAutenticacion() throws Exception {
        mockMvc.perform(get("/reading-progress/story/5"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void eliminarProgresoDelUsuario() throws Exception {
        mockMvc.perform(delete("/reading-progress/story/5/me"))
                .andExpect(status().isNoContent());
    }

    @Test
    void guardarProgreso_sinAutenticacion_debeResponder401() throws Exception {
        SaveReadingProgressRequest request = new SaveReadingProgressRequest(
                5, 3, 50.0, false
        );

        mockMvc.perform(post("/reading-progress")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
