package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.ContentReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ContentReportRepository extends JpaRepository<ContentReport, Integer> {
    List<ContentReport> findByStatusName(String statusName);
    List<ContentReport> findByReporterUserId(Integer userId);
    List<ContentReport> findByStoryId(Integer storyId);
    List<ContentReport> findByStatusNameOrderByCreatedAt(String statusName);
}
