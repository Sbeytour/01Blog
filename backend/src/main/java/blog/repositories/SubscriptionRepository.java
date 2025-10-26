package blog.repositories;

import blog.entity.Subscription;
import blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

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

    // // Get all users that a specific user follows (for feed filtering)
    // @Query("SELECT s.following FROM Subscription s WHERE s.follower = :follower")
    // List<User> findFollowingUsers(@Param("follower") User follower);

    // // Get all followers of a user
    // @Query("SELECT s.follower FROM Subscription s WHERE s.following = :following")
    // List<User> findFollowers(@Param("following") User following);

    // // Get all users that a user is following
    // @Query("SELECT s.following FROM Subscription s WHERE s.follower = :follower")
    // List<User> findFollowing(@Param("follower") User follower);
}
