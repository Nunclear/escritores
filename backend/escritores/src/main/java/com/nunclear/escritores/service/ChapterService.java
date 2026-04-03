package com.nunclear.escritores.service;

import com.nunclear.escritores.dto.ChapterDTO;
import com.nunclear.escritores.entity.Chapter;
import com.nunclear.escritores.entity.Story;
import com.nunclear.escritores.entity.Volume;
import com.nunclear.escritores.repository.ChapterRepository;
import com.nunclear.escritores.repository.StoryRepository;
import com.nunclear.escritores.repository.VolumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ChapterService {

    private final ChapterRepository chapterRepository;
    private final StoryRepository storyRepository;
    private final VolumeRepository volumeRepository;

    public ChapterDTO createChapter(ChapterDTO chapterDTO, Integer storyId, Integer userId) {
        Story story = storyRepository.findById(storyId)
                .orElseThrow(() -> new IllegalArgumentException("Story not found"));

        if (!story.getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this story");
        }

        Chapter chapter = Chapter.builder()
                .title(chapterDTO.getTitle())
                .subtitle(chapterDTO.getSubtitle())
                .content(chapterDTO.getContent())
                .publishedOn(chapterDTO.getPublishedOn())
                .story(story)
                .publicationState(chapterDTO.getPublicationState() != null ? chapterDTO.getPublicationState() : "draft")
                .build();

        if (chapterDTO.getVolumeId() != null) {
            Volume volume = volumeRepository.findById(chapterDTO.getVolumeId())
                    .orElseThrow(() -> new IllegalArgumentException("Volume not found"));
            chapter.setVolume(volume);
        }

        Chapter savedChapter = chapterRepository.save(chapter);
        return mapToDTO(savedChapter);
    }

    public ChapterDTO getChapterById(Integer id) {
        return chapterRepository.findById(id)
                .map(this::mapToDTO)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));
    }

    public List<ChapterDTO> getChaptersByStoryId(Integer storyId) {
        return chapterRepository.findByStoryIdOrderByPositionIndex(storyId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public List<ChapterDTO> getPublishedChapters(Integer storyId) {
        return chapterRepository.findByStoryIdAndPublicationState(storyId, "published").stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    public ChapterDTO updateChapter(Integer id, ChapterDTO chapterDTO, Integer userId) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!chapter.getStory().getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this chapter's story");
        }

        if (chapterDTO.getTitle() != null) {
            chapter.setTitle(chapterDTO.getTitle());
        }
        if (chapterDTO.getSubtitle() != null) {
            chapter.setSubtitle(chapterDTO.getSubtitle());
        }
        if (chapterDTO.getContent() != null) {
            chapter.setContent(chapterDTO.getContent());
            chapter.setReadingMinutes(estimateReadingTime(chapterDTO.getContent()));
            chapter.setWordCount(countWords(chapterDTO.getContent()));
        }
        if (chapterDTO.getPublicationState() != null) {
            chapter.setPublicationState(chapterDTO.getPublicationState());
        }
        if (chapterDTO.getPublishedOn() != null) {
            chapter.setPublishedOn(chapterDTO.getPublishedOn());
        }

        Chapter updatedChapter = chapterRepository.save(chapter);
        return mapToDTO(updatedChapter);
    }

    public void deleteChapter(Integer id, Integer userId) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!chapter.getStory().getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this chapter's story");
        }

        chapterRepository.delete(chapter);
    }

    public void archiveChapter(Integer id, Integer userId) {
        Chapter chapter = chapterRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chapter not found"));

        if (!chapter.getStory().getOwnerUser().getId().equals(userId)) {
            throw new IllegalArgumentException("User is not the owner of this chapter's story");
        }

        chapter.setArchivedAt(LocalDateTime.now());
        chapterRepository.save(chapter);
    }

    private int estimateReadingTime(String content) {
        if (content == null) return 0;
        int wordCount = countWords(content);
        return Math.max(1, wordCount / 200);
    }

    private int countWords(String content) {
        if (content == null || content.isEmpty()) return 0;
        String[] words = content.trim().split("\\s+");
        return words.length;
    }

    private ChapterDTO mapToDTO(Chapter chapter) {
        return ChapterDTO.builder()
                .id(chapter.getId())
                .title(chapter.getTitle())
                .subtitle(chapter.getSubtitle())
                .content(chapter.getContent())
                .publishedOn(chapter.getPublishedOn())
                .storyId(chapter.getStory().getId())
                .volumeId(chapter.getVolume() != null ? chapter.getVolume().getId() : null)
                .positionIndex(chapter.getPositionIndex())
                .readingMinutes(chapter.getReadingMinutes())
                .wordCount(chapter.getWordCount())
                .publicationState(chapter.getPublicationState())
                .createdAt(chapter.getCreatedAt())
                .updatedAt(chapter.getUpdatedAt())
                .archivedAt(chapter.getArchivedAt())
                .build();
    }
}
