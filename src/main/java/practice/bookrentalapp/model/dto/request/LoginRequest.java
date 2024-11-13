package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    String usernameOrEmail;
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,20}$\n",
            message = "Password must be 8-20 characters long and include at least one uppercase letter, one lowercase letter, one digit, and one special character."
    )
    String password;
}
