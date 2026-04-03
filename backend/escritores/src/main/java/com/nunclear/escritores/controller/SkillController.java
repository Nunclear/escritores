package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.Skill;
import com.nunclear.escritores.repository.SkillRepository;
import com.nunclear.escritores.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillRepository skillRepository;
    private final StoryRepository storyRepository;

    @PostMapping
    public ResponseEntity<Skill> createSkill(@RequestBody Skill skill) {
        skill.setCreatedAt(LocalDateTime.now());
        skill.setUpdatedAt(LocalDateTime.now());
        Skill savedSkill = skillRepository.save(skill);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedSkill);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Skill> getSkillById(@PathVariable Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        return ResponseEntity.ok(skill);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<Skill>> getSkillsByStoryId(@PathVariable Integer storyId) {
        storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));
        List<Skill> skills = skillRepository.findByStoryId(storyId);
        return ResponseEntity.ok(skills);
    }

    @GetMapping("/story/{storyId}/category/{category}")
    public ResponseEntity<List<Skill>> getSkillsByCategory(
            @PathVariable Integer storyId,
            @PathVariable String category) {
        List<Skill> skills = skillRepository.findByStoryIdAndCategoryName(storyId, category);
        return ResponseEntity.ok(skills);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Integer id, @RequestBody Skill skillDetails) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        skill.setName(skillDetails.getName());
        skill.setDescription(skillDetails.getDescription());
        skill.setLevelValue(skillDetails.getLevelValue());
        skill.setCategoryName(skillDetails.getCategoryName());
        skill.setUpdatedAt(LocalDateTime.now());
        Skill updatedSkill = skillRepository.save(skill);
        return ResponseEntity.ok(updatedSkill);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Skill not found"));
        skillRepository.delete(skill);
        return ResponseEntity.noContent().build();
    }
}
