package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.bookrentalapp.model.dto.request.BookRequest;
import practice.bookrentalapp.model.dto.request.BookSearchRequest;
import practice.bookrentalapp.model.dto.response.PageBookResponse;
import practice.bookrentalapp.model.entities.Book;
import practice.bookrentalapp.service.BookService;
import practice.bookrentalapp.service.GoogleBooksApiService;

import java.util.List;


@RestController
@RequestMapping("api/books")
public class BookController {
    private final GoogleBooksApiService googleBooksApiService;
    private final BookService bookService;

    @Autowired
    public BookController(GoogleBooksApiService googleBooksApiService, BookService bookService) {
        this.googleBooksApiService = googleBooksApiService;
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<PageBookResponse>> getBooks(@Valid BookRequest searchParams) {
        Page<PageBookResponse> books = bookService.getBooks(searchParams);
        return ResponseEntity.ok(books);
    }

    @PostMapping
    public ResponseEntity<List<Long>> addBookByAuthorName(@Valid @RequestBody BookSearchRequest bookSearchRequest) {
        List<Long> savedBookIds = googleBooksApiService.fetchAndSaveBooks(bookSearchRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookIds);
    }

}
