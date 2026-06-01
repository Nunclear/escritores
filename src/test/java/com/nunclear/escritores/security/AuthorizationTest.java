package com.nunclear.escritores.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.CreateGenreRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthorizationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @WithMockUser(roles = "ADMIN")
    void usuarioAdmin_puedeCrearGenero() throws Exception {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                "Historias de fantasía"
        );

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioNormal_NOPuedeCrearGenero() throws Exception {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                "Historias de fantasía"
        );

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    void usuarioSinAutenticacion_NOPuedeCrearGenero() throws Exception {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                "Historias de fantasía"
        );

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void usuarioAdmin_puedeEliminarGenero() throws Exception {
        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioNormal_NOPuedeEliminarGenero() throws Exception {
        mockMvc.perform(delete("/genres/1"))
                .andExpect(status().isForbidden());
    }

    @Test
    @WithMockUser(roles = "USER")
    void usuarioAutenticado_puedeVerSusNotificaciones() throws Exception {
        mockMvc.perform(get("/notifications/me"))
                .andExpect(status().isOk());
    }

    @Test
    void usuarioSinAutenticacion_NOPuedeVerNotificaciones() throws Exception {
        mockMvc.perform(get("/notifications/me"))
                .andExpect(status().isUnauthorized());
    }
}
