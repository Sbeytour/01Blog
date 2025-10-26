package blog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.entity.Post;
import blog.entity.User;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    //find all posts ordered by created Date
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllPosts();

    // Find posts from users that the current user follows
    @Query("SELECT p FROM Post p WHERE p.creator IN " +
           "(SELECT s.following FROM Subscription s WHERE s.follower = :currentUser) " +
           "ORDER BY p.createdAt DESC")
    List<Post> findPostsByFollowedUsers(@Param("currentUser") User currentUser);
}

