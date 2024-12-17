package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import practice.bookrentalapp.model.enums.RentalStatus;

import java.time.LocalDate;

@Data
public class RentalFilter {
    private RentalStatus status = RentalStatus.ACTIVE;
    private LocalDate issueDateBefore;
    private LocalDate issueDateAfter;
    private LocalDate dueDateBefore;
    private LocalDate dueDateAfter;
    private String bookTitle;
    private Long userId;

    @Min(value = 0)
    private Integer page = 0;
    @Min(value = 1, message = "Page cannot be empty")
    @Max(value = 40, message = "Maximum 40 items per page allowed")
    private Integer size = 10;
    @Pattern(regexp = "issueDate|dueDate",
            message = "orderBy must be one of: issueDate, dueDate")
    private String orderBy = "username";
    @Pattern(regexp = "asc|desc", message = "dir must be either 'asc' or 'desc'")
    private String dir = "asc";
}
