package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.StoryCharacter;
import com.nunclear.escritores.entity.Chapter;
import com.nunclear.escritores.service.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {
    private final SearchService searchService;

    /**
     * Búsqueda global de historias por título (con paginación)
     */
    @GetMapping("/stories")
    public ResponseEntity<?> searchStories(
            @RequestParam String title,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            var results = searchService.searchStoriesByTitle(title, pageable);
            return ResponseEntity.ok(Map.of(
                "query", title,
                "page", page,
                "size", size,
                "results", results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda de historias por autor
     */
    @GetMapping("/stories/author")
    public ResponseEntity<?> searchStoriesByAuthor(@RequestParam String author) {
        try {
            List<Story> stories = searchService.searchStoriesByAuthor(author);
            return ResponseEntity.ok(Map.of("author", author, "results", stories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda de historias por estado
     */
    @GetMapping("/stories/status")
    public ResponseEntity<?> searchStoriesByStatus(@RequestParam String status) {
        try {
            List<Story> stories = searchService.searchStoriesByPublicationState(status);
            return ResponseEntity.ok(Map.of("status", status, "results", stories));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda de personajes por nombre
     */
    @GetMapping("/characters")
    public ResponseEntity<?> searchCharacters(@RequestParam String name) {
        try {
            List<StoryCharacter> characters = searchService.searchCharactersByName(name);
            return ResponseEntity.ok(Map.of("query", name, "results", characters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda de personajes por historia
     */
    @GetMapping("/characters/story/{storyId}")
    public ResponseEntity<?> searchCharactersByStory(@PathVariable Integer storyId) {
        try {
            List<StoryCharacter> characters = searchService.searchCharactersByStory(storyId);
            return ResponseEntity.ok(Map.of("storyId", storyId, "results", characters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda de capítulos por título
     */
    @GetMapping("/chapters")
    public ResponseEntity<?> searchChapters(@RequestParam String title) {
        try {
            List<Chapter> chapters = searchService.searchChaptersByTitle(title);
            return ResponseEntity.ok(Map.of("query", title, "results", chapters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda de capítulos por historia
     */
    @GetMapping("/chapters/story/{storyId}")
    public ResponseEntity<?> searchChaptersByStory(@PathVariable Integer storyId) {
        try {
            List<Chapter> chapters = searchService.searchChaptersByStory(storyId);
            return ResponseEntity.ok(Map.of("storyId", storyId, "results", chapters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda avanzada de historias
     */
    @GetMapping("/stories/advanced")
    public ResponseEntity<?> advancedStorySearch(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String status) {
        try {
            List<Story> results = searchService.advancedStorySearch(title, author, status);
            return ResponseEntity.ok(Map.of(
                "filters", Map.of("title", title, "author", author, "status", status),
                "results", results
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Búsqueda global
     */
    @GetMapping("/global")
    public ResponseEntity<?> globalSearch(@RequestParam String q) {
        try {
            Map<String, Object> results = searchService.globalSearch(q);
            return ResponseEntity.ok(Map.of("query", q, "results", results));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
