package practice.bookrentalapp.model.dto.request;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class CreateRentalRequest {
    @Size(min = 1, max = 10)
    @NotNull
    private List<Long> bookIds;
    @Min(1)
    @Max(90)
    private Integer rentalDuration;
}
