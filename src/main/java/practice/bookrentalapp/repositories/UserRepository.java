package practice.bookrentalapp.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.bookrentalapp.model.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsernameOrEmail(String username, String email);
    boolean existsByUsername(String username);

    boolean existsByEmail(@NotBlank @Email String email);
}
