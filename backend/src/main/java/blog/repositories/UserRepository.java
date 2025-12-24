package blog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import blog.entity.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    User findByUsernameOrEmail(@Param("identifier") String identifier);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // for user management in admin dashboard
    Page<User> findAllByOrderByIdDesc(Pageable pageable);

    @Query("""
            SELECT u FROM User u WHERE
                LOWER(u.username) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(u.firstName) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(u.lastName) LIKE LOWER(CONCAT('%', :query, '%')) OR
                LOWER(CONCAT(u.firstName, ' ', u.lastName)) LIKE LOWER(CONCAT('%', :query, '%'))
                """)
    public List<User> searchUsers(@Param("query") String query);
}
