package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.ChapterDTO;
import com.nunclear.escritores.service.ChapterService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chapters")
@RequiredArgsConstructor
public class ChapterController {

    private final ChapterService chapterService;

    @PostMapping
    public ResponseEntity<ChapterDTO> createChapter(
            @RequestBody ChapterDTO chapterDTO,
            @RequestParam Integer storyId,
            @RequestHeader("X-User-Id") Integer userId) {
        ChapterDTO createdChapter = chapterService.createChapter(chapterDTO, storyId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdChapter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChapterDTO> getChapterById(@PathVariable Integer id) {
        ChapterDTO chapter = chapterService.getChapterById(id);
        return ResponseEntity.ok(chapter);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<ChapterDTO>> getChaptersByStoryId(@PathVariable Integer storyId) {
        List<ChapterDTO> chapters = chapterService.getChaptersByStoryId(storyId);
        return ResponseEntity.ok(chapters);
    }

    @GetMapping("/story/{storyId}/published")
    public ResponseEntity<List<ChapterDTO>> getPublishedChapters(@PathVariable Integer storyId) {
        List<ChapterDTO> chapters = chapterService.getPublishedChapters(storyId);
        return ResponseEntity.ok(chapters);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChapterDTO> updateChapter(
            @PathVariable Integer id,
            @RequestBody ChapterDTO chapterDTO,
            @RequestHeader("X-User-Id") Integer userId) {
        ChapterDTO updatedChapter = chapterService.updateChapter(id, chapterDTO, userId);
        return ResponseEntity.ok(updatedChapter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChapter(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer userId) {
        chapterService.deleteChapter(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveChapter(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer userId) {
        chapterService.archiveChapter(id, userId);
        return ResponseEntity.ok().build();
    }
}
