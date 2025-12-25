package blog.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.entity.Post;
import blog.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
        // find all posts ordered by created Date
        @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
        Page<Post> findAllPosts(Pageable pageable);

        @Query("SELECT p FROM Post p WHERE p.creator.id = :userId AND p.isHidden = false ORDER BY p.createdAt DESC")
        Page<Post> findByUserIdOrderByCreatedAtDesc(@Param("userId") Long userId, Pageable pageable);

        // Find posts from users that the current user follows with pagination
        @Query("SELECT p FROM Post p WHERE p.isHidden = false AND (p.creator = :currentUser OR p.creator IN " +
                        "(SELECT s.following FROM Subscription s WHERE s.follower = :currentUser))")
        Page<Post> findPostsByFollowedUsersPaged(@Param("currentUser") User currentUser, Pageable pageable);
}
