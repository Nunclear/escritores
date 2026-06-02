package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.request.CreateSkillRequest;
import com.nunclear.escritores.dto.request.UpdateSkillRequest;
import com.nunclear.escritores.dto.response.*;
import com.nunclear.escritores.entity.Skill;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.AppUserRepository;
import com.nunclear.escritores.repository.SkillRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.util.PaginationUtils;
import com.nunclear.escritores.util.StoryAccessUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SkillService {

    // La cadena de error de historia se obtiene desde StoryAccessUtils.STORY_NOT_FOUND

    private final SkillRepository skillRepository;
    private final StoryRepository storyRepository;
    private final AppUserRepository appUserRepository;

    public CreateSkillResponse createSkill(CreateSkillRequest request) {
        Story story = StoryAccessUtils.getEditableStory(request.storyId(), storyRepository, appUserRepository);

        Skill skill = new Skill();
        skill.setStoryId(story.getId());
        skill.setName(request.name());
        skill.setDescription(request.description());
        skill.setCategoryName(request.categoryName());
        skill.setLevelValue(request.levelValue());

        Skill saved = skillRepository.save(skill);

        return new CreateSkillResponse(
                saved.getId(),
                saved.getStoryId(),
                saved.getName(),
                saved.getCategoryName(),
                saved.getLevelValue()
        );
    }

    public SkillDetailResponse getSkillById(Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad no encontrada"));

        Story story = storyRepository.findById(skill.getStoryId())
                .orElseThrow(() -> new ResourceNotFoundException(StoryAccessUtils.STORY_NOT_FOUND));
        StoryAccessUtils.validateReadAccess(story, appUserRepository);

        return new SkillDetailResponse(
                skill.getId(),
                skill.getStoryId(),
                skill.getName(),
                skill.getCategoryName(),
                skill.getLevelValue()
        );
    }

    public PageResponse<SkillListItemResponse> getSkillsByStory(
            Integer storyId,
            String categoryName,
            int page,
            int size,
            String sort
    ) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new ResourceNotFoundException(StoryAccessUtils.STORY_NOT_FOUND));
        StoryAccessUtils.validateReadAccess(story, appUserRepository);

        Pageable pageable = PaginationUtils.buildPageable(
                page,
                size,
                (sort == null || sort.isBlank() ? "name,asc" : sort),
                this::mapSortField
        );
        Page<Skill> result = skillRepository.findByStoryWithFilters(storyId, categoryName, pageable);

        return new PageResponse<>(
                result.getContent().stream()
                        .map(skill -> new SkillListItemResponse(
                                skill.getId(),
                                skill.getName(),
                                skill.getCategoryName()
                        ))
                        .toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages()
        );
    }

    public PageResponse<SkillSearchItemResponse> searchSkills(String q, int page, int size, String sort) {
        Pageable pageable = PaginationUtils.buildPageable(
                page,
                size,
                (sort == null || sort.isBlank() ? "name,asc" : sort),
                this::mapSortField
        );
        Page<Skill> result = skillRepository.searchByName(q == null ? "" : q, pageable);

        var content = result.getContent().stream()
                .filter(skill -> {
                    Story story = storyRepository.findById(skill.getStoryId()).orElse(null);
                    return story != null && StoryAccessUtils.canReadStory(story, appUserRepository);
                })
                .map(skill -> new SkillSearchItemResponse(
                        skill.getId(),
                        skill.getName()
                ))
                .toList();

        return new PageResponse<>(content, result.getNumber(), result.getSize(), result.getTotalElements(), result.getTotalPages());
    }

    public UpdateSkillResponse updateSkill(Integer id, UpdateSkillRequest request) {
        Skill skill = getEditableSkill(id);

        skill.setName(request.name());
        skill.setDescription(request.description());
        skill.setCategoryName(request.categoryName());
        skill.setLevelValue(request.levelValue());

        Skill saved = skillRepository.save(skill);

        return new UpdateSkillResponse(saved.getId(), saved.getName());
    }

    public MessageResponse deleteSkill(Integer id) {
        Skill skill = getEditableSkill(id);
        skillRepository.delete(skill);
        return new MessageResponse("Habilidad eliminada correctamente");
    }

    private Skill getEditableSkill(Integer id) {
        Skill skill = skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Habilidad no encontrada"));
        StoryAccessUtils.getEditableStory(skill.getStoryId(), storyRepository, appUserRepository);
        return skill;
    }
    // Métodos de autenticación, autorización y paginación se delegan a utilidades compartidas.

    private String mapSortField(String field) {
        return switch (field) {
            case "createdAt" -> "createdAt";
            case "updatedAt" -> "updatedAt";
            case "categoryName" -> "categoryName";
            default -> "name";
        };
    }
}