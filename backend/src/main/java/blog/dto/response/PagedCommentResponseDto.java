package blog.dto.response;

import java.util.List;

import org.springframework.data.domain.Page;


import blog.entity.Comment;

public class PagedCommentResponseDto {

    private List<CommentResponseDto> comments;

    private long totalComments;

    private int currentPage;

    private int totalPages;

    private boolean hasMore;

    public static PagedCommentResponseDto fromPage(Page<Comment> commentPage) {
        PagedCommentResponseDto dto = new PagedCommentResponseDto();
        dto.comments = commentPage.getContent().stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
        dto.totalComments = commentPage.getTotalElements();
        dto.currentPage = commentPage.getNumber();
        dto.totalPages = commentPage.getTotalPages();
        dto.hasMore = commentPage.hasNext();
        return dto;
    }

    public List<CommentResponseDto> getComments() {
        return comments;
    }

    public void setComments(List<CommentResponseDto> comments) {
        this.comments = comments;
    }

    public long getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(long totalComments) {
        this.totalComments = totalComments;
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(int currentPage) {
        this.currentPage = currentPage;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public boolean isHasMore() {
        return hasMore;
    }

    public void setHasMore(boolean hasMore) {
        this.hasMore = hasMore;
    }

}
