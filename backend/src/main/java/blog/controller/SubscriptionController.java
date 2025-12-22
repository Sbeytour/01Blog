package blog.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import blog.dto.response.UserResponseDto;
import blog.entity.User;
import blog.services.SubscriptionService;

@RestController
@RequestMapping("/api/users")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    public SubscriptionController(SubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        try {
            UserResponseDto response = subscriptionService.followUser(currentUser.getId(), userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to follow user");
        }
    }

    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        try {
            UserResponseDto response = subscriptionService.unfollowUser(currentUser.getId(), userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to unfollow user");
        }
    }

    @GetMapping("/{userId}/isfollowing")
    public ResponseEntity<?> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        try {
            boolean isFollowing = subscriptionService.isFollowing(currentUser.getId(), userId);
            return ResponseEntity.ok(isFollowing);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to check follow status");
        }
    }
}
