package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.StoryDTO;
import com.nunclear.escritores.service.StoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stories")
@RequiredArgsConstructor
public class StoryController {

    private final StoryService storyService;

    @PostMapping
    public ResponseEntity<StoryDTO> createStory(
            @RequestBody StoryDTO storyDTO,
            @RequestHeader("X-User-Id") Integer userId) {
        StoryDTO createdStory = storyService.createStory(storyDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdStory);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryDTO> getStoryById(@PathVariable Integer id) {
        StoryDTO story = storyService.getStoryById(id);
        return ResponseEntity.ok(story);
    }

    @GetMapping
    public ResponseEntity<List<StoryDTO>> getPublishedStories() {
        List<StoryDTO> stories = storyService.getPublishedStories();
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<StoryDTO>> getStoriesByUserId(@PathVariable Integer userId) {
        List<StoryDTO> stories = storyService.getStoriesByUserId(userId);
        return ResponseEntity.ok(stories);
    }

    @GetMapping("/visibility/{visibility}")
    public ResponseEntity<List<StoryDTO>> getStoriesByVisibility(@PathVariable String visibility) {
        List<StoryDTO> stories = storyService.getStoriesByVisibility(visibility);
        return ResponseEntity.ok(stories);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryDTO> updateStory(
            @PathVariable Integer id,
            @RequestBody StoryDTO storyDTO,
            @RequestHeader("X-User-Id") Integer userId) {
        StoryDTO updatedStory = storyService.updateStory(id, storyDTO, userId);
        return ResponseEntity.ok(updatedStory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStory(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer userId) {
        storyService.deleteStory(id, userId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/archive")
    public ResponseEntity<Void> archiveStory(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer userId) {
        storyService.archiveStory(id, userId);
        return ResponseEntity.ok().build();
    }
}
