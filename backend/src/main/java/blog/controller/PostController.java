package blog.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
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
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponseDto createPost(@Valid @RequestParam String title, @Valid @ModelAttribute CreatePostRequestDto createDto, Authentication authentication) {

        User creator = (User) authentication.getPrincipal();
        return postService.createPost(createDto, creator);
    }

    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDto> getAllPosts(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return postService.getAllPosts(currentUser.getId());
    }

    @GetMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public List<PostResponseDto> getPostsByUser(@PathVariable Long userId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return postService.getPostsByUser(userId, currentUser.getId());
    }

    @GetMapping("{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseDto getSinglePost(@PathVariable Long postId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return postService.getSinglePost(postId, currentUser.getId());
    }

    @PutMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public PostResponseDto updatePost(@PathVariable Long postId,@Valid @ModelAttribute CreatePostRequestDto updateDto,
            Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return postService.updatePost(postId, updateDto, currentUser.getId());
    }

    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePost(
            @PathVariable Long postId,
            Authentication authentication) {

        User currentUser = (User) authentication.getPrincipal();
        postService.deletePost(postId, currentUser.getId());
    }

}
