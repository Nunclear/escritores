package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.CreateGenreRequest;
import com.nunclear.escritores.dto.response.GenreResponse;
import com.nunclear.escritores.entity.Genre;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.GenreRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenreServiceTest {

    @Mock
    private GenreRepository genreRepository;

    @InjectMocks
    private GenreService genreService;

    private Genre genre;

    @BeforeEach
    void setUp() {
        genre = new Genre();
        genre.setId(1);
        genre.setName("Fantasía");
        genre.setSlug("fantasia");
        genre.setDescription("Historias de fantasía");
        genre.setActive(true);
    }

    @Test
    void crearGenero_exitosamente() {
        CreateGenreRequest request = new CreateGenreRequest(
                "Ciencia Ficción",
                "Historias futuristas"
        );

        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        GenreResponse response = genreService.createGenre(request);

        assertNotNull(response);
        assertEquals("Fantasía", response.name());
        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    void obtenerGenroPorId_exitosamente() {
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        GenreResponse response = genreService.getGenreById(1);

        assertNotNull(response);
        assertEquals("Fantasía", response.name());
    }

    @Test
    void obtenerGenroPorId_lanzaExcepcionSiNoExiste() {
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            genreService.getGenreById(999);
        });
    }

    @Test
    void listarGeneros_conPaginacion() {
        List<Genre> genres = new ArrayList<>();
        genres.add(genre);
        Page<Genre> page = new PageImpl<>(genres, PageRequest.of(0, 10), 1);

        when(genreRepository.findByActiveTrue(any())).thenReturn(page);

        Page<GenreResponse> result = genreService.getAllGenres(PageRequest.of(0, 10));

        assertNotNull(result);
        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void obtenerGenroPorSlug_exitosamente() {
        when(genreRepository.findBySlug("fantasia")).thenReturn(Optional.of(genre));

        GenreResponse response = genreService.getGenreBySlug("fantasia");

        assertNotNull(response);
        assertEquals("Fantasía", response.name());
    }

    @Test
    void actualizarGenero_exitosamente() {
        CreateGenreRequest request = new CreateGenreRequest(
                "Fantasía Épica",
                "Grandes historias de fantasía"
        );

        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));
        when(genreRepository.save(any(Genre.class))).thenReturn(genre);

        GenreResponse response = genreService.updateGenre(1, request);

        assertNotNull(response);
        verify(genreRepository, times(1)).save(any(Genre.class));
    }

    @Test
    void eliminarGenero_exitosamente() {
        when(genreRepository.findById(1)).thenReturn(Optional.of(genre));

        genreService.deleteGenre(1);

        verify(genreRepository, times(1)).deleteById(1);
    }

    @Test
    void eliminarGenero_lanzaExcepcionSiNoExiste() {
        when(genreRepository.findById(999)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            genreService.deleteGenre(999);
        });
    }
}
