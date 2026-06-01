package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.response.NotificationResponse;
import com.nunclear.escritores.entity.Notification;
import com.nunclear.escritores.enums.NotificationType;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.NotificationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private NotificationService notificationService;

    private Notification notification;

    @BeforeEach
    void setUp() {
        notification = new Notification();
        notification.setId(1);
        notification.setUserId(10);
        notification.setType(NotificationType.STORY_PUBLISHED);
        notification.setTitle("Nueva Historia");
        notification.setMessage("Tu historia fue publicada");
        notification.setRead(false);
        notification.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void crearNotificacion_exitosamente() {
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        Notification result = notificationService.createNotification(notification);

        assertNotNull(result);
        assertEquals(NotificationType.STORY_PUBLISHED, result.getType());
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void obtenerNotificacionPorId_exitosamente() {
        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

        NotificationResponse response = notificationService.getNotificationById(1);

        assertNotNull(response);
        assertEquals("Nueva Historia", response.title());
    }

    @Test
    void obtenerNotificacionPorId_lanzaExcepcion() {
        when(notificationRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            notificationService.getNotificationById(999);
        });
    }

    @Test
    void obtenerNotificacionesDelUsuario_conPaginacion() {
        List<Notification> notifications = new ArrayList<>();
        notifications.add(notification);
        Page<Notification> page = new PageImpl<>(notifications, PageRequest.of(0, 10), 1);

        when(notificationRepository.findByUserIdOrderByCreatedAtDesc(eq(10), any()))
                .thenReturn(page);

        Page<NotificationResponse> result = notificationService.getUserNotifications(
                10, PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void marcarNotificacionComoLeida_exitosamente() {
        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));
        when(notificationRepository.save(any(Notification.class))).thenReturn(notification);

        notificationService.markAsRead(1);

        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void marcarTodasLasNotificacionesComoLeidas() {
        when(notificationRepository.findByUserIdAndReadFalse(10))
                .thenReturn(new ArrayList<>());

        notificationService.markAllAsRead(10);

        verify(notificationRepository, times(1)).findByUserIdAndReadFalse(10);
    }

    @Test
    void contarNotificacionesNoLeidas() {
        when(notificationRepository.countByUserIdAndReadFalse(10)).thenReturn(5L);

        long count = notificationService.getUnreadCount(10);

        assertEquals(5L, count);
    }

    @Test
    void eliminarNotificacion_exitosamente() {
        when(notificationRepository.findById(1)).thenReturn(Optional.of(notification));

        notificationService.deleteNotification(1);

        verify(notificationRepository, times(1)).deleteById(1);
    }
}
