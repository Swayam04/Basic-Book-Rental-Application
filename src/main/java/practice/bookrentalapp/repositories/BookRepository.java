package practice.bookrentalapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.bookrentalapp.entities.Book;

import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b.title FROM Book b WHERE b.title IN :titles OR b.ISBN IN :isbns")
    Set<String> findExistingTitlesByTitleOrIsbn(@Param("titles") Set<String> titles, @Param("isbns") Set<String> isbns);
}
