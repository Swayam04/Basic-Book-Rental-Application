package practice.bookrentalapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import practice.bookrentalapp.entities.Book;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Boolean existsByTitleOrISBN(String title, String ISBN_10);
}
