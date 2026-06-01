package com.nunclear.escritores.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class GenreTest {

    @Test
    void prePersist_deberiaAsignarCreatedAtYUpdatedAt() {
        Genre genre = new Genre();
        genre.setName("Fantasía");

        genre.prePersist();

        assertNotNull(genre.getCreatedAt());
        assertNotNull(genre.getUpdatedAt());
        assertEquals(genre.getCreatedAt(), genre.getUpdatedAt());
    }

    @Test
    void preUpdate_deberiaActualizarUpdatedAt() {
        Genre genre = new Genre();
        genre.setName("Romance");
        LocalDateTime originalTime = LocalDateTime.of(2026, 4, 22, 10, 0);
        genre.setUpdatedAt(originalTime);

        genre.preUpdate();

        assertNotNull(genre.getUpdatedAt());
        assertTrue(genre.getUpdatedAt().isAfter(originalTime));
    }

    @Test
    void crearGenre_conDatosValidos() {
        Genre genre = new Genre();
        genre.setName("Ciencia Ficción");
        genre.setSlug("science-fiction");
        genre.setDescription("Historias futuristas");
        genre.setActive(true);

        assertNotNull(genre.getName());
        assertEquals("Ciencia Ficción", genre.getName());
        assertEquals("science-fiction", genre.getSlug());
        assertEquals("Historias futuristas", genre.getDescription());
        assertTrue(genre.isActive());
    }

    @Test
    void deactivateGenre() {
        Genre genre = new Genre();
        genre.setName("Horror");
        genre.setActive(true);

        genre.setActive(false);

        assertFalse(genre.isActive());
    }
}
