package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

@Data
public class BookFilter {
    private String title;
    private String author;
    @Pattern(regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$", message = "Invalid ISBN format")
    private String isbn;
    private Boolean available;
    private List<String> categories;
    @DecimalMin(value = "0.0", inclusive = false, message = "Rating must be greater than 0")
    @DecimalMax(value = "5.0", message = "Rating cannot exceed 5")
    private Double ratingGreaterThan;

    @Min(0)
    private Integer page = 0;
    @Min(value = 1, message = "Page cannot be empty")
    @Max(value = 40, message = "Maximum 40 items per page allowed")
    private Integer size = 10;
    @Pattern(regexp = "title|authors|publisher|averageRating|pageCount",
            message = "orderBy must be one of: title, authors, publisher, averageRating, pageCount")
    private String orderBy = "title";
    @Pattern(regexp = "asc|desc", message = "dir must be either 'asc' or 'desc'")
    private String dir = "asc";
}
