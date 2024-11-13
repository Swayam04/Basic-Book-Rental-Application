package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    @Size(min = 2, max = 100)
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$\n",
            message = "Password must be 8-20 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    private String password;
    @NotBlank
    @Size(min = 5, max = 20)
    private String username;
}
