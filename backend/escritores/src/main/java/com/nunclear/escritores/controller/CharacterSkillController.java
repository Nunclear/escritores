package com.nunclear.escritores.controller;

import com.nunclear.escritores.entity.CharacterSkill;
import com.nunclear.escritores.service.CharacterSkillService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/character-skills")
@RequiredArgsConstructor
public class CharacterSkillController {
    private final CharacterSkillService characterSkillService;

    /**
     * Asignar una habilidad a un personaje
     */
    @PostMapping
    public ResponseEntity<?> assignSkillToCharacter(@RequestBody AssignSkillRequest request) {
        try {
            CharacterSkill characterSkill = characterSkillService.assignSkillToCharacter(
                request.getCharacterId(),
                request.getSkillId(),
                request.getProficiency(),
                request.getNotes()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(characterSkill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todas las habilidades de un personaje
     */
    @GetMapping("/characters/{characterId}")
    public ResponseEntity<?> getCharacterSkills(@PathVariable Integer characterId) {
        try {
            List<CharacterSkill> skills = characterSkillService.getCharacterSkills(characterId);
            return ResponseEntity.ok(Map.of("characterId", characterId, "skills", skills));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Obtener todos los personajes con una habilidad específica
     */
    @GetMapping("/skills/{skillId}")
    public ResponseEntity<?> getSkillCharacters(@PathVariable Integer skillId) {
        try {
            List<CharacterSkill> characters = characterSkillService.getSkillCharacters(skillId);
            return ResponseEntity.ok(Map.of("skillId", skillId, "characters", characters));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar dominio/proficiency de una habilidad
     */
    @PutMapping("/{characterSkillId}/proficiency")
    public ResponseEntity<?> updateProficiency(
            @PathVariable Integer characterSkillId,
            @RequestBody UpdateProficiencyRequest request) {
        try {
            CharacterSkill characterSkill = characterSkillService.updateProficiency(
                characterSkillId,
                request.getProficiency()
            );
            return ResponseEntity.ok(characterSkill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Actualizar notas de una habilidad
     */
    @PutMapping("/{characterSkillId}/notes")
    public ResponseEntity<?> updateNotes(
            @PathVariable Integer characterSkillId,
            @RequestBody UpdateNotesRequest request) {
        try {
            CharacterSkill characterSkill = characterSkillService.updateNotes(
                characterSkillId,
                request.getNotes()
            );
            return ResponseEntity.ok(characterSkill);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Remover una habilidad de un personaje
     */
    @DeleteMapping("/{characterSkillId}")
    public ResponseEntity<?> removeSkillFromCharacter(@PathVariable Integer characterSkillId) {
        try {
            characterSkillService.removeSkillFromCharacter(characterSkillId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Contar habilidades de un personaje
     */
    @GetMapping("/characters/{characterId}/count")
    public ResponseEntity<?> countCharacterSkills(@PathVariable Integer characterId) {
        try {
            long count = characterSkillService.countCharacterSkills(characterId);
            return ResponseEntity.ok(Map.of("characterId", characterId, "count", count));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Verificar si un personaje tiene una habilidad
     */
    @GetMapping("/characters/{characterId}/has-skill/{skillId}")
    public ResponseEntity<?> hasSkill(
            @PathVariable Integer characterId,
            @PathVariable Integer skillId) {
        try {
            boolean hasSkill = characterSkillService.hasSkill(characterId, skillId);
            return ResponseEntity.ok(Map.of("hasSkill", hasSkill));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @lombok.Data
    public static class AssignSkillRequest {
        private Integer characterId;
        private Integer skillId;
        private Integer proficiency;
        private String notes;
    }

    @lombok.Data
    public static class UpdateProficiencyRequest {
        private Integer proficiency;
    }

    @lombok.Data
    public static class UpdateNotesRequest {
        private String notes;
    }
}
