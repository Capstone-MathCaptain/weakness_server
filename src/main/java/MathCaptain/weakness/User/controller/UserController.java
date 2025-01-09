package MathCaptain.weakness.User.controller;

import MathCaptain.weakness.User.dto.updateUserDto;
import MathCaptain.weakness.User.dto.userDto;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/signup")
    public String saveUser(@Valid @RequestBody userDto user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "error";
        }
        return Long.toString(userService.saveUser(user));
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable long userId, @RequestBody String password) {
        return Long.toString(userService.deleteUser(userId, password));
    }

    @GetMapping("/user/{userId}")
    public userDto userInfo(@PathVariable long userId) {
        return userService.getUserInfo(userId);
    }

    @PutMapping("/user/{userId}")
    public Users updateUser(@RequestBody updateUserDto updateUser) {
        return userService.updateUser(updateUser);
    }

}
