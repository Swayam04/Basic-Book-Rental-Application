package practice.bookrentalapp.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import practice.bookrentalapp.model.entities.Book;

import java.util.List;
import java.util.Set;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    @Query("SELECT DISTINCT b.title FROM Book b WHERE b.title IN :titles OR b.ISBN IN :isbns")
    Set<String> findExistingTitlesByTitleOrIsbn(@Param("titles") Set<String> titles, @Param("isbns") Set<String> isbns);

    @Query("SELECT DISTINCT b FROM Book b " +
            "LEFT JOIN b.authors a ON :author IS NOT NULL " +
            "LEFT JOIN b.categories c ON :categories IS NOT NULL " +
            "WHERE (:author IS NULL OR LOWER(a) = LOWER(:author)) " +
            "AND (:categories IS NULL OR c IN :categories) " +
            "AND (:title IS NULL OR LOWER(b.title) = LOWER(:title)) " +
            "AND (:isbn IS NULL OR b.ISBN = :isbn) " +
            "AND (:available IS NULL OR b.copies > 0) " +
            "AND (:minRating IS NULL OR b.averageRating >= :minRating)"
    )
    Page<Book> fetchByFilters(
      @Param("title") String title,
      @Param("author") String author,
      @Param("isbn") String isbn,
      @Param("available") Boolean available,
      @Param("genres") List<String> categories,
      @Param("minRating") Double minRating,
      Pageable pageable
    );
}
