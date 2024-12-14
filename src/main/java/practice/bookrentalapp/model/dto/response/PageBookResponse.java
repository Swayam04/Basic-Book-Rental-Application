package practice.bookrentalapp.model.dto.response;


import lombok.Data;

import java.util.Set;

@Data
public class PageBookResponse {
    private String title;
    private Set<String> authors;
    private String publisher;
    private Double averageRating;
    private Set<String> categories;
}
