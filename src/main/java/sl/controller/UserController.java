package sl.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import sl.model.User;
import sl.service.UserService;

/**
 * <b>User controller used to handle all logic that is user related.</b>
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * This method is used to add a {@link User} to the database.
     *
     * @param user object containing user data
     * @return {@link ResponseStatus} 200 if successfully added to database
     */
    @PostMapping("/register")
    public ResponseEntity addUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.register(user));
    }

    /**
     * This method is used to authenticate {@link User} by given credentials.
     *
     * @param user containing {@link User} credentials
     * @return token used for authentication
     */
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user) {
        return ResponseEntity.ok(userService.login(user));
    }

    /**
     * This method is used to get {@link User}'s username by token.
     *
     * @param token used for authentication
     * @return {@link User}'s username
     */
    @GetMapping("/getUsername")
    public ResponseEntity<String> getUsername(@RequestParam("token") String token) {
        return ResponseEntity.ok(userService.getUsername(token));
    }
}
