package practice.bookrentalapp.model.dto.response;

import lombok.Data;

@Data
public class UpdateUserProfileResponse {
    private String updatedName;
    private String updatedEmail;
    private String updatedUsername;
}
