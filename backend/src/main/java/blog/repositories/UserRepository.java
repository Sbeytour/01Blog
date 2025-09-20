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

    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
