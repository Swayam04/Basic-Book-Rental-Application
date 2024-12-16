package practice.bookrentalapp.model.dto.request;

import lombok.Data;
import practice.bookrentalapp.validators.annotations.AtLeastOneNotNull;

import java.time.LocalDate;
import java.util.List;

@Data
@AtLeastOneNotNull(fieldNames = {"copiesToAdd, title, authors, publisher, publicationDate, categories"}, message = "Please provide at least one field to update")
public class UpdateBookRequest {
    private Integer copiesToAdd;
    private String title;
    private List<String> authors;
    private String publisher;
    private LocalDate publicationDate;
    private List<String> categories;
}
