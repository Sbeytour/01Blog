package blog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.entity.Media;

@Repository
public interface MediaRepository extends JpaRepository<Media, Long> {
    // find media of a post
    @Query("SELECT m FROM Media m WHERE m.post.id = :postId")
    List<Media> findAllMedia(@Param("postId") Long postId);
}
