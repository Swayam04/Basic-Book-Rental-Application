package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import practice.bookrentalapp.validators.annotations.AtLeastOneNotNull;

@Data
@AtLeastOneNotNull(fieldNames = {"newName", "newEmail", "newUsername"})
public class UpdateUserProfileRequest {
    @Size(min = 2, max = 50)
    private String newName;
    @Email
    private String newEmail;
    @Size(min = 5, max = 20)
    private String newUsername;
}
