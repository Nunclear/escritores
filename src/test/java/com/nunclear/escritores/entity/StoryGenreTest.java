package com.nunclear.escritores.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class StoryGenreTest {

    @Test
    void crearStoryGenre_conDatosValidos() {
        StoryGenre storyGenre = new StoryGenre();
        storyGenre.setStoryId(1);
        storyGenre.setGenreId(5);

        assertEquals(1, storyGenre.getStoryId());
        assertEquals(5, storyGenre.getGenreId());
    }

    @Test
    void storyGenreRelacionaHistoriasConGeneros() {
        StoryGenre sg1 = new StoryGenre();
        sg1.setStoryId(100);
        sg1.setGenreId(1); // Fantasía

        StoryGenre sg2 = new StoryGenre();
        sg2.setStoryId(100);
        sg2.setGenreId(2); // Romance

        assertEquals(100, sg1.getStoryId());
        assertEquals(100, sg2.getStoryId());
        assertNotEquals(sg1.getGenreId(), sg2.getGenreId());
    }

    @Test
    void unaHistoriaPuedeTenerMultiplesGeneros() {
        StoryGenre[] genresAsignados = new StoryGenre[3];

        for (int i = 0; i < 3; i++) {
            StoryGenre sg = new StoryGenre();
            sg.setStoryId(1);
            sg.setGenreId(i + 1);
            genresAsignados[i] = sg;
        }

        for (StoryGenre sg : genresAsignados) {
            assertEquals(1, sg.getStoryId());
        }
    }
}
