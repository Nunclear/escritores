package com.nunclear.escritores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.CreateGenreRequest;
import com.nunclear.escritores.dto.response.GenreResponse;
import com.nunclear.escritores.security.JwtAuthenticationFilter;
import com.nunclear.escritores.service.GenreService;
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

@WebMvcTest(GenreController.class)
class GenreControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private GenreService genreService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "ADMIN")
    void crearGenero_exitosamente() throws Exception {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                "Historias de fantasía"
        );

        GenreResponse response = new GenreResponse(
                1,
                "Fantasía",
                "fantasia",
                "Historias de fantasía",
                true
        );

        when(genreService.createGenre(any(CreateGenreRequest.class))).thenReturn(response);

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fantasía"))
                .andExpect(jsonPath("$.slug").value("fantasia"));
    }

    @Test
    void obtenerGenroPorId_exitosamente() throws Exception {
        GenreResponse response = new GenreResponse(
                1,
                "Fantasía",
                "fantasia",
                "Historias de fantasía",
                true
        );

        when(genreService.getGenreById(1)).thenReturn(response);

        mockMvc.perform(get("/genres/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Fantasía"));
    }

    @Test
    void obtenerGenroPorSlug_exitosamente() throws Exception {
        GenreResponse response = new GenreResponse(
                1,
                "Ciencia Ficción",
                "science-fiction",
                "Historias futuristas",
                true
        );

        when(genreService.getGenreBySlug("science-fiction")).thenReturn(response);

        mockMvc.perform(get("/genres/slug/science-fiction"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.slug").value("science-fiction"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void actualizarGenero_exitosamente() throws Exception {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía Épica",
                "Grandes historias"
        );

        GenreResponse response = new GenreResponse(
                1,
                "Fantasía Épica",
                "fantasia-epica",
                "Grandes historias",
                true
        );

        when(genreService.updateGenre(eq(1), any(CreateGenreRequest.class)))
                .thenReturn(response);

        mockMvc.perform(put("/genres/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fantasía Épica"));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void eliminarGenero_exitosamente() throws Exception {
        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void obtenerGenros_sinAutenticacion_debeResponder401() throws Exception {
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk());
    }
}
