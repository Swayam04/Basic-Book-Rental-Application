package practice.bookrentalapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.bookrentalapp.entities.Book;
import practice.bookrentalapp.repositories.BookRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public List<Book> saveBooksBatch(List<Book> books) {
        Set<String> titles = books.stream().map(Book::getTitle).collect(Collectors.toSet());
        Set<String> isbn = books.stream()
                .map(Book::getISBN)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> existingTitles = bookRepository.findExistingTitlesByTitleOrIsbn(titles, isbn);

        List<Book> newBooks = books.stream()
                .filter(book -> !existingTitles.contains(book.getTitle()))
                .collect(Collectors.toList());

        return bookRepository.saveAll(newBooks);
    }
}
