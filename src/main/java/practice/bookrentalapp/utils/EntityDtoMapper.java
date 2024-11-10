package practice.bookrentalapp.utils;

import org.springframework.stereotype.Component;
import practice.bookrentalapp.model.dto.entityDtos.BookDto;
import practice.bookrentalapp.model.dto.entityDtos.RentalDto;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.entities.Book;
import practice.bookrentalapp.model.entities.Rental;
import practice.bookrentalapp.model.entities.User;

import java.util.ArrayList;

@Component
public class EntityDtoMapper {

    public UserDto mapToUserDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setName(user.getName());
        return userDto;
    }

    public RentalDto mapToRentalDto(Rental rental) {
        RentalDto dto = new RentalDto();
        dto.setId(rental.getId());
        dto.setBookTitles(new ArrayList<>(rental.getBooks().stream().map(Book::getTitle).toList()));
        dto.setUser(mapToUserDto(rental.getUser()));
        dto.setStatus(rental.getStatus());
        dto.setDueDate(rental.getDueDate());
        dto.setIssueDate(rental.getIssueDate());
        return dto;
    }

    public BookDto mapToBookDto(Book book) {
        BookDto dto = new BookDto();
        dto.setAuthors(new ArrayList<>(book.getAuthors()));
        dto.setTitle(book.getTitle());
        dto.setCategories(new ArrayList<>(book.getCategories()));
        dto.setIsbn(book.getISBN());
        dto.setPublisher(book.getPublisher());
        dto.setPageCount(book.getPageCount());
        dto.setAverageRating(book.getAverageRating());
        dto.setAvailableCopies(book.getCopies());
        return dto;
    }

}
