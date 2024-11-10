package practice.bookrentalapp.service;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practice.bookrentalapp.model.dto.entityDtos.RentalDto;
import practice.bookrentalapp.model.dto.entityDtos.UserDto;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.model.enums.Role;
import practice.bookrentalapp.repositories.RentalRepository;
import practice.bookrentalapp.repositories.UserRepository;
import practice.bookrentalapp.utils.EntityDtoMapper;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserService {
    private final UserRepository userRepository;
    private final RentalRepository rentalRepository;
    private final EntityDtoMapper entityDtoMapper;
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    public User promoteToAdmin(String username) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }
        user.setRole(Role.ROLE_ADMIN);
        logger.info("User {} promoted to admin.", username);
        return userRepository.save(user);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(entityDtoMapper::mapToUserDto)
                .collect(Collectors.toList());
    }

    public UserDto getCurrentUser(String username) {
        User user = userRepository.findByUsername(username);
        return entityDtoMapper.mapToUserDto(user);
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
