package blog.controller;

import blog.entity.User;
import blog.services.SubscriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class SubscriptionController {

    @Autowired
    private SubscriptionService subscriptionService;

    /**
     * Follow a user
     * POST /api/users/{userId}/follow
     */
    @PostMapping("/{userId}/follow")
    public ResponseEntity<?> followUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        try {
            Map<String, Object> response = subscriptionService.followUser(currentUser.getId(), userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to follow user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Unfollow a user
     * DELETE /api/users/{userId}/unfollow
     */
    @DeleteMapping("/{userId}/unfollow")
    public ResponseEntity<?> unfollowUser(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        try {
            Map<String, Object> response = subscriptionService.unfollowUser(currentUser.getId(), userId);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to unfollow user");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Check if current user follows a specific user
     * GET /api/users/{userId}/is-following
     */
    @GetMapping("/{userId}/is-following")
    public ResponseEntity<?> isFollowing(
            @PathVariable Long userId,
            @AuthenticationPrincipal User currentUser) {
        try {
            boolean isFollowing = subscriptionService.isFollowing(currentUser.getId(), userId);
            Map<String, Boolean> response = new HashMap<>();
            response.put("isFollowing", isFollowing);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to check follow status");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}
