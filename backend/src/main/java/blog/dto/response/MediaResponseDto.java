package blog.dto.response;

import blog.entity.Media;
import blog.entity.MediaType;

public class MediaResponseDto {
    private Long id;
    private String url;
    private MediaType type;

    public static MediaResponseDto fromEntity(Media media) {
        MediaResponseDto dto = new MediaResponseDto();
        dto.id = media.getId();
        dto.url = media.getUrl();
        dto.type = media.getType();
        return dto;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public MediaType getType() {
        return type;
    }

    public void setType(MediaType type) {
        this.type = type;
    }
}