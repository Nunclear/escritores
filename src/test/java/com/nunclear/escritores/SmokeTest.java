package com.nunclear.escritores;

import com.nunclear.escritores.controller.*;
import com.nunclear.escritores.service.*;
import com.nunclear.escritores.repository.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class SmokeTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextoCarga_exitosamente() {
        assertNotNull(applicationContext);
    }

    @Test
    void controladorGenreExiste() {
        GenreController controller = applicationContext.getBean(GenreController.class);
        assertNotNull(controller);
    }

    @Test
    void controladorNotificationExiste() {
        NotificationController controller = applicationContext.getBean(NotificationController.class);
        assertNotNull(controller);
    }

    @Test
    void controladorReadingProgressExiste() {
        ReadingProgressController controller = applicationContext.getBean(ReadingProgressController.class);
        assertNotNull(controller);
    }

    @Test
    void controladorChapterVersionExiste() {
        ChapterVersionController controller = applicationContext.getBean(ChapterVersionController.class);
        assertNotNull(controller);
    }

    @Test
    void controladorCommentLikeExiste() {
        CommentLikeController controller = applicationContext.getBean(CommentLikeController.class);
        assertNotNull(controller);
    }

    @Test
    void servicioGenreExiste() {
        GenreService service = applicationContext.getBean(GenreService.class);
        assertNotNull(service);
    }

    @Test
    void servicioNotificationExiste() {
        NotificationService service = applicationContext.getBean(NotificationService.class);
        assertNotNull(service);
    }

    @Test
    void servicioReadingProgressExiste() {
        ReadingProgressService service = applicationContext.getBean(ReadingProgressService.class);
        assertNotNull(service);
    }

    @Test
    void servicioChapterVersionExiste() {
        ChapterVersionService service = applicationContext.getBean(ChapterVersionService.class);
        assertNotNull(service);
    }

    @Test
    void servicioCommentLikeExiste() {
        CommentLikeService service = applicationContext.getBean(CommentLikeService.class);
        assertNotNull(service);
    }

    @Test
    void repositorioGenreExiste() {
        GenreRepository repo = applicationContext.getBean(GenreRepository.class);
        assertNotNull(repo);
    }

    @Test
    void repositorioNotificationExiste() {
        NotificationRepository repo = applicationContext.getBean(NotificationRepository.class);
        assertNotNull(repo);
    }

    @Test
    void repositorioReadingProgressExiste() {
        ReadingProgressRepository repo = applicationContext.getBean(ReadingProgressRepository.class);
        assertNotNull(repo);
    }

    @Test
    void repositorioChapterVersionExiste() {
        ChapterVersionRepository repo = applicationContext.getBean(ChapterVersionRepository.class);
        assertNotNull(repo);
    }

    @Test
    void repositorioCommentLikeExiste() {
        CommentLikeRepository repo = applicationContext.getBean(CommentLikeRepository.class);
        assertNotNull(repo);
    }
}
