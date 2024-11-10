package practice.bookrentalapp.model.dto.entityDtos;

import lombok.Data;

import java.util.List;

@Data
public class BookDto {
    private String title;
    private List<String> authors;
    private String isbn;
    private String publisher;
    private Integer pageCount;
    private Double averageRating;
    private List<String> categories;
    private Integer availableCopies;
}
