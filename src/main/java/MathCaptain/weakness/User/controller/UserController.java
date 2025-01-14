package MathCaptain.weakness.User.controller;

import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.User.dto.request.UpdateUserRequestDto;
import MathCaptain.weakness.User.dto.request.SaveUserRequestDto;
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
    public UserResponseDto saveUser(@Valid @RequestBody SaveUserRequestDto user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            throw new IllegalStateException("잘못된 입력입니다.");
        }
        return userService.saveUser(user);
    }

    @DeleteMapping("/user/{userId}")
    public String deleteUser(@PathVariable long userId, @RequestBody String password) {
        return Long.toString(userService.deleteUser(userId, password));
    }

    @GetMapping("/user/{userId}")
    public UserResponseDto userInfo(@PathVariable long userId) {
        return userService.getUserInfo(userId);
    }

    @PutMapping("/user/{userId}")
    public UserResponseDto updateUser(@PathVariable Long userId, @RequestBody UpdateUserRequestDto updateUser) {
        return userService.updateUser(userId, updateUser);
    }

}
