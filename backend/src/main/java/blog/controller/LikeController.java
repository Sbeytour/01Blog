package blog.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.response.LikeResponseDto;
import blog.entity.User;
import blog.services.LikeService;

@RestController
@RequestMapping("/api/post")
public class LikeController {

    @Autowired
    private LikeService likeService;

    @PostMapping("/{postId}/like")
    @ResponseStatus(HttpStatus.OK)
    public LikeResponseDto toggleLike(@PathVariable Long postId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return likeService.toggleLike(postId, currentUser);
    }
}
