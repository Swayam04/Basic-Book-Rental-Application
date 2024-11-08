package practice.bookrentalapp.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import practice.bookrentalapp.utils.StringSetConverter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "books")
public class Book extends BaseEntity {
    private String title;
    @Convert(converter = StringSetConverter.class)
    @Column(columnDefinition = "TEXT")
    private Set<String> authors;
    private String publisher;
    private LocalDate publishedDate;
    private String ISBN;
    private Integer pageCount;
    private Double averageRating;
    private String language;
    @Convert(converter = StringSetConverter.class)
    @Column(columnDefinition = "TEXT")
    private Set<String> categories;
    private Integer copies;
}
