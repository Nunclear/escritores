package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.request.SaveReadingProgressRequest;
import com.nunclear.escritores.dto.response.ReadingProgressResponse;
import com.nunclear.escritores.entity.ReadingProgress;
import com.nunclear.escritores.security.CustomUserDetails;
import com.nunclear.escritores.service.ReadingProgressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reading-progress")
@RequiredArgsConstructor
public class ReadingProgressController {

    private final ReadingProgressService readingProgressService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ReadingProgressResponse saveProgress(@Valid @RequestBody SaveReadingProgressRequest request) {
        Integer userId = getCurrentUserId();
        ReadingProgress progress = readingProgressService.saveOrUpdateProgress(
                userId,
                request.storyId(),
                request.lastChapterId(),
                request.percentageRead()
        );
        return mapToResponse(progress);
    }

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public List<ReadingProgressResponse> getMyProgress() {
        Integer userId = getCurrentUserId();
        List<ReadingProgress> progressList = readingProgressService.getProgressByUser(userId);
        return progressList.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/story/{storyId}/me")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ReadingProgressResponse getStoryProgress(@PathVariable Integer storyId) {
        Integer userId = getCurrentUserId();
        ReadingProgress progress = readingProgressService.getProgressByUserAndStory(userId, storyId);
        return mapToResponse(progress);
    }

    @GetMapping("/story/{storyId}")
    public List<ReadingProgressResponse> getProgressByStory(@PathVariable Integer storyId) {
        List<ReadingProgress> progressList = readingProgressService.getProgressByStory(storyId);
        return progressList.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @DeleteMapping("/story/{storyId}/me")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public void deleteMyProgress(@PathVariable Integer storyId) {
        Integer userId = getCurrentUserId();
        readingProgressService.deleteProgress(userId, storyId);
    }

    private ReadingProgressResponse mapToResponse(ReadingProgress progress) {
        return new ReadingProgressResponse(
                progress.getId(),
                progress.getUserId(),
                progress.getStoryId(),
                progress.getLastChapterId(),
                progress.getLastReadAt(),
                progress.getPercentageRead(),
                progress.getCreatedAt(),
                progress.getUpdatedAt()
        );
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.getId();
    }
}
