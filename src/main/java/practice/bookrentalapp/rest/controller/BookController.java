package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.bookrentalapp.model.dto.request.BookSearchRequest;
import practice.bookrentalapp.service.GoogleBooksApiService;

import java.util.List;


@RestController
@RequestMapping("api/books")
public class BookController {
    private final GoogleBooksApiService googleBooksApiService;

    @Autowired
    public BookController(GoogleBooksApiService googleBooksApiService) {
        this.googleBooksApiService = googleBooksApiService;
    }

    @PostMapping
    public ResponseEntity<List<Long>> addBookByAuthorName(@Valid @RequestBody BookSearchRequest bookSearchRequest) {
        List<Long> savedBookIds = googleBooksApiService.fetchAndSaveBooks(bookSearchRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookIds);
    }

}
