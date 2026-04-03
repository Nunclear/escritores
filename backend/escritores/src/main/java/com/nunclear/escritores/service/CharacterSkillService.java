package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.CharacterSkill;
import com.nunclear.escritores.entity.Skill;
import com.nunclear.escritores.entity.StoryCharacter;
import com.nunclear.escritores.repository.CharacterSkillRepository;
import com.nunclear.escritores.repository.SkillRepository;
import com.nunclear.escritores.repository.StoryCharacterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CharacterSkillService {
    private final CharacterSkillRepository characterSkillRepository;
    private final SkillRepository skillRepository;
    private final StoryCharacterRepository characterRepository;

    /**
     * Asignar una habilidad a un personaje
     */
    @Transactional
    public CharacterSkill assignSkillToCharacter(Integer characterId, Integer skillId, 
                                                  Integer proficiency, String notes) {
        StoryCharacter character = characterRepository.findById(characterId)
                .orElseThrow(() -> new RuntimeException("Personaje no encontrado"));
        Skill skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new RuntimeException("Habilidad no encontrada"));

        // Verificar que la habilidad pertenece a la misma historia
        if (!character.getStory().getId().equals(skill.getStory().getId())) {
            throw new RuntimeException("La habilidad debe pertenecer a la misma historia");
        }

        CharacterSkill characterSkill = CharacterSkill.builder()
                .storyCharacter(character)
                .skill(skill)
                .proficiency(proficiency != null ? proficiency : 50)
                .notes(notes)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        return characterSkillRepository.save(characterSkill);
    }

    /**
     * Obtener todas las habilidades de un personaje
     */
    public List<CharacterSkill> getCharacterSkills(Integer characterId) {
        return characterSkillRepository.findByStoryCharacterId(characterId);
    }

    /**
     * Obtener todos los personajes que tienen una habilidad
     */
    public List<CharacterSkill> getSkillCharacters(Integer skillId) {
        return characterSkillRepository.findBySkillId(skillId);
    }

    /**
     * Actualizar dominio/proficiency de una habilidad
     */
    @Transactional
    public CharacterSkill updateProficiency(Integer characterSkillId, Integer proficiency) {
        CharacterSkill characterSkill = characterSkillRepository.findById(characterSkillId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        if (proficiency != null && proficiency >= 0 && proficiency <= 100) {
            characterSkill.setProficiency(proficiency);
        } else {
            throw new RuntimeException("Proficiency debe estar entre 0 y 100");
        }

        characterSkill.setUpdatedAt(LocalDateTime.now());
        return characterSkillRepository.save(characterSkill);
    }

    /**
     * Actualizar notas de una habilidad
     */
    @Transactional
    public CharacterSkill updateNotes(Integer characterSkillId, String notes) {
        CharacterSkill characterSkill = characterSkillRepository.findById(characterSkillId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));

        characterSkill.setNotes(notes);
        characterSkill.setUpdatedAt(LocalDateTime.now());
        return characterSkillRepository.save(characterSkill);
    }

    /**
     * Remover una habilidad de un personaje
     */
    @Transactional
    public void removeSkillFromCharacter(Integer characterSkillId) {
        CharacterSkill characterSkill = characterSkillRepository.findById(characterSkillId)
                .orElseThrow(() -> new RuntimeException("Relación no encontrada"));
        characterSkillRepository.delete(characterSkill);
    }

    /**
     * Contar habilidades de un personaje
     */
    public long countCharacterSkills(Integer characterId) {
        return characterSkillRepository.findByStoryCharacterId(characterId).size();
    }

    /**
     * Verificar si un personaje tiene una habilidad específica
     */
    public boolean hasSkill(Integer characterId, Integer skillId) {
        return characterSkillRepository.findByStoryCharacterId(characterId).stream()
                .anyMatch(cs -> cs.getSkill().getId().equals(skillId));
    }
}
