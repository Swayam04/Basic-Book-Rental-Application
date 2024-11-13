package practice.bookrentalapp.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import practice.bookrentalapp.model.dto.entityDtos.RentalDto;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.dto.request.UpdateUserProfileRequest;
import practice.bookrentalapp.model.dto.response.UpdateUserProfileResponse;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.model.enums.Role;
import practice.bookrentalapp.repositories.RentalRepository;
import practice.bookrentalapp.repositories.UserRepository;
import practice.bookrentalapp.utils.EntityDtoMapper;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final EntityDtoMapper entityDtoMapper;

    public User promoteToAdmin(Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setRole(Role.ROLE_ADMIN);
        log.info("User {} promoted to admin.", user.getUsername());
        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(entityDtoMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getCurrentUser(long userId) {
        Optional<User> user = userRepository.findById(userId);
        return user.map(entityDtoMapper::mapToUserDto).orElse(null);
    }

    public UpdateUserProfileResponse updateUser(long userId, UpdateUserProfileRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        UpdateUserProfileResponse response = new UpdateUserProfileResponse();

        updateField(request.getNewName(), user::setName, response::setUpdatedName, user::getUsername);
        updateField(request.getNewEmail(), user::setEmail, response::setUpdatedEmail, user::getEmail);
        updateField(request.getNewUsername(), user::setUsername, response::setUpdatedUsername, user::getUsername);

        userRepository.save(user);
        return response;
    }

    private <T> void updateField(T newValue, Consumer<T> setter, Consumer<T> responseSetter, Supplier<T> getter) {
        if (newValue != null) {
            setter.accept(newValue);
            responseSetter.accept(getter.get());
        }
    }


    public void removeUser(Long id) {
        userRepository.deleteById(id);
    }

    public List<RentalDto> getUserRentals(Long userId) {
        return rentalRepository.findByUserId(userId).stream()
                .map(entityDtoMapper::mapToRentalDto)
                .toList();
    }

}
