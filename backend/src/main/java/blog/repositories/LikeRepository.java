package blog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import blog.entity.Like;

public interface LikeRepository extends JpaRepository<Like, Long> {

}
