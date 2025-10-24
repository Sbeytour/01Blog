package blog.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;

public class LikeResponseDto {
    @JsonProperty("isLiked")
    private boolean isLiked;
    private long likesCount;

    public LikeResponseDto(boolean isLiked, long likesCount) {
        this.isLiked = isLiked;
        this.likesCount = likesCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean isLiked) {
        this.isLiked = isLiked;
    }

    public long getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(long likesCount) {
        this.likesCount = likesCount;
    }
}
