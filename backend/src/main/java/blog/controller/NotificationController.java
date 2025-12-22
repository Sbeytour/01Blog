package blog.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.response.NotificationResponseDto;
import blog.entity.User;
import blog.services.NotificationService;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDto>> getNotifications(
            @RequestParam(defaultValue = "false") boolean unreadOnly,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        List<NotificationResponseDto> notifications = notificationService.getNotifications(
                currentUser.getId(),
                unreadOnly);

        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getUnreadCount(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Long count = notificationService.getUnreadCount(currentUser.getId());

        return ResponseEntity.ok(count);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<NotificationResponseDto> markAsRead(
            @PathVariable Long notificationId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        NotificationResponseDto response = notificationService.markAsRead(notificationId, currentUser.getId());

        return ResponseEntity.ok(response);
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<Void> markAllAsRead(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        notificationService.markAllAsRead(currentUser.getId());

        return ResponseEntity.noContent().build();
    }
}
