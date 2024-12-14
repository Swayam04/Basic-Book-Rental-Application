package practice.bookrentalapp.service;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import practice.bookrentalapp.model.dto.request.BookRequest;
import practice.bookrentalapp.model.dto.response.PageBookResponse;
import practice.bookrentalapp.model.entities.Book;
import practice.bookrentalapp.repositories.BookRepository;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BookService {
    private final BookRepository bookRepository;

    @Autowired
    public BookService(BookRepository bookRepository) {
        this.bookRepository = bookRepository;
    }

    @Transactional
    public List<Book> saveBooksBatch(List<Book> books) {
        log.info("Saving unique books from a batch of size {}", books.size());
        Set<String> titles = books.stream().map(Book::getTitle).collect(Collectors.toSet());
        Set<String> isbn = books.stream()
                .map(Book::getISBN)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());

        Set<String> existingTitles = bookRepository.findExistingTitlesByTitleOrIsbn(titles, isbn);
        List<Book> newBooks = books.stream()
                .filter(book -> !existingTitles.contains(book.getTitle()))
                .collect(Collectors.toList());
        log.info("Found {} unique books to be saved", newBooks.size());
        return bookRepository.saveAll(newBooks);
    }

    public Page<PageBookResponse> getBooks(BookRequest searchParams) {
        Pageable pageable = PageRequest.of(
                searchParams.getPage(),
                searchParams.getSize(),
                searchParams.getDir().equalsIgnoreCase("asc")
                        ? Sort.by(searchParams.getOrderBy()).ascending()
                        : Sort.by(searchParams.getOrderBy()).descending()
        );
        Page<Book> books = bookRepository.fetchByFilters(
                searchParams.getTitle(),
                searchParams.getAuthor(),
                searchParams.getIsbn(),
                searchParams.getAvailable(),
                searchParams.getCategories(),
                searchParams.getRatingGreaterThan(),
                pageable
        );
        return books.map(book -> {
            PageBookResponse bookResponse = new PageBookResponse();
            bookResponse.setTitle(book.getTitle());
            bookResponse.setAuthors(book.getAuthors());
            bookResponse.setPublisher(book.getPublisher());
            bookResponse.setCategories(book.getCategories());
            bookResponse.setAverageRating(book.getAverageRating());
            return bookResponse;
        });
    }
}
