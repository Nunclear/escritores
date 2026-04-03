package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.StoryCharacter;
import com.nunclear.escritores.repository.StoryCharacterRepository;
import com.nunclear.escritores.repository.StoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/characters")
@RequiredArgsConstructor
public class CharacterController {

    private final StoryCharacterRepository characterRepository;
    private final StoryRepository storyRepository;

    @PostMapping
    public ResponseEntity<StoryCharacter> createCharacter(@RequestBody StoryCharacter character) {
        character.setCreatedAt(LocalDateTime.now());
        character.setUpdatedAt(LocalDateTime.now());
        StoryCharacter savedCharacter = characterRepository.save(character);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCharacter);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoryCharacter> getCharacterById(@PathVariable Integer id) {
        StoryCharacter character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));
        return ResponseEntity.ok(character);
    }

    @GetMapping("/story/{storyId}")
    public ResponseEntity<List<StoryCharacter>> getCharactersByStoryId(@PathVariable Integer storyId) {
        storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));
        List<StoryCharacter> characters = characterRepository.findByStoryId(storyId);
        return ResponseEntity.ok(characters);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoryCharacter> updateCharacter(
            @PathVariable Integer id,
            @RequestBody StoryCharacter characterDetails) {
        StoryCharacter character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));
        character.setName(characterDetails.getName());
        character.setDescription(characterDetails.getDescription());
        character.setCharacterRoleName(characterDetails.getCharacterRoleName());
        character.setProfession(characterDetails.getProfession());
        character.setAbility(characterDetails.getAbility());
        character.setAge(characterDetails.getAge());
        character.setBirthDate(characterDetails.getBirthDate());
        character.setIsAlive(characterDetails.getIsAlive());
        character.setImageUrl(characterDetails.getImageUrl());
        character.setRolesJson(characterDetails.getRolesJson());
        character.setUpdatedAt(LocalDateTime.now());
        StoryCharacter updatedCharacter = characterRepository.save(character);
        return ResponseEntity.ok(updatedCharacter);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCharacter(@PathVariable Integer id) {
        StoryCharacter character = characterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Character not found"));
        characterRepository.delete(character);
        return ResponseEntity.noContent().build();
    }
}
