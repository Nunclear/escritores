package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MediaRepository extends JpaRepository<Media, Integer> {
    List<Media> findByChapterId(Integer chapterId);
}
