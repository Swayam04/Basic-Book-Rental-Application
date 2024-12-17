package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.bookrentalapp.model.dto.entityDtos.BookDto;
import practice.bookrentalapp.model.dto.entityDtos.RentalDto;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.dto.request.BookSearchRequest;
import practice.bookrentalapp.model.dto.request.RentalFilter;
import practice.bookrentalapp.model.dto.request.UpdateBookRequest;
import practice.bookrentalapp.model.dto.request.UserFilter;
import practice.bookrentalapp.service.BookService;
import practice.bookrentalapp.service.GoogleBooksApiService;
import practice.bookrentalapp.service.RentalService;
import practice.bookrentalapp.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@Slf4j
public class AdminController {
    private final GoogleBooksApiService googleBooksApiService;
    private final BookService bookService;
    private final UserService userService;
    private final RentalService rentalService;

    @Autowired
    public AdminController(GoogleBooksApiService googleBooksApiService, BookService bookService, UserService userService, RentalService rentalService) {
        this.googleBooksApiService = googleBooksApiService;
        this.bookService = bookService;
        this.userService = userService;
        this.rentalService = rentalService;
    }

    @PostMapping("/books")
    public ResponseEntity<List<Long>> addBookByAuthorName(@Valid @RequestBody BookSearchRequest bookSearchRequest) {
        List<Long> savedBookIds = googleBooksApiService.fetchAndSaveBooks(bookSearchRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedBookIds);
    }

    @PatchMapping("/books/{id}")
    public ResponseEntity<BookDto> updateBookDetails(@PathVariable Long id, @Valid @RequestBody UpdateBookRequest updateBookRequest) {
        return ResponseEntity.ok(bookService.updateBook(id, updateBookRequest));
    }

    @DeleteMapping("/books/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/users")
    public ResponseEntity<Page<UserDto>> getAllUsers(@Valid UserFilter userFilter) {
        return ResponseEntity.ok(userService.findUsersWithFilter(userFilter));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PatchMapping("/users/{id}")
    public ResponseEntity<Void> changeUserRole(@PathVariable Long id) {
        userService.promoteToAdmin(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/rentals")
    public ResponseEntity<Page<RentalDto>> getAllRentals(@Valid RentalFilter rentalFilter) {
        return ResponseEntity.ok(rentalService.getRentals(rentalFilter));
    }
}
