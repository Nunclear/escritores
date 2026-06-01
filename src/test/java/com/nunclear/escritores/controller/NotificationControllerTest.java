package com.nunclear.escritores.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nunclear.escritores.dto.response.NotificationResponse;
import com.nunclear.escritores.enums.NotificationType;
import com.nunclear.escritores.security.JwtAuthenticationFilter;
import com.nunclear.escritores.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(NotificationController.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private NotificationService notificationService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    @WithMockUser(roles = "USER")
    void obtenerNotificacionesDelUsuario() throws Exception {
        mockMvc.perform(get("/notifications/me"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void obtenerNotificacionPorId_exitosamente() throws Exception {
        NotificationResponse response = new NotificationResponse(
                1,
                NotificationType.STORY_PUBLISHED,
                "Nueva Historia",
                "Tu historia fue publicada",
                false,
                LocalDateTime.now(),
                null,
                null
        );

        when(notificationService.getNotificationById(1)).thenReturn(response);

        mockMvc.perform(get("/notifications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.title").value("Nueva Historia"));
    }

    @Test
    @WithMockUser(roles = "USER")
    void marcarNotificacionComoLeida() throws Exception {
        mockMvc.perform(post("/notifications/1/read"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void marcarTodasLasNotificacionesComoLeidas() throws Exception {
        mockMvc.perform(post("/notifications/me/read-all"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void contarNotificacionesNoLeidas() throws Exception {
        when(notificationService.getUnreadCount(1)).thenReturn(3L);

        mockMvc.perform(get("/notifications/me/unread-count"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void eliminarNotificacion_exitosamente() throws Exception {
        mockMvc.perform(delete("/notifications/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void obtenerNotificaciones_sinAutenticacion_debeResponder401() throws Exception {
        mockMvc.perform(get("/notifications/me"))
                .andExpect(status().isUnauthorized());
    }
}
