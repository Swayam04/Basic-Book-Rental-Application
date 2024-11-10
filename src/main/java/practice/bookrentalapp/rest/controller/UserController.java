package practice.bookrentalapp.rest.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.repositories.UserRepository;
import practice.bookrentalapp.utils.EntityDtoMapper;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserRepository userRepository;
    private final EntityDtoMapper entityDtoMapper;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null
            && authentication.isAuthenticated()
            && authentication.getPrincipal() instanceof User user) {
            return ResponseEntity.ok(entityDtoMapper.mapToUserDto(user));
        }
        throw new RuntimeException("No authenticated users");
    }
    
}
