package practice.bookrentalapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.bookrentalapp.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
}
