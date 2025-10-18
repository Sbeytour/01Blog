package blog.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import blog.entity.Post;

public class PostResponseDto {
    private Long id;
    private String title;
    private String content;
    private UserResponseDto creator;
    private List<MediaResponseDto> media;
    private int commentsCount;
    private int likesCount;
    private boolean isLikedByCurrentUser;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PostResponseDto fromEntity(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.id = post.getId();
        dto.title = post.getTitle();
        dto.content = post.getContent();
        dto.creator = UserResponseDto.fromEntity(post.getCreator());
        dto.media = post.getMediaList().stream()
                .map(MediaResponseDto::fromEntity)
                .collect(Collectors.toList());
        dto.createdAt = post.getCreatedAt();
        dto.updatedAt = post.getUpdatedAt();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public UserResponseDto getCreator() {
        return creator;
    }

    public void setCreator(UserResponseDto creator) {
        this.creator = creator;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(int likesCount) {
        this.likesCount = likesCount;
    }

    public boolean isLikedByCurrentUser() {
        return isLikedByCurrentUser;
    }

    public void setLikedByCurrentUser(boolean isLikedByCurrentUser) {
        this.isLikedByCurrentUser = isLikedByCurrentUser;
    }

    public List<MediaResponseDto> getMedia() {
        return media;
    }

    public void setMedia(List<MediaResponseDto> media) {
        this.media = media;
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