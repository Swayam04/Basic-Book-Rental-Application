package practice.bookrentalapp.model.dto.entityDtos;

import lombok.Data;
import practice.bookrentalapp.model.enums.RentalStatus;

import java.time.LocalDate;
import java.util.List;

@Data
public class RentalDto {
    private Long id;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private RentalStatus status;
    private List<String> bookTitles;
    private String username;
}
