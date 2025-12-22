package blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.response.LikeResponseDto;
import blog.entity.User;
import blog.services.LikeService;

@RestController
@RequestMapping("/api/post")
public class LikeController {

    private final LikeService likeService;

    public LikeController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<LikeResponseDto> toggleLike(@PathVariable Long postId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        LikeResponseDto response = likeService.toggleLike(postId, currentUser);

        HttpStatus status = response.isLiked() ? HttpStatus.CREATED : HttpStatus.OK;
        return ResponseEntity.status(status).body(response);
    }
}
