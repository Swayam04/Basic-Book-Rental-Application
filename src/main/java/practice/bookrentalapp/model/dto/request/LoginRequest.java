package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequest {
    @NotBlank
    String usernameOrEmail;
    @NotBlank
    String password;
}
