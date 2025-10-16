package blog.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import blog.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    //find all posts ordered by created Date
    @Query("SELECT p FROM Post p ORDER BY p.createdAt DESC")
    List<Post> findAllPosts(); 
}

