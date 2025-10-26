package blog.services;

import blog.entity.Subscription;
import blog.entity.User;
import blog.repositories.SubscriptionRepository;
import blog.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SubscriptionService {

    @Autowired
    private SubscriptionRepository subscriptionRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Follow a user
     * @param followerId ID of the user who wants to follow
     * @param followingId ID of the user to be followed
     * @return Response with success status and counts
     */
    @Transactional
    public Map<String, Object> followUser(Long followerId, Long followingId) {
        // Prevent self-following
        if (followerId.equals(followingId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }

        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to follow not found"));

        // Check if already following
        if (subscriptionRepository.existsByFollowerAndFollowing(follower, following)) {
            throw new IllegalArgumentException("Already following this user");
        }

        // Create subscription
        Subscription subscription = new Subscription();
        subscription.setFollower(follower);
        subscription.setFollowing(following);
        subscriptionRepository.save(subscription);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", true);
        response.put("followersCount", subscriptionRepository.countByFollowing(following));
        response.put("followingCount", subscriptionRepository.countByFollower(follower));

        return response;
    }

    /**
     * Unfollow a user
     * @param followerId ID of the user who wants to unfollow
     * @param followingId ID of the user to be unfollowed
     * @return Response with success status and counts
     */
    @Transactional
    public Map<String, Object> unfollowUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        // Find and delete subscription
        Subscription subscription = subscriptionRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalArgumentException("Not following this user"));

        subscriptionRepository.delete(subscription);

        // Prepare response
        Map<String, Object> response = new HashMap<>();
        response.put("isFollowing", false);
        response.put("followersCount", subscriptionRepository.countByFollowing(following));
        response.put("followingCount", subscriptionRepository.countByFollower(follower));

        return response;
    }

    /**
     * Check if a user follows another user
     */
    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.existsByFollowerAndFollowing(follower, following);
    }

    /**
     * Get followers count for a user
     */
    public long getFollowersCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.countByFollowing(user);
    }

    /**
     * Get following count for a user
     */
    public long getFollowingCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.countByFollower(user);
    }

    /**
     * Get all users that a user follows (for feed filtering)
     */
    public List<User> getFollowingUsers(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.findFollowingUsers(user);
    }
}
