package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class RegisterRequest {
    @NotBlank
    private String name;
    @NotBlank
    @Email
    private String email;
    @NotBlank
    @Size(min = 8, max = 120)
    private String password;
    @NotBlank
    @Size(min = 5, max = 20)
    private String username;
}
