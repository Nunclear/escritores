package com.nunclear.escritores.service;

import com.nunclear.escritores.entity.Notification;
import com.nunclear.escritores.enums.NotificationType;
import com.nunclear.escritores.exception.ResourceNotFoundException;
import com.nunclear.escritores.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private static final String NOTIFICATION_NOT_FOUND = "Notificación no encontrada";

    private final NotificationRepository notificationRepository;

    public Notification createNotification(
            Integer recipientUserId,
            NotificationType type,
            Integer relatedUserId,
            Integer relatedStoryId,
            Integer relatedChapterId,
            Integer relatedCommentId,
            String content
    ) {
        Notification notification = new Notification();
        notification.setRecipientUserId(recipientUserId);
        notification.setType(type);
        notification.setRelatedUserId(relatedUserId);
        notification.setRelatedStoryId(relatedStoryId);
        notification.setRelatedChapterId(relatedChapterId);
        notification.setRelatedCommentId(relatedCommentId);
        notification.setContent(content);
        notification.setIsRead(false);

        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(Integer id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(NOTIFICATION_NOT_FOUND));
    }

    public Page<Notification> getNotificationsByRecipient(Integer userId, Pageable pageable) {
        return notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    public List<Notification> getUnreadNotifications(Integer userId) {
        return notificationRepository.findByRecipientUserIdAndIsReadFalse(userId);
    }

    public long getUnreadCount(Integer userId) {
        return notificationRepository.countByRecipientUserIdAndIsReadFalse(userId);
    }

    public Notification markAsRead(Integer notificationId) {
        Notification notification = getNotificationById(notificationId);
        notification.setIsRead(true);
        return notificationRepository.save(notification);
    }

    public void markAllAsRead(Integer userId) {
        List<Notification> unreadNotifications = getUnreadNotifications(userId);
        unreadNotifications.forEach(n -> n.setIsRead(true));
        notificationRepository.saveAll(unreadNotifications);
    }

    public void deleteNotification(Integer notificationId) {
        Notification notification = getNotificationById(notificationId);
        notificationRepository.delete(notification);
    }

    public void deleteNotificationsByRecipient(Integer userId) {
        notificationRepository.deleteByRecipientUserId(userId);
    }
}
