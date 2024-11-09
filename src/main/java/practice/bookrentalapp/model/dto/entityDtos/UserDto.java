package practice.bookrentalapp.model.dto.entityDtos;

import lombok.Data;

@Data
public class UserDto {
    private Long id;
    private String name;
    private String username;
    private String email;
}
