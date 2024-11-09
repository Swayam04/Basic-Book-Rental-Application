package practice.bookrentalapp.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;

@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String type;
    private UserDto user;
}
