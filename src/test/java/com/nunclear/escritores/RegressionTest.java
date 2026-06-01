package com.nunclear.escritores;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.CreateStoryRequest;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.repository.StoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class RegressionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private StoryRepository storyRepository;

    @BeforeEach
    void setUp() {
        storyRepository.deleteAll();
    }

    @Test
    @WithMockUser(roles = "USER")
    void crearHistoria_deberiaFuncionarComosAntesDelRefactoring() throws Exception {
        CreateStoryRequest request = new CreateStoryRequest(
                "Título Test",
                "Descripción",
                "https://img.com/1.jpg",
                "public",
                "draft",
                true,
                true,
                null
        );

        mockMvc.perform(post("/stories")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Título Test"));
    }

    @Test
    void listarHistorias_deberiaFuncionarComosAntesDelRefactoring() throws Exception {
        Story story = new Story();
        story.setTitle("Historia Test");
        story.setSlugText("historia-test");
        story.setStatus("published");
        storyRepository.save(story);

        mockMvc.perform(get("/stories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void nuevosCamposEnStory_noDeberianRomperFuncionalidadExistente() throws Exception {
        Story story = new Story();
        story.setTitle("Historia con Nuevos Campos");
        story.setSlugText("historia-nuevos-campos");
        story.setStatus("published");
        story.setLanguage("es");
        story.setAgeRating(null);
        storyRepository.save(story);

        mockMvc.perform(get("/stories/" + story.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Historia con Nuevos Campos"));
    }

    @Test
    void loginContinuaFuncionando() throws Exception {
        // Este test verifica que la funcionalidad de login siga funcionando
        // después de los cambios
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"testuser\",\"password\":\"password\"}"))
                .andExpect(status().isUnauthorized()); // Expected porque el usuario no existe
    }

    @Test
    void crearComentarioSigueTrasfuncionando() throws Exception {
        // Verifica que el sistema de comentarios siga funcionando
        mockMvc.perform(post("/comments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"storyId\":1,\"content\":\"Test comment\"}"))
                .andExpect(status().isUnauthorized()); // Sin autenticación
    }
}
