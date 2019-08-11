package sl.service;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import sl.exception.EmptyUserDataException;
import sl.exception.IncorrectPasswordException;
import sl.exception.UserNotFoundException;
import sl.model.User;
import sl.repository.UserRepository;

import javax.transaction.Transactional;
import java.util.Optional;
import java.util.UUID;

/**
 * <b>This service declares all manipulations that can be done on a {@link User}.</b>
 */
@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * This method is used for {@link User} registration.
     *
     * @param userData contains the data from the register form
     */
    public ResponseEntity register(User userData) {
        if (StringUtils.isEmpty(userData.getUsername()) || StringUtils.isEmpty(userData.getPassword()))
            throw new EmptyUserDataException("Empty username or password!");

        User user = new User(userData.getUsername(), passwordEncoder.encode(userData.getPassword()));
        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

    /**
     * This method is used for logging-in an existing {@link User}.
     *
     * @param userData containing the data from the form
     * @return generated token for authentication
     */
    public String login(User userData) {
        if (StringUtils.isEmpty(userData.getUsername()) || StringUtils.isEmpty(userData.getPassword())) {
            throw new EmptyUserDataException("Empty username or password!");
        }

        User user = userRepository.findByUsername(userData.getUsername())
                .orElseThrow(() -> new UserNotFoundException("Username not found!"));

        return Optional.of(user)
                .filter(u -> passwordEncoder.matches(userData.getPassword(), u.getPassword()))
                .map(u -> {
                    String token = UUID.randomUUID().toString();
                    userRepository.setToken(u.getUsername(), token);
                    return token;
                })
                .orElseThrow(() -> new IncorrectPasswordException("Wrong password!"));
    }

    /**
     * This method is used for checking {@link User}'s token upon login.
     *
     * @param userToken generated token
     * @return found user
     */
    public Optional<User> verifyToken(String userToken) {
        return Optional.ofNullable(userToken)
                .flatMap(token -> userRepository.findByToken(token));
    }


    /**
     * This method is used to return logged-in {@link User}'s username.
     *
     * @param token generated token
     * @return username of the logged-in {@link User}
     */
    public String getUsername(String token) {
        return userRepository.findByToken(token)
                .map(User::getUsername)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }
}
