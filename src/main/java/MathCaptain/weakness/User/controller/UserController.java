package MathCaptain.weakness.User.controller;

import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.User.dto.request.UpdateUserRequestDto;
import MathCaptain.weakness.User.dto.request.SaveUserRequestDto;
import MathCaptain.weakness.User.service.UserService;
import MathCaptain.weakness.global.Api.ApiResponse;
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

    // READ
    @GetMapping("/user/{userId}")
    public ApiResponse<UserResponseDto> userInfo(@PathVariable long userId) {
        return userService.getUserInfo(userId);
    }

    // CREATE
    @PostMapping("/user/signup")
    public ApiResponse<UserResponseDto> saveUser(@Valid @RequestBody SaveUserRequestDto user) {
        return userService.saveUser(user);
    }

    // UPDATE
    @PutMapping("/user/{userId}")
    public ApiResponse<UserResponseDto> updateUser(@Valid @PathVariable Long userId, @RequestBody UpdateUserRequestDto updateUser) {
        return userService.updateUser(userId, updateUser);
    }

    // DELETE
    @DeleteMapping("/user/{userId}")
    public ApiResponse<?> deleteUser(@PathVariable long userId, @RequestBody String password) {
        return userService.deleteUser(userId, password);
    }

}
