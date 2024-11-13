package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.dto.request.UpdateUserProfileRequest;
import practice.bookrentalapp.model.dto.response.UpdateUserProfileResponse;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.service.UserService;

import java.util.Optional;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        Optional<User> loggedInUser = Optional.ofNullable(isAuthenticated());
        if (loggedInUser.isPresent()) {
            return ResponseEntity.ok(userService.getCurrentUser(loggedInUser.get().getId()));
        }
        throw new RuntimeException("No authenticated users");
    }

    @PutMapping("/profile")
    public ResponseEntity<UpdateUserProfileResponse> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest updateRequest) {
        Optional<User> loggedInUser = Optional.ofNullable(isAuthenticated());
        if (loggedInUser.isPresent()) {
            return ResponseEntity.ok(userService.updateUser(loggedInUser.get().getId(), updateRequest));
        }
        throw new RuntimeException("No authenticated users");
    }

    private User isAuthenticated() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication != null
                && authentication.isAuthenticated()
                && authentication.getPrincipal() instanceof User user) {
            return user;
        }
        return null;
    }
}
