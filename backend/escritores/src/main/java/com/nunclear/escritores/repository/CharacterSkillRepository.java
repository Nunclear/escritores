package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.CharacterSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CharacterSkillRepository extends JpaRepository<CharacterSkill, Integer> {
    List<CharacterSkill> findByStoryCharacterId(Integer storyCharacterId);
    List<CharacterSkill> findBySkillId(Integer skillId);
}
