package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.Arc;
import com.nunclear.escritores.repository.ArcRepository;
import com.nunclear.escritores.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/arcs")
@RequiredArgsConstructor
public class ArcController {

    private final ArcRepository arcRepository;
    private final StoryRepository storyRepository;

    @PostMapping
    public ResponseEntity<Arc> createArc(@RequestBody Arc arc) {
        arc.setCreatedAt(LocalDateTime.now());
        arc.setUpdatedAt(LocalDateTime.now());
        Arc savedArc = arcRepository.save(arc);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArc);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Arc> getArcById(@PathVariable Integer id) {
        Arc arc = arcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Arc not found"));
        return ResponseEntity.ok(arc);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Arc>> getArcsByStoryId(@PathVariable Integer storyId) {
        storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));
        List<Arc> arcs = arcRepository.findByStoryIdOrderByPositionIndex(storyId);
        return ResponseEntity.ok(arcs);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Arc> updateArc(@PathVariable Integer id, @RequestBody Arc arcDetails) {
        Arc arc = arcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Arc not found"));
        arc.setTitle(arcDetails.getTitle());
        arc.setSubtitle(arcDetails.getSubtitle());
        arc.setPositionIndex(arcDetails.getPositionIndex());
        arc.setUpdatedAt(LocalDateTime.now());
        Arc updatedArc = arcRepository.save(arc);
        return ResponseEntity.ok(updatedArc);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteArc(@PathVariable Integer id) {
        Arc arc = arcRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Arc not found"));
        arcRepository.delete(arc);
        return ResponseEntity.noContent().build();
    }
}
