package blog.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import blog.dto.request.CreateCommentRequestDto;
import blog.dto.response.CommentResponseDto;
import blog.dto.response.PagedCommentResponseDto;
import blog.entity.Comment;
import blog.entity.Post;
import blog.entity.User;
import blog.exceptions.ResourceNotFoundException;
import blog.repositories.CommentRepository;
import blog.repositories.PostRepository;
import blog.util.ValidationUtils;

@Service
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    public CommentResponseDto createComment(Long postId, CreateCommentRequestDto createDto, User currentUser) {
        Post post = ValidationUtils.validatePostExists(postId, postRepository);

        Comment comment = new Comment();
        comment.setContent(createDto.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return CommentResponseDto.fromEntity(savedComment);
    }

    public CommentResponseDto updateComment(Long commentId, CreateCommentRequestDto updateDto, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        ValidationUtils.validateOwnership(comment.getUser().getId(), currentUserId, "comment");

        comment.setContent(updateDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return CommentResponseDto.fromEntity(updatedComment);
    }

    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found with id: " + commentId));

        ValidationUtils.validateOwnership(comment.getUser().getId(), currentUserId, "comment");

        commentRepository.delete(comment);
    }

    public PagedCommentResponseDto getCommentsByPostId(Long postId, int page, int size) {
        ValidationUtils.validatePostExists(postId, postRepository);

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Comment> commentPage = commentRepository.findByPostId(postId, pageable);
        return PagedCommentResponseDto.fromPage(commentPage);
    }
}
