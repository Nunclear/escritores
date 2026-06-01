package com.nunclear.escritores.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.CreateGenreRequest;
import com.nunclear.escritores.entity.Genre;
import com.nunclear.escritores.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
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
class GenreApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private GenreRepository genreRepository;

    @BeforeEach
    void setUp() {
        genreRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void crudCompleto_genero() throws Exception {
        // Create
        CreateGenreRequest createRequest = new CreateGenreRequest(
                "Fantasía",
                "Historias de fantasía"
        );

        String createResponse = mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Fantasía"))
                .andReturn().getResponse().getContentAsString();

        Integer genreId = objectMapper.readTree(createResponse).get("id").asInt();

        // Read
        mockMvc.perform(get("/genres/" + genreId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fantasía"));

        // Update
        CreateGenreRequest updateRequest = new CreateGenreRequest(
                "Fantasía Épica",
                "Grandes historias"
        );

        mockMvc.perform(put("/genres/" + genreId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Fantasía Épica"));

        // Delete
        mockMvc.perform(delete("/genres/" + genreId))
                .andExpect(status().isNoContent());

        // Verify deletion
        mockMvc.perform(get("/genres/" + genreId))
                .andExpect(status().isNotFound());
    }

    @Test
    void listarGeneros_conResultados() throws Exception {
        // Create some genres
        Genre g1 = new Genre();
        g1.setName("Fantasía");
        g1.setSlug("fantasia");
        g1.setActive(true);
        genreRepository.save(g1);

        Genre g2 = new Genre();
        g2.setName("Ciencia Ficción");
        g2.setSlug("science-fiction");
        g2.setActive(true);
        genreRepository.save(g2);

        // List genres
        mockMvc.perform(get("/genres"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2));
    }

    @Test
    void obtenerGenroPorSlug_exitosamente() throws Exception {
        Genre genre = new Genre();
        genre.setName("Romance");
        genre.setSlug("romance");
        genre.setDescription("Historias de amor");
        genre.setActive(true);
        genreRepository.save(genre);

        mockMvc.perform(get("/genres/slug/romance"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Romance"));
    }

    @Test
    void validarCodigosDeResponse() throws Exception {
        // GET existente
        mockMvc.perform(get("/genres"))
                .andExpect(status().is(200));

        // POST sin autenticación
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía",
                "Desc"
        );

        mockMvc.perform(post("/genres")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is(401));

        // DELETE no encontrado
        mockMvc.perform(delete("/genres/999"))
                .andExpect(status().is(404));
    }
}
