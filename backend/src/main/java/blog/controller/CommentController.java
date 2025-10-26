package blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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

    @Autowired
    private CommentService commentService;

    @PostMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponseDto createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequestDto createDto,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return commentService.createComment(postId, createDto, currentUser);
    }

    @GetMapping("/posts/{postId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public PagedCommentResponseDto getCommentsByPostId(
            @PathVariable Long postId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        return commentService.getCommentsByPostId(postId, page, size);
    }

    @PutMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.OK)
    public CommentResponseDto updateComment(
            @PathVariable Long commentId,
            @Valid @RequestBody CreateCommentRequestDto updateDto,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return commentService.updateComment(commentId, updateDto, currentUser.getId());
    }

    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long commentId,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        commentService.deleteComment(commentId, currentUser.getId());
    }
}
