package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.SaveReadingProgressRequest;
import com.nunclear.escritores.dto.response.ReadingProgressResponse;
import com.nunclear.escritores.entity.ReadingProgress;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.ReadingProgressRepository;
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
class ReadingProgressServiceTest {

    @Mock
    private ReadingProgressRepository readingProgressRepository;

    @InjectMocks
    private ReadingProgressService readingProgressService;

    private ReadingProgress progress;

    @BeforeEach
    void setUp() {
        progress = new ReadingProgress();
        progress.setId(1);
        progress.setUserId(10);
        progress.setStoryId(5);
        progress.setCurrentChapterId(3);
        progress.setPercentageRead(50.0);
        progress.setCompleted(false);
        progress.setLastReadAt(LocalDateTime.now());
    }

    @Test
    void guardarProgresoDeLectura_exitosamente() {
        SaveReadingProgressRequest request = new SaveReadingProgressRequest(
                5, 3, 50.0, false
        );

        when(readingProgressRepository.save(any(ReadingProgress.class))).thenReturn(progress);

        ReadingProgressResponse response = readingProgressService.saveReadingProgress(10, request);

        assertNotNull(response);
        assertEquals(50.0, response.percentageRead());
        verify(readingProgressRepository, times(1)).save(any(ReadingProgress.class));
    }

    @Test
    void obtenerProgresoDelUsuario_conPaginacion() {
        List<ReadingProgress> progressList = new ArrayList<>();
        progressList.add(progress);
        Page<ReadingProgress> page = new PageImpl<>(progressList, PageRequest.of(0, 10), 1);

        when(readingProgressRepository.findByUserId(eq(10), any())).thenReturn(page);

        Page<ReadingProgressResponse> result = readingProgressService.getUserReadingProgress(
                10, PageRequest.of(0, 10)
        );

        assertNotNull(result);
        assertFalse(result.isEmpty());
    }

    @Test
    void obtenerProgresoDeUnaCiertaHistoria_paraUnUsuario() {
        when(readingProgressRepository.findByUserIdAndStoryId(10, 5))
                .thenReturn(Optional.of(progress));

        ReadingProgressResponse response = readingProgressService.getReadingProgress(10, 5);

        assertNotNull(response);
        assertEquals(5, response.storyId());
    }

    @Test
    void obtenerProgresoDeHistoria_sinUsuario() {
        when(readingProgressRepository.findByStoryId(eq(5), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));

        Page<ReadingProgressResponse> result = readingProgressService.getStoryReadingProgress(
                5, PageRequest.of(0, 10)
        );

        assertNotNull(result);
    }

    @Test
    void marcarHistoriaComoCompletada() {
        when(readingProgressRepository.findByUserIdAndStoryId(10, 5))
                .thenReturn(Optional.of(progress));
        when(readingProgressRepository.save(any(ReadingProgress.class))).thenReturn(progress);

        progress.setCompleted(true);
        progress.setPercentageRead(100.0);

        readingProgressService.saveReadingProgress(10,
                new SaveReadingProgressRequest(5, 3, 100.0, true));

        assertTrue(progress.isCompleted());
        assertEquals(100.0, progress.getPercentageRead());
    }

    @Test
    void eliminarProgresoDelUsuario() {
        when(readingProgressRepository.findByUserIdAndStoryId(10, 5))
                .thenReturn(Optional.of(progress));

        readingProgressService.deleteReadingProgress(10, 5);

        verify(readingProgressRepository, times(1)).delete(any(ReadingProgress.class));
    }

    @Test
    void eliminarProgreso_lanzaExcepcion() {
        when(readingProgressRepository.findByUserIdAndStoryId(999, 999))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> {
            readingProgressService.deleteReadingProgress(999, 999);
        });
    }
}
