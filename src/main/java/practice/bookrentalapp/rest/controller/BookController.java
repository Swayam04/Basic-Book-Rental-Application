package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.bookrentalapp.model.dto.entityDtos.BookDto;
import practice.bookrentalapp.model.dto.request.BookFilter;
import practice.bookrentalapp.model.dto.response.PageBookResponse;
import practice.bookrentalapp.service.BookService;

@RestController
@RequestMapping("/api/books")
@Slf4j
public class BookController {
    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    @GetMapping
    public ResponseEntity<Page<PageBookResponse>> getBooks(@Valid BookFilter searchParams) {
        Page<PageBookResponse> books = bookService.getBooks(searchParams);
        return ResponseEntity.ok(books);
    }

    @GetMapping("{id}")
    public ResponseEntity<BookDto> getBookById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }
}
