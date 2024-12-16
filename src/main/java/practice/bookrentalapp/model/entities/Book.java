package practice.bookrentalapp.model.entities;

import jakarta.persistence.*;
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
    @ElementCollection
    private Set<String> authors;
    private String publisher;
    private LocalDate publishedDate;
    private String ISBN;
    private Integer pageCount;
    private Double averageRating;
    private String language;
    @ElementCollection
    private Set<String> categories;
    private Integer totalCopies;
    private Integer copiesLent = 0;
}
