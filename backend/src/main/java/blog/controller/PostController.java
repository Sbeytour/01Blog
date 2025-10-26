package blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.request.CreatePostRequestDto;
import blog.dto.response.PostResponseDto;
import blog.entity.User;
import blog.services.PostService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/post")
public class PostController {

    @Autowired
    private PostService postService;

    @PostMapping
    public ResponseEntity<PostResponseDto> createPost(
            @Valid @RequestParam String title,
            @Valid @ModelAttribute CreatePostRequestDto createDto,
            Authentication authentication) {

        User creator = (User) authentication.getPrincipal();
        PostResponseDto response = postService.createPost(createDto, creator);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<PostResponseDto>> getAllPosts(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        List<PostResponseDto> posts = postService.getAllPosts(currentUser.getId());

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PostResponseDto>> getPostsByUser(
            @PathVariable Long userId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        List<PostResponseDto> posts = postService.getPostsByUser(userId, currentUser.getId());

        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDto> getSinglePost(
            @PathVariable Long postId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        PostResponseDto post = postService.getSinglePost(postId, currentUser.getId());

        return ResponseEntity.ok(post);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDto> updatePost(@PathVariable Long postId,
            @Valid @ModelAttribute CreatePostRequestDto updateDto,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        PostResponseDto updatedPost = postService.updatePost(postId, updateDto, currentUser.getId());

        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long postId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        postService.deletePost(postId, currentUser.getId());

        return ResponseEntity.noContent().build();
    }

}
