package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.StoryDTO;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.AppUser;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.AppUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StoryService {

    private final StoryRepository storyRepository;
    private final AppUserRepository userRepository;

    public StoryDTO createStory(StoryDTO storyDTO, Integer userId) {
        AppUser owner = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Story story = Story.builder()
                .title(storyDTO.getTitle())
                .slugText(generateSlug(storyDTO.getTitle()))
                .description(storyDTO.getDescription())
                .coverImageUrl(storyDTO.getCoverImageUrl())
                .ownerUser(owner)
                .visibilityState(storyDTO.getVisibilityState() != null ? storyDTO.getVisibilityState() : "public")
                .publicationState(storyDTO.getPublicationState() != null ? storyDTO.getPublicationState() : "draft")
                .allowFeedback(storyDTO.getAllowFeedback() != null ? storyDTO.getAllowFeedback() : true)
                .allowScores(storyDTO.getAllowScores() != null ? storyDTO.getAllowScores() : true)
                .startedOn(storyDTO.getStartedOn())
                .build();

        Story savedStory = storyRepository.save(story);
        return mapToDTO(savedStory);
    }

    public StoryDTO getStoryById(Integer id) {
        return storyRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));
    }

    public List<StoryDTO> getStoriesByUserId(Integer userId) {
        return storyRepository.findByOwnerUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<StoryDTO> getPublishedStories() {
        return storyRepository.findPublishedAndPublicStories().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<StoryDTO> getStoriesByVisibility(String visibility) {
        return storyRepository.findByVisibilityState(visibility).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public StoryDTO updateStory(Integer id, StoryDTO storyDTO, Integer userId) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (!story.getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this story");
        }

        if (storyDTO.getTitle() != null) {
            story.setTitle(storyDTO.getTitle());
        }
        if (storyDTO.getDescription() != null) {
            story.setDescription(storyDTO.getDescription());
        }
        if (storyDTO.getCoverImageUrl() != null) {
            story.setCoverImageUrl(storyDTO.getCoverImageUrl());
        }
        if (storyDTO.getVisibilityState() != null) {
            story.setVisibilityState(storyDTO.getVisibilityState());
        }
        if (storyDTO.getPublicationState() != null) {
            story.setPublicationState(storyDTO.getPublicationState());
            if ("published".equals(storyDTO.getPublicationState())) {
                story.setPublishedAt(LocalDateTime.now());
            }
        }
        if (storyDTO.getAllowFeedback() != null) {
            story.setAllowFeedback(storyDTO.getAllowFeedback());
        }
        if (storyDTO.getAllowScores() != null) {
            story.setAllowScores(storyDTO.getAllowScores());
        }

        Story updatedStory = storyRepository.save(story);
        return mapToDTO(updatedStory);
    }

    public void deleteStory(Integer id, Integer userId) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (!story.getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this story");
        }

        storyRepository.delete(story);
    }

    public void archiveStory(Integer id, Integer userId) {
        Story story = storyRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (!story.getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this story");
        }

        story.setArchivedAt(LocalDateTime.now());
        story.setPublicationState("archived");
        storyRepository.save(story);
    }

    private String generateSlug(String title) {
        return title.toLowerCase()
                .replaceAll("\\s+", "-")
                .replaceAll("[^a-z0-9\\-]", "")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
    }

    private StoryDTO mapToDTO(Story story) {
        return StoryDTO.builder()
                .id(story.getId())
                .ownerUserId(story.getOwnerUser().getId())
                .ownerUserName(story.getOwnerUser().getDisplayName())
                .title(story.getTitle())
                .slugText(story.getSlugText())
                .description(story.getDescription())
                .coverImageUrl(story.getCoverImageUrl())
                .visibilityState(story.getVisibilityState())
                .publicationState(story.getPublicationState())
                .allowFeedback(story.getAllowFeedback())
                .allowScores(story.getAllowScores())
                .startedOn(story.getStartedOn())
                .publishedAt(story.getPublishedAt())
                .createdAt(story.getCreatedAt())
                .updatedAt(story.getUpdatedAt())
                .archivedAt(story.getArchivedAt())
                .build();
    }
}
