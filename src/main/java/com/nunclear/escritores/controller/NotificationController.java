package com.nunclear.escritores.controller;

import com.nunclear.escritores.dto.response.NotificationResponse;
import com.nunclear.escritores.entity.Notification;
import com.nunclear.escritores.security.CustomUserDetails;
import com.nunclear.escritores.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public Page<NotificationResponse> getMyNotifications(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        Integer userId = getCurrentUserId();
        Page<Notification> notifications = notificationService.getNotificationsByRecipient(
                userId,
                PageRequest.of(page, size)
        );
        return notifications.map(this::mapToResponse);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public NotificationResponse getNotificationById(@PathVariable Integer id) {
        Notification notification = notificationService.getNotificationById(id);
        return mapToResponse(notification);
    }

    @PostMapping("/{id}/read")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public NotificationResponse markAsRead(@PathVariable Integer id) {
        Notification notification = notificationService.markAsRead(id);
        return mapToResponse(notification);
    }

    @PostMapping("/me/read-all")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public void markAllAsRead() {
        Integer userId = getCurrentUserId();
        notificationService.markAllAsRead(userId);
    }

    @GetMapping("/me/unread-count")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public long getUnreadCount() {
        Integer userId = getCurrentUserId();
        return notificationService.getUnreadCount(userId);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','MODERATOR','ADMIN')")
    public void deleteNotification(@PathVariable Integer id) {
        notificationService.deleteNotification(id);
    }

    private NotificationResponse mapToResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getRecipientUserId(),
                notification.getType(),
                notification.getRelatedUserId(),
                notification.getRelatedStoryId(),
                notification.getRelatedChapterId(),
                notification.getRelatedCommentId(),
                notification.getContent(),
                notification.getIsRead(),
                notification.getCreatedAt(),
                notification.getUpdatedAt()
        );
    }

    private Integer getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        return userDetails.id();
    }
}
