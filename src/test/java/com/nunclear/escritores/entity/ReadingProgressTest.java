package com.nunclear.escritores.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReadingProgressTest {

    @Test
    void prePersist_deberiaAsignarTimestamps() {
        ReadingProgress progress = new ReadingProgress();

        progress.prePersist();

        assertNotNull(progress.getCreatedAt());
        assertNotNull(progress.getUpdatedAt());
    }

    @Test
    void crearReadingProgress_conDatosValidos() {
        ReadingProgress progress = new ReadingProgress();
        progress.setCurrentChapterId(1);
        progress.setLastReadAt(LocalDateTime.now());
        progress.setPercentageRead(50.0);
        progress.setCompleted(false);

        assertEquals(1, progress.getCurrentChapterId());
        assertEquals(50.0, progress.getPercentageRead());
        assertFalse(progress.isCompleted());
        assertNotNull(progress.getLastReadAt());
    }

    @Test
    void marcarComoCompletada() {
        ReadingProgress progress = new ReadingProgress();
        progress.setCompleted(false);
        progress.setPercentageRead(0.0);

        progress.setCompleted(true);
        progress.setPercentageRead(100.0);

        assertTrue(progress.isCompleted());
        assertEquals(100.0, progress.getPercentageRead());
    }

    @Test
    void actualizarProgreso() {
        ReadingProgress progress = new ReadingProgress();
        progress.setPercentageRead(25.0);
        progress.setCurrentChapterId(2);

        progress.setPercentageRead(75.0);
        progress.setCurrentChapterId(5);

        assertEquals(75.0, progress.getPercentageRead());
        assertEquals(5, progress.getCurrentChapterId());
    }
}
