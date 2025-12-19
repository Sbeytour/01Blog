package blog.dto.response;

import java.time.LocalDateTime;

import blog.entity.Notification;
import blog.entity.NotificationType;

public class NotificationResponseDto {

    private Long id;
    private NotificationType type;
    private String message;
    private Long relatedPostId;
    private Boolean isRead;
    private LocalDateTime createdAt;
    private CreatorDto creator;

    public static class CreatorDto {
        private Long id;
        private String username;

        public CreatorDto() {
        }

        public CreatorDto(Long id, String username) {
            this.id = id;
            this.username = username;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

    // Constructors
    public NotificationResponseDto() {
    }

    // Factory method
    public static NotificationResponseDto fromEntity(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setType(notification.getType());
        dto.setMessage(notification.getMessage());
        dto.setRelatedPostId(notification.getRelatedPostId());
        dto.setIsRead(notification.getIsRead());
        dto.setCreatedAt(notification.getCreatedAt());

        CreatorDto creatorDto = new CreatorDto(
                notification.getCreator().getId(),
                notification.getCreator().getUsername()
                );
        dto.setCreator(creatorDto);

        return dto;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getRelatedPostId() {
        return relatedPostId;
    }

    public void setRelatedPostId(Long relatedPostId) {
        this.relatedPostId = relatedPostId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public CreatorDto getCreator() {
        return creator;
    }

    public void setCreator(CreatorDto creator) {
        this.creator = creator;
    }
}
