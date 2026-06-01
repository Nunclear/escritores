package com.nunclear.escritores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.RestoreChapterVersionRequest;
import com.nunclear.escritores.dto.response.ChapterVersionResponse;
import com.nunclear.escritores.security.JwtAuthenticationFilter;
import com.nunclear.escritores.service.ChapterVersionService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChapterVersionController.class)
class ChapterVersionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ChapterVersionService chapterVersionService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void listarVersionesDelCapitulo() throws Exception {
        mockMvc.perform(get("/chapters/1/versions"))
                .andExpect(status().isOk());
    }

    @Test
    void obtenerVersionEspecificaDelCapitulo_exitosamente() throws Exception {
        ChapterVersionResponse response = new ChapterVersionResponse(
                1, 1, 1, "Contenido", "Primera versión", LocalDateTime.now()
        );

        when(chapterVersionService.getChapterVersion(1, 1)).thenReturn(response);

        mockMvc.perform(get("/chapters/1/versions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versionNumber").value(1));
    }

    @Test
    void obtenerUltimaVersionDelCapitulo() throws Exception {
        ChapterVersionResponse response = new ChapterVersionResponse(
                1, 1, 3, "Contenido actualizado", "Última versión", LocalDateTime.now()
        );

        when(chapterVersionService.getLatestVersion(1)).thenReturn(response);

        mockMvc.perform(get("/chapters/1/versions/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.versionNumber").value(3));
    }

    @Test
    @WithMockUser(roles = "USER")
    void restaurarVersionAnteriorDelCapitulo() throws Exception {
        RestoreChapterVersionRequest request = new RestoreChapterVersionRequest(2);

        mockMvc.perform(post("/chapters/1/versions/2/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void restaurarVersion_sinAutenticacion_debeResponder401() throws Exception {
        RestoreChapterVersionRequest request = new RestoreChapterVersionRequest(1);

        mockMvc.perform(post("/chapters/1/versions/1/restore")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }
}
