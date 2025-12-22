package blog.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import blog.entity.Subscription;
import blog.entity.User;

@Repository
public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {

    // Find subscription between two users
    Optional<Subscription> findByFollowerAndFollowing(User follower, User following);

    // Check if a user follows another user
    boolean existsByFollowerAndFollowing(User follower, User following);

    // Count followers of a user
    long countByFollowing(User following);

    // Count how many users a user is following
    long countByFollower(User follower);

}
