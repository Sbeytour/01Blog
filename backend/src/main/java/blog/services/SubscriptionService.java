package blog.services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import blog.dto.response.UserResponseDto;
import blog.entity.Subscription;
import blog.entity.User;
import blog.repositories.SubscriptionRepository;
import blog.repositories.UserRepository;

@Service
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SubscriptionService(SubscriptionRepository subscriptionRepository, UserRepository userRepository) {
        this.userRepository = userRepository;
        this.subscriptionRepository = subscriptionRepository;
    }

    @Transactional
    public UserResponseDto followUser(Long followerId, Long followingId) {
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

        // Prepare response - return full user data for the user being followed
        UserResponseDto response = UserResponseDto.fromEntity(following);
        response.setIsFollowedByCurrentUser(true);
        response.setFollowersCount(subscriptionRepository.countByFollowing(following));
        response.setFollowingCount(subscriptionRepository.countByFollower(follower));

        return response;
    }

    @Transactional
    public UserResponseDto unfollowUser(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User to unfollow not found"));

        // Find and delete subscription
        Subscription subscription = subscriptionRepository.findByFollowerAndFollowing(follower, following)
                .orElseThrow(() -> new IllegalArgumentException("Not following this user"));

        subscriptionRepository.delete(subscription);

        // Prepare response - return full user data for the user being unfollowed
        UserResponseDto response = UserResponseDto.fromEntity(following);
        response.setIsFollowedByCurrentUser(false);
        response.setFollowersCount(subscriptionRepository.countByFollowing(following));
        response.setFollowingCount(subscriptionRepository.countByFollower(following));

        return response;
    }

    public boolean isFollowing(Long followerId, Long followingId) {
        User follower = userRepository.findById(followerId)
                .orElseThrow(() -> new RuntimeException("Follower user not found"));

        User following = userRepository.findById(followingId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.existsByFollowerAndFollowing(follower, following);
    }

    public long getFollowersCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.countByFollowing(user);
    }

    public long getFollowingCount(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return subscriptionRepository.countByFollower(user);
    }
}
