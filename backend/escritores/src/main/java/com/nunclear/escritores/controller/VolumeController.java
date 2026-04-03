package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.Volume;
import com.nunclear.escritores.repository.VolumeRepository;
import com.nunclear.escritores.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/volumes")
@RequiredArgsConstructor
public class VolumeController {

    private final VolumeRepository volumeRepository;
    private final StoryRepository storyRepository;

    @PostMapping
    public ResponseEntity<Volume> createVolume(@RequestBody Volume volume) {
        volume.setCreatedAt(LocalDateTime.now());
        volume.setUpdatedAt(LocalDateTime.now());
        Volume savedVolume = volumeRepository.save(volume);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedVolume);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Volume> getVolumeById(@PathVariable Integer id) {
        Volume volume = volumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Volume not found"));
        return ResponseEntity.ok(volume);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Volume>> getVolumesByStoryId(@PathVariable Integer storyId) {
        storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));
        List<Volume> volumes = volumeRepository.findByStoryIdOrderByPositionIndex(storyId);
        return ResponseEntity.ok(volumes);
    }

    @GetMapping("/arc/{arcId}")
    public ResponseEntity<List<Volume>> getVolumesByArcId(@PathVariable Integer arcId) {
        List<Volume> volumes = volumeRepository.findByArcId(arcId);
        return ResponseEntity.ok(volumes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Volume> updateVolume(@PathVariable Integer id, @RequestBody Volume volumeDetails) {
        Volume volume = volumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Volume not found"));
        volume.setTitle(volumeDetails.getTitle());
        volume.setPositionIndex(volumeDetails.getPositionIndex());
        volume.setUpdatedAt(LocalDateTime.now());
        Volume updatedVolume = volumeRepository.save(volume);
        return ResponseEntity.ok(updatedVolume);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVolume(@PathVariable Integer id) {
        Volume volume = volumeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Volume not found"));
        volumeRepository.delete(volume);
        return ResponseEntity.noContent().build();
    }
}
