package blog.repositories;

import org.springframework.stereotype.Repository;

import blog.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    User findByUsernameOrEmail(@Param("identifier") String identifier);

    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = ?1 OR u.email = ?1")
    boolean existsByUsernameOrEmail(String identifier);
}
