package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.request.RestoreChapterVersionRequest;
import com.nunclear.escritores.dto.response.ChapterVersionResponse;
import com.nunclear.escritores.entity.ChapterVersion;
import com.nunclear.escritores.service.ChapterVersionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chapters/{chapterId}/versions")
@RequiredArgsConstructor
public class ChapterVersionController {

    private final ChapterVersionService chapterVersionService;

    @GetMapping
    public List<ChapterVersionResponse> getVersions(@PathVariable Integer chapterId) {
        List<ChapterVersion> versions = chapterVersionService.getVersionsByChapter(chapterId);
        return versions.stream()
                .map(this::mapToResponse)
                .toList();
    }

    @GetMapping("/{versionNumber}")
    public ChapterVersionResponse getVersion(
            @PathVariable Integer chapterId,
            @PathVariable Integer versionNumber
    ) {
        ChapterVersion version = chapterVersionService.getVersionByChapterAndNumber(chapterId, versionNumber);
        return mapToResponse(version);
    }

    @GetMapping("/latest")
    public ChapterVersionResponse getLatestVersion(@PathVariable Integer chapterId) {
        ChapterVersion version = chapterVersionService.getLatestVersion(chapterId);
        if (version == null) {
            throw new com.nunclear.escritores.exception.ResourceNotFoundException("No hay versiones disponibles");
        }
        return mapToResponse(version);
    }

    @PostMapping("/{versionNumber}/restore")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public ChapterVersionResponse restoreVersion(
            @PathVariable Integer chapterId,
            @PathVariable Integer versionNumber,
            @Valid @RequestBody RestoreChapterVersionRequest request
    ) {
        ChapterVersion version = chapterVersionService.getVersionByChapterAndNumber(chapterId, versionNumber);
        return mapToResponse(version);
    }

    private ChapterVersionResponse mapToResponse(ChapterVersion version) {
        return new ChapterVersionResponse(
                version.getId(),
                version.getChapterId(),
                version.getVersionNumber(),
                version.getTitle(),
                version.getSubtitle(),
                version.getContent(),
                version.getCreatedAt()
        );
    }
}
