package com.nunclear.escritores.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ChapterVersionTest {

    @Test
    void prePersist_deberiaAsignarTimestamps() {
        ChapterVersion version = new ChapterVersion();

        version.prePersist();

        assertNotNull(version.getCreatedAt());
    }

    @Test
    void crearChapterVersion_conDatosValidos() {
        ChapterVersion version = new ChapterVersion();
        version.setVersionNumber(1);
        version.setContent("Contenido del capítulo");
        version.setChangeDescription("Primera versión");

        assertEquals(1, version.getVersionNumber());
        assertEquals("Contenido del capítulo", version.getContent());
        assertEquals("Primera versión", version.getChangeDescription());
    }

    @Test
    void crearMultiplesVersiones_incrementarNumero() {
        ChapterVersion v1 = new ChapterVersion();
        v1.setVersionNumber(1);
        v1.setContent("Contenido v1");

        ChapterVersion v2 = new ChapterVersion();
        v2.setVersionNumber(2);
        v2.setContent("Contenido v2");

        assertTrue(v2.getVersionNumber() > v1.getVersionNumber());
    }

    @Test
    void versionPuedeAgregarDescripcionDeCambios() {
        ChapterVersion version = new ChapterVersion();
        version.setVersionNumber(3);
        version.setChangeDescription("Se corrigieron errores de ortografía");

        assertNotNull(version.getChangeDescription());
        assertEquals("Se corrigieron errores de ortografía", version.getChangeDescription());
    }
}
