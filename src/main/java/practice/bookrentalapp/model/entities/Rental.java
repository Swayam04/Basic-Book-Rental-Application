package practice.bookrentalapp.model.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import practice.bookrentalapp.model.enums.RentalStatus;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Getter
@Setter
@Table(name = "rentals")
public class Rental extends BaseEntity {
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rentals_books",
            joinColumns = @JoinColumn(name = "rental_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Set<Book> books;
    private LocalDate issueDate;
    private LocalDate returnDate;
    private LocalDate dueDate;
    @Enumerated(EnumType.STRING)
    private RentalStatus status;
}
