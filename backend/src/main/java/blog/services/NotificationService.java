package blog.services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.dto.response.NotificationResponseDto;
import blog.entity.Notification;
import blog.entity.NotificationType;
import blog.entity.Post;
import blog.entity.Subscription;
import blog.entity.User;
import blog.exceptions.UserNotFoundException;
import blog.repositories.NotificationRepository;
import blog.repositories.UserRepository;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public NotificationResponseDto createNotification(Long creatorId, Long recipientId, NotificationType type,
            Long relatedPostId, String message) {
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new UserNotFoundException("Creator not found"));

        User recipient = userRepository.findById(recipientId)
                .orElseThrow(() -> new UserNotFoundException("Recipient not found"));

        Notification notification = new Notification();
        notification.setType(type);
        notification.setMessage(message);
        notification.setRelatedPostId(relatedPostId);
        notification.setCreator(creator);
        notification.setRecipient(recipient);
        Notification savedNotification = notificationRepository.save(notification);

        return NotificationResponseDto.fromEntity(savedNotification);
    }

    public List<NotificationResponseDto> getNotifications(Long userId, boolean unreadOnly) {
        List<Notification> notifications;

        if (unreadOnly) {
            notifications = notificationRepository.findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);
        } else {
            notifications = notificationRepository.findByRecipientIdOrderByCreatedAtDesc(userId);
        }

        return notifications.stream()
                .map(NotificationResponseDto::fromEntity)
                .toList();
    }

    public Long getUnreadCount(Long userId) {
        return notificationRepository.countByRecipientIdAndIsReadFalse(userId);
    }

    @Transactional
    public NotificationResponseDto markAsRead(Long notificationId, Long userId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));

        if (notification.getRecipient().getId() != userId) {
            throw new RuntimeException("You are not authorized to modify this notification");
        }

        notification.setIsRead(true);
        Notification updatedNotification = notificationRepository.save(notification);

        return NotificationResponseDto.fromEntity(updatedNotification);
    }

    @Transactional
    public void markAllAsRead(Long userId) {
        List<Notification> unreadNotifications = notificationRepository
                .findByRecipientIdAndIsReadFalseOrderByCreatedAtDesc(userId);

        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }

        notificationRepository.saveAll(unreadNotifications);
    }

    @Transactional
    public void notifyFollowersAboutNewPost(Post post) {
        User postCreator = userRepository.findByUsernameOrEmail(post.getCreator().getUsername());

        List<Subscription> followers = postCreator.getFollowers();
        String message = postCreator.getUsername() + " created a new post";

        for (Subscription subscription : followers) {
            User follower = subscription.getFollower();

            if (follower.getId() != postCreator.getId()) {
                Notification notification = new Notification();
                notification.setType(NotificationType.NEW_POST);
                notification.setMessage(message);
                notification.setRelatedPostId(post.getId());
                notification.setCreator(postCreator);
                notification.setRecipient(follower);

                notificationRepository.save(notification);
            }
        }
    }
}
