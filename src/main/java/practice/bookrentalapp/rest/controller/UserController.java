package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import practice.bookrentalapp.exceptions.UserNotAuthenticatedException;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.dto.request.UpdateUserProfileRequest;
import practice.bookrentalapp.model.dto.response.UpdateUserProfileResponse;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.service.UserService;

import static practice.bookrentalapp.utils.LoginChecker.isAuthenticated;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserController {
    private final UserService userService;

    @GetMapping("/profile")
    public ResponseEntity<UserDto> getCurrentUserProfile() {
        User loggedInUser = isAuthenticated();
        if (loggedInUser == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        return ResponseEntity.ok(userService.getCurrentUser(loggedInUser.getId()));
    }

    @PatchMapping("/profile")
    public ResponseEntity<UpdateUserProfileResponse> updateUserProfile(@Valid @RequestBody UpdateUserProfileRequest updateRequest) {
        User loggedInUser = isAuthenticated();
        if (loggedInUser == null) {
            throw new UserNotAuthenticatedException("User is not authenticated");
        }
        return ResponseEntity.ok(userService.updateUser(loggedInUser.getId(), updateRequest));
    }
}
