package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.StoryRatingDTO;
import com.nunclear.escritores.service.StoryRatingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/ratings")
@RequiredArgsConstructor
public class StoryRatingController {

    private final StoryRatingService ratingService;

    @PostMapping
    public ResponseEntity<StoryRatingDTO> rateStory(
            @RequestBody StoryRatingDTO ratingDTO,
            @RequestHeader("X-User-Id") Integer userId) {
        StoryRatingDTO createdRating = ratingService.rateStory(ratingDTO, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdRating);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryRatingDTO> getRatingById(@PathVariable Integer id) {
        StoryRatingDTO rating = ratingService.getRatingById(id);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<StoryRatingDTO>> getRatingsByStoryId(@PathVariable Integer storyId) {
        List<StoryRatingDTO> ratings = ratingService.getRatingsByStoryId(storyId);
        return ResponseEntity.ok(ratings);
    }

    @GetMapping("/story/{storyId}/user/{userId}")
    public ResponseEntity<StoryRatingDTO> getUserRating(
            @PathVariable Integer storyId,
            @PathVariable Integer userId) {
        StoryRatingDTO rating = ratingService.getUserRating(storyId, userId);
        return ResponseEntity.ok(rating);
    }

    @GetMapping("/story/{storyId}/average")
    public ResponseEntity<Map<String, Double>> getAverageRating(@PathVariable Integer storyId) {
        double average = ratingService.getAverageRating(storyId);
        return ResponseEntity.ok(Map.of("averageScore", average));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRating(
            @PathVariable Integer id,
            @RequestHeader("X-User-Id") Integer userId) {
        ratingService.deleteRating(id, userId);
        return ResponseEntity.noContent().build();
    }
}
