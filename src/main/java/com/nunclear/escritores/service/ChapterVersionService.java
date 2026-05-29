package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.Chapter;
import com.nunclear.escritores.entity.ChapterVersion;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.ChapterVersionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChapterVersionService {

    private static final String VERSION_NOT_FOUND = "Versión de capítulo no encontrada";

    private final ChapterVersionRepository chapterVersionRepository;

    public ChapterVersion createVersion(Chapter chapter) {
        Integer nextVersionNumber = chapterVersionRepository.countByChapterId(chapter.getId()) + 1;

        ChapterVersion version = new ChapterVersion();
        version.setChapterId(chapter.getId());
        version.setVersionNumber(nextVersionNumber);
        version.setTitle(chapter.getTitle());
        version.setSubtitle(chapter.getSubtitle());
        version.setContent(chapter.getContent());

        return chapterVersionRepository.save(version);
    }

    public ChapterVersion getVersionByChapterAndNumber(Integer chapterId, Integer versionNumber) {
        return chapterVersionRepository.findByChapterIdAndVersionNumber(chapterId, versionNumber)
                .orElseThrow(() -> new ResourceNotFoundException(VERSION_NOT_FOUND));
    }

    public List<ChapterVersion> getVersionsByChapter(Integer chapterId) {
        return chapterVersionRepository.findByChapterIdOrderByVersionNumberDesc(chapterId);
    }

    public Integer getVersionCount(Integer chapterId) {
        Integer count = chapterVersionRepository.countByChapterId(chapterId);
        return count != null ? count : 0;
    }

    public ChapterVersion getLatestVersion(Integer chapterId) {
        List<ChapterVersion> versions = chapterVersionRepository.findByChapterIdOrderByVersionNumberDesc(chapterId);
        return versions.isEmpty() ? null : versions.get(0);
    }

    public void deleteVersionsForChapter(Integer chapterId) {
        chapterVersionRepository.deleteByChapterId(chapterId);
    }

    public ChapterVersion restoreVersion(Integer chapterId, Integer versionNumber, Chapter chapter) {
        ChapterVersion version = getVersionByChapterAndNumber(chapterId, versionNumber);

        chapter.setTitle(version.getTitle());
        chapter.setSubtitle(version.getSubtitle());
        chapter.setContent(version.getContent());

        return version;
    }
}
