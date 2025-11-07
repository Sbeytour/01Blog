package blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.entity.Like;
@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

    @Query("SELECT COUNT(l) FROM Like l WHERE l.post.id = :postId")
    Long countByPostId(@Param("postId") Long postId);

    // check if current user likes a post
    @Query("SELECT COUNT(l) > 0 FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    boolean existByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

    // Find like by user and post (for unliking)
    @Query("SELECT l FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
    Like findByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);
}
