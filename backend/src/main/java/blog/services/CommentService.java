package blog.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import blog.dto.request.CreateCommentRequestDto;
import blog.dto.response.CommentResponseDto;
import blog.entity.Comment;
import blog.entity.Post;
import blog.entity.User;
import blog.repositories.CommentRepository;
import blog.repositories.PostRepository;

@Service
public class CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private PostRepository postRepository;

    public CommentResponseDto createComment(Long postId, CreateCommentRequestDto createDto, User currentUser) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found with id: " + postId));

        Comment comment = new Comment();
        comment.setContent(createDto.getContent());
        comment.setUser(currentUser);
        comment.setPost(post);

        Comment savedComment = commentRepository.save(comment);
        return CommentResponseDto.fromEntity(savedComment);
    }

    public CommentResponseDto updateComment(Long commentId, CreateCommentRequestDto updateDto, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (comment.getUser().getId() != currentUserId) {
            throw new RuntimeException("You are not authorized to update this comment");
        }

        comment.setContent(updateDto.getContent());
        Comment updatedComment = commentRepository.save(comment);
        return CommentResponseDto.fromEntity(updatedComment);
    }

    public void deleteComment(Long commentId, Long currentUserId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found with id: " + commentId));

        if (comment.getUser().getId() != currentUserId) {
            throw new RuntimeException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        if (!postRepository.existsById(postId)) {
            throw new RuntimeException("Post not found with id: " + postId);
        }

        List<Comment> comments = commentRepository.findByPostId(postId);
        return comments.stream()
                .map(CommentResponseDto::fromEntity)
                .toList();
    }
}
