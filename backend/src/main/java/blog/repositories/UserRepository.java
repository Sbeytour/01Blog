package blog.repositories;

import org.springframework.stereotype.Repository;

import blog.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameOrEmail(String username);

    boolean existsByUsernameOrEmail(String email);
}
