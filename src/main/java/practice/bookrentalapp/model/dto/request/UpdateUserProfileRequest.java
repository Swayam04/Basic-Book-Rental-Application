package practice.bookrentalapp.model.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Data;
import practice.bookrentalapp.validators.annotations.AtLeastOneNotNull;

@Data
@AtLeastOneNotNull(fieldNames = {"newName", "newEmail", "newUsername"}, message = "Please provide at least one field to update")
public class UpdateUserProfileRequest {
    @Size(min = 2, max = 50, message = "Name must be between 2 and 50 characters")
    private String newName;
    @Email(message = "Please provide a valid email address")
    private String newEmail;
    @Size(min = 5, max = 20, message = "Username must be between 5 and 20 characters")
    private String newUsername;
}
