package blog.dto.response;

public class LikeResponseDto {
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
