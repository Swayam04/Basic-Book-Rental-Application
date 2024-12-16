package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import practice.bookrentalapp.model.enums.RentalStatus;
import practice.bookrentalapp.model.enums.Role;

import java.time.LocalDate;

@Data
public class UserFilter {
    private String name;
    private String username;
    @Email
    private String email;
    private String rentedBookTitle;
    private Long rentedBookId;
    private Long rentalId;
    private Role role;
    private RentalStatus rentalStatus;
    private LocalDate rentedAfter;
    private LocalDate rentedBefore;
    private Integer rentalsGreaterThan;

    @Min(value = 0)
    private Integer page = 0;
    @Min(value = 1, message = "Page cannot be empty")
    @Max(value = 40, message = "Maximum 40 items per page allowed")
    private Integer size = 10;
    @Pattern(regexp = "name|email|username",
            message = "orderBy must be one of: name, email, username")
    private String orderBy = "username";
    @Pattern(regexp = "asc|desc", message = "dir must be either 'asc' or 'desc'")
    private String dir = "asc";
}
