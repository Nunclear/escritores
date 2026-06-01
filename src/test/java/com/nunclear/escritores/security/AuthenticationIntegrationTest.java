package com.nunclear.escritores.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.request.LoginRequest;
import com.nunclear.escritores.dto.request.RegisterRequest;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.repository.AppUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource(locations = "classpath:application-test.properties")
class AuthenticationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppUserRepository appUserRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        appUserRepository.deleteAll();
    }

    @Test
    void registrarUsuarioNuevo_exitosamente() throws Exception {
        RegisterRequest request = new RegisterRequest(
                "testuser",
                "test@example.com",
                "Nombre Test",
                "password123"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void registrarUsuarioDuplicado_fallaSiEmailYaExiste() throws Exception {
        AppUser existingUser = new AppUser();
        existingUser.setUsername("testuser");
        existingUser.setEmail("test@example.com");
        existingUser.setPassword(passwordEncoder.encode("password123"));
        appUserRepository.save(existingUser);

        RegisterRequest request = new RegisterRequest(
                "testuser2",
                "test@example.com",
                "Nombre Test",
                "password123"
        );

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void loginConCredencialesValidas_exitosamente() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        appUserRepository.save(user);

        LoginRequest request = new LoginRequest(
                "testuser",
                "password123"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    @Test
    void loginConCredencialesInvalidas_falla() throws Exception {
        LoginRequest request = new LoginRequest(
                "nonexistent",
                "wrongpassword"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginConContraseñaIncorrecta_falla() throws Exception {
        AppUser user = new AppUser();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        appUserRepository.save(user);

        LoginRequest request = new LoginRequest(
                "testuser",
                "wrongpassword"
        );

        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void accesarEndpointProtegido_sinToken_debeResponder401() throws Exception {
        mockMvc.perform(get("/stories"))
                .andExpect(status().isUnauthorized());
    }
}
