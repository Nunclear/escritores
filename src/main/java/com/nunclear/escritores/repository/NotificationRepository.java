package com.nunclear.escritores.repository;

import com.nunclear.escritores.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Integer> {

    Page<Notification> findByRecipientUserId(Integer recipientUserId, Pageable pageable);

    Page<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Integer recipientUserId, Pageable pageable);

    List<Notification> findByRecipientUserIdAndIsReadFalse(Integer recipientUserId);

    long countByRecipientUserIdAndIsReadFalse(Integer recipientUserId);

    void deleteByRecipientUserId(Integer recipientUserId);
}
