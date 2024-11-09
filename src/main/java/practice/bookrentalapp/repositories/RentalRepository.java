package practice.bookrentalapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.bookrentalapp.model.entities.Rental;

@Repository
public interface RentalRepository extends JpaRepository<Rental, Long> {
}
