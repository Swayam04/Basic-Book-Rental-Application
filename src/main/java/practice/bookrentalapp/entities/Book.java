package practice.bookrentalapp.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "books")
public class Book extends BaseEntity {
    private String title;
    private Set<String> authors;
    private String publisher;
    private LocalDate publishedDate;
    private String ISBN_10;
    private int pageCount;
    private double averageRating;
    private String description;
    private String language;
    private Set<String> categories;
}
