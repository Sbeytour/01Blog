package blog.dto.response;

import java.time.LocalDateTime;

import blog.entity.Comment;

public class CommentResponseDto {

    private Long id;
    private String content;
    private UserResponseDto user;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static CommentResponseDto fromEntity(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.id = comment.getId();
        dto.content = comment.getContent();
        dto.user = UserResponseDto.fromEntity(comment.getUser());
        dto.createdAt = comment.getCreatedAt();
        dto.updatedAt = comment.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserResponseDto getUser() {
        return user;
    }

    public void setUser(UserResponseDto user) {
        this.user = user;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

}
