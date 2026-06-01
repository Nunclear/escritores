package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.response.ChapterVersionResponse;
import com.nunclear.escritores.entity.ChapterVersion;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.ChapterVersionRepository;
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
class ChapterVersionServiceTest {

    @Mock
    private ChapterVersionRepository chapterVersionRepository;

    @InjectMocks
    private ChapterVersionService chapterVersionService;

    private ChapterVersion version;

    @BeforeEach
    void setUp() {
        version = new ChapterVersion();
        version.setId(1);
        version.setChapterId(5);
        version.setVersionNumber(1);
        version.setContent("Contenido del capítulo");
        version.setChangeDescription("Primera versión");
        version.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void crearVersionDeCapitulo_exitosamente() {
        when(chapterVersionRepository.save(any(ChapterVersion.class))).thenReturn(version);

        ChapterVersion result = chapterVersionService.createVersion(version);

        assertNotNull(result);
        assertEquals(1, result.getVersionNumber());
        verify(chapterVersionRepository, times(1)).save(any(ChapterVersion.class));
    }

    @Test
    void obtenerVersionesDelCapitulo_conPaginacion() {
        List<ChapterVersion> versions = new ArrayList<>();
        versions.add(version);
        Page<ChapterVersion> page = new PageImpl<>(versions, PageRequest.of(0, 10), 1);

        when(chapterVersionRepository.findByChapterIdOrderByVersionNumberDesc(eq(5), any()))
                .thenReturn(page);

        Page<ChapterVersionResponse> result = chapterVersionService.getChapterVersions(
                5, PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void obtenerVersionEspecificaDelCapitulo() {
        when(chapterVersionRepository.findByChapterIdAndVersionNumber(5, 1))
                .thenReturn(Optional.of(version));

        ChapterVersionResponse response = chapterVersionService.getChapterVersion(5, 1);

        assertNotNull(response);
        assertEquals(1, response.versionNumber());
    }

    @Test
    void obtenerUltimaVersionDelCapitulo() {
        when(chapterVersionRepository.findFirstByChapterIdOrderByVersionNumberDesc(5))
                .thenReturn(Optional.of(version));

        ChapterVersionResponse response = chapterVersionService.getLatestVersion(5);

        assertNotNull(response);
        assertEquals("Contenido del capítulo", response.content());
    }

    @Test
    void obtenerVersionDelCapitulo_noExiste() {
        when(chapterVersionRepository.findByChapterIdAndVersionNumber(999, 999))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            chapterVersionService.getChapterVersion(999, 999);
        });
    }

    @Test
    void restaurarVersionAnteriorDelCapitulo() {
        ChapterVersion oldVersion = new ChapterVersion();
        oldVersion.setVersionNumber(2);
        oldVersion.setContent("Contenido anterior");

        when(chapterVersionRepository.findByChapterIdAndVersionNumber(5, 2))
                .thenReturn(Optional.of(oldVersion));
        when(chapterVersionRepository.save(any(ChapterVersion.class))).thenReturn(oldVersion);

        chapterVersionService.restoreVersion(5, 2);

        verify(chapterVersionRepository, times(1)).save(any(ChapterVersion.class));
    }

    @Test
    void contar_numeroDeTotalVersionesDeCapitulo() {
        when(chapterVersionRepository.countByChapterId(5)).thenReturn(3L);

        long count = chapterVersionService.countVersions(5);

        assertEquals(3L, count);
    }
}
