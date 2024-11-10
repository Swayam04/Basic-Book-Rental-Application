package practice.bookrentalapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import practice.bookrentalapp.model.dto.request.LoginRequest;
import practice.bookrentalapp.model.dto.request.RegisterRequest;
import practice.bookrentalapp.model.dto.response.AuthResponse;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.model.enums.Role;
import practice.bookrentalapp.repositories.UserRepository;
import practice.bookrentalapp.security.jwt.JwtTokenProvider;
import practice.bookrentalapp.utils.EntityDtoMapper;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EntityDtoMapper entityDtoMapper;

    public AuthResponse register(RegisterRequest registerRequest) throws BadRequestException {
        log.debug("Starting registration for username: {}", registerRequest.getUsername());

        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already in use");
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Account with provided email already exists");
        }
        User user = createUser(registerRequest);
        User savedUser = userRepository.save(user);
        log.debug("User saved successfully: {}", savedUser.getUsername());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            registerRequest.getUsername(),
                            registerRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.debug("User authenticated successfully after registration");
            String jwt = jwtTokenProvider.generateToken(savedUser);
            log.debug("JWT token generated for new user");

            return new AuthResponse(jwt, "Bearer", entityDtoMapper.mapToUserDto(savedUser));
        } catch (AuthenticationException e) {
            log.error("Authentication failed after registration", e);
            throw new BadRequestException("Authentication failed after registration");
        }
    }

    public AuthResponse login(LoginRequest request) throws BadRequestException {
        log.debug("Attempting login for user: {}", request.getUsernameOrEmail());

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            log.debug("User authenticated successfully: {}", user.getUsername());

            String jwt = jwtTokenProvider.generateToken(user);
            log.debug("JWT token generated successfully");

            String usernameFromToken = jwtTokenProvider.getUsernameFromToken(jwt);
            log.debug("Username extracted from generated token: {}", usernameFromToken);

            return new AuthResponse(jwt, "Bearer", entityDtoMapper.mapToUserDto(user));

        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", request.getUsernameOrEmail(), e);
            throw new BadRequestException("Invalid username/email or password");
        }
    }

    private User createUser(RegisterRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setName(request.getName());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.ROLE_USER);
        log.debug("Creating new user: {}", user.getUsername());
        return user;
    }

}
