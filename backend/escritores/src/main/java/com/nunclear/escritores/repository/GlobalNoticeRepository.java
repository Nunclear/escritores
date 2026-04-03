package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.GlobalNotice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface GlobalNoticeRepository extends JpaRepository<GlobalNotice, Integer> {
    List<GlobalNotice> findByIsEnabled(Boolean isEnabled);
}
