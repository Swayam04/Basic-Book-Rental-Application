package practice.bookrentalapp.security;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import practice.bookrentalapp.model.entities.User;
import practice.bookrentalapp.repositories.UserRepository;

@Service
@RequiredArgsConstructor(onConstructor_ = {@Autowired})
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsernameOrEmail(username, username);
        if (user == null) {
            logger.error("User: {} not found", username);
            throw new UsernameNotFoundException("User not found with username or email: " + username);
        }
        return user;
    }
}
