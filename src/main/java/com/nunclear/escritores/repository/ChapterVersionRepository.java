package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.ChapterVersion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChapterVersionRepository extends JpaRepository<ChapterVersion, Integer> {

    List<ChapterVersion> findByChapterId(Integer chapterId);

    List<ChapterVersion> findByChapterIdOrderByVersionNumberDesc(Integer chapterId);

    Optional<ChapterVersion> findByChapterIdAndVersionNumber(Integer chapterId, Integer versionNumber);

    Integer countByChapterId(Integer chapterId);

    void deleteByChapterId(Integer chapterId);
}
