package practice.bookrentalapp.rest.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import practice.bookrentalapp.model.dto.request.LoginRequest;
import practice.bookrentalapp.model.dto.request.RegisterRequest;
import practice.bookrentalapp.model.dto.response.AuthResponse;
import practice.bookrentalapp.service.AuthService;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) throws BadRequestException {
        return ResponseEntity.ok(authService.login(request));
    }

}
