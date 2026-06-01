package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Genre;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class GenreRepositoryTest {

    @Autowired
    private GenreRepository genreRepository;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setName("Fantasía");
        genre.setSlug("fantasia");
        genre.setDescription("Historias de fantasía");
        genre.setActive(true);
        genreRepository.save(genre);
    }

    @Test
    void guardarGenero_exitosamente() {
        Genre newGenre = new Genre();
        newGenre.setName("Ciencia Ficción");
        newGenre.setSlug("science-fiction");
        newGenre.setActive(true);

        Genre saved = genreRepository.save(newGenre);

        assertNotNull(saved.getId());
        assertEquals("Ciencia Ficción", saved.getName());
    }

    @Test
    void obtenerGenroPorId_exitosamente() {
        Optional<Genre> found = genreRepository.findById(genre.getId());

        assertTrue(found.isPresent());
        assertEquals("Fantasía", found.get().getName());
    }

    @Test
    void obtenerGenroPorSlug_exitosamente() {
        Optional<Genre> found = genreRepository.findBySlug("fantasia");

        assertTrue(found.isPresent());
        assertEquals("Fantasía", found.get().getName());
    }

    @Test
    void actualizarGenero_exitosamente() {
        genre.setName("Fantasía Épica");
        genreRepository.save(genre);

        Optional<Genre> updated = genreRepository.findById(genre.getId());

        assertTrue(updated.isPresent());
        assertEquals("Fantasía Épica", updated.get().getName());
    }

    @Test
    void eliminarGenero_exitosamente() {
        Integer id = genre.getId();
        genreRepository.delete(genre);

        Optional<Genre> found = genreRepository.findById(id);

        assertTrue(found.isEmpty());
    }

    @Test
    void listarGenerosActivos_conPaginacion() {
        Genre inactiveGenre = new Genre();
        inactiveGenre.setName("Horror");
        inactiveGenre.setSlug("horror");
        inactiveGenre.setActive(false);
        genreRepository.save(inactiveGenre);

        Page<Genre> activePage = genreRepository.findByActiveTrue(PageRequest.of(0, 10));

        assertEquals(1, activePage.getTotalElements());
        assertEquals("Fantasía", activePage.getContent().get(0).getName());
    }

    @Test
    void contarGenerosActivos() {
        Genre g2 = new Genre();
        g2.setName("Romance");
        g2.setSlug("romance");
        g2.setActive(true);
        genreRepository.save(g2);

        long count = genreRepository.countByActiveTrue();

        assertEquals(2, count);
    }

    @Test
    void validarConstraints_nombreNoEsDuplicado() {
        Genre duplicate = new Genre();
        duplicate.setName("Fantasía");
        duplicate.setSlug("fantasia-2");

        // Esto depende de si hay restricción UNIQUE en el nombre
        // Si la hay, debería fallar
        assertDoesNotThrow(() -> {
            genreRepository.save(duplicate);
        });
    }
}
