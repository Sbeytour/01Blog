package blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.request.CreateCommentRequestDto;
import blog.dto.response.CommentResponseDto;
import blog.dto.response.PagedCommentResponseDto;
import blog.entity.User;
import blog.services.CommentService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CommentController {

    private final CommentService commentService;

    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<CommentResponseDto> createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto createDto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        CommentResponseDto response = commentService.createComment(postId, createDto, currentUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<PagedCommentResponseDto> getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        PagedCommentResponseDto response = commentService.getCommentsByPostId(postId, page, size);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/comments/{commentId}")
    public ResponseEntity<CommentResponseDto> updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequestDto updateDto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        CommentResponseDto response = commentService.updateComment(commentId, updateDto, currentUser.getId());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        commentService.deleteComment(commentId, currentUser.getId());

        return ResponseEntity.noContent().build();
    }
}
