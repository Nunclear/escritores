package com.nunclear.escritores.entity;

import com.nunclear.escritores.enums.NotificationType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class NotificationTest {

    @Test
    void prePersist_deberiaAsignarTimestamps() {
        Notification notification = new Notification();
        notification.setType(NotificationType.STORY_PUBLISHED);

        notification.prePersist();

        assertNotNull(notification.getCreatedAt());
        assertNotNull(notification.getUpdatedAt());
        assertEquals(notification.getCreatedAt(), notification.getUpdatedAt());
    }

    @Test
    void crearNotification_conDatosValidos() {
        Notification notification = new Notification();
        notification.setType(NotificationType.NEW_COMMENT);
        notification.setTitle("Nuevo comentario");
        notification.setMessage("Alguien comentó en tu historia");
        notification.setRead(false);

        assertEquals(NotificationType.NEW_COMMENT, notification.getType());
        assertEquals("Nuevo comentario", notification.getTitle());
        assertEquals("Alguien comentó en tu historia", notification.getMessage());
        assertFalse(notification.isRead());
    }

    @Test
    void marcarNotificationComoLeida() {
        Notification notification = new Notification();
        notification.setRead(false);

        notification.setRead(true);

        assertTrue(notification.isRead());
    }

    @Test
    void notificationPuedeContenerRelacionesOpcionales() {
        Notification notification = new Notification();
        notification.setType(NotificationType.FOLLOWED);
        notification.setRelatedStoryId(123);
        notification.setRelatedUserId(456);

        assertEquals(123, notification.getRelatedStoryId());
        assertEquals(456, notification.getRelatedUserId());
    }
}
