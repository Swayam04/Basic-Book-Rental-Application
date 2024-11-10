package practice.bookrentalapp.service;

import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EntityDtoMapper entityDtoMapper;
    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    public AuthResponse register(RegisterRequest registerRequest) throws BadRequestException {
        if(userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BadRequestException("Username is already in use");
        }
        if(userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BadRequestException("Account with provided email already exists");
        }
        User user = createUser(registerRequest);
        User savedUser = userRepository.save(user);
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        registerRequest.getUsername(),
                        registerRequest.getPassword()
                )
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken(savedUser);
        return new AuthResponse(jwt, "Bearer", entityDtoMapper.mapToUserDto(savedUser));
    }

    public AuthResponse login(LoginRequest request) throws BadRequestException {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsernameOrEmail(),
                            request.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            User user = (User) authentication.getPrincipal();
            String jwt = jwtTokenProvider.generateToken(user);

            return new AuthResponse(jwt, "Bearer", entityDtoMapper.mapToUserDto(user));

        } catch (AuthenticationException e) {
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
        logger.debug("Creating new user: {}", user.getUsername());
        return user;
    }

}
