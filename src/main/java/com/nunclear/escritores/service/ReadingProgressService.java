package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.ReadingProgress;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.ReadingProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReadingProgressService {

    private static final String PROGRESS_NOT_FOUND = "Progreso de lectura no encontrado";

    private final ReadingProgressRepository readingProgressRepository;

    public ReadingProgress saveOrUpdateProgress(
            Integer userId,
            Integer storyId,
            Integer lastChapterId,
            Integer percentageRead
    ) {
        ReadingProgress progress = readingProgressRepository.findByUserIdAndStoryId(userId, storyId)
                .orElseGet(() -> {
                    ReadingProgress newProgress = new ReadingProgress();
                    newProgress.setUserId(userId);
                    newProgress.setStoryId(storyId);
                    return newProgress;
                });

        if (lastChapterId != null) {
            progress.setLastChapterId(lastChapterId);
        }
        if (percentageRead != null && percentageRead >= 0 && percentageRead <= 100) {
            progress.setPercentageRead(percentageRead);
        }
        progress.setLastReadAt(LocalDateTime.now());

        return readingProgressRepository.save(progress);
    }

    public ReadingProgress getProgressByUserAndStory(Integer userId, Integer storyId) {
        return readingProgressRepository.findByUserIdAndStoryId(userId, storyId)
                .orElseThrow(() -> new ResourceNotFoundException(PROGRESS_NOT_FOUND));
    }

    public List<ReadingProgress> getProgressByUser(Integer userId) {
        return readingProgressRepository.findByUserId(userId);
    }

    public List<ReadingProgress> getProgressByStory(Integer storyId) {
        return readingProgressRepository.findByStoryId(storyId);
    }

    public void deleteProgress(Integer userId, Integer storyId) {
        if (!readingProgressRepository.existsByUserIdAndStoryId(userId, storyId)) {
            throw new ResourceNotFoundException(PROGRESS_NOT_FOUND);
        }
        readingProgressRepository.deleteByUserIdAndStoryId(userId, storyId);
    }

    public void deleteUserProgress(Integer userId) {
        List<ReadingProgress> progressList = readingProgressRepository.findByUserId(userId);
        readingProgressRepository.deleteAll(progressList);
    }

    public void deleteStoryProgress(Integer storyId) {
        List<ReadingProgress> progressList = readingProgressRepository.findByStoryId(storyId);
        readingProgressRepository.deleteAll(progressList);
    }
}
