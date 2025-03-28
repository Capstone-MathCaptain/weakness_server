package MathCaptain.weakness.domain.User.controller;

import MathCaptain.weakness.domain.User.dto.request.*;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.User.dto.response.ChangePwdDto;
import MathCaptain.weakness.domain.User.dto.response.UserCardResponse;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import MathCaptain.weakness.domain.User.service.UserService;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // READ
    @GetMapping("/user/{userId}")
    public ApiResponse<UserResponse> userInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    // CREATE
    @PostMapping("/user/signup")
    public ApiResponse<UserResponse> saveUser(@Valid @RequestBody SaveUserRequest user) {
        return userService.saveUser(user);
    }

    // UPDATE
    @PutMapping("/user")
    public ApiResponse<UserResponse> updateUser(@Valid @LoginUser Users loginUser, @RequestBody UpdateUserRequest updateUser) {
        return userService.updateUser(loginUser, updateUser);
    }

    // DELETE
    @DeleteMapping("/user")
    public ApiResponse<?> deleteUser(@LoginUser Users loginUser, @RequestBody UserDeleteRequest userDeleteRequest) {
        return userService.deleteUser(loginUser, userDeleteRequest);
    }

    @PostMapping("/user/find/email")
    public ApiResponse<?> findEmail(@Valid @RequestBody FindEmailRequest findEmailRequest) {
        return userService.findEmail(findEmailRequest);
    }

    @PostMapping("/user/find/password")
    public ApiResponse<?> FindPwd(@Valid @RequestBody FindPwdRequest findPwdRequest) {
        userService.findPwdRequest(findPwdRequest);
        return ApiResponse.ok("이메일로 비밀번호 재설정 링크를 보냈습니다.");
    }

    @PostMapping("/user/reset/password")
    public ApiResponse<?> ChangedPwd(@ModelAttribute ChangePwdDto changePwdDto) {
        userService.changePwd(changePwdDto);
        return ApiResponse.ok("비밀번호가 변경되었습니다.");
    }

    @GetMapping("/user/mypage")
    public ApiResponse<UserCardResponse> getUserCard(@LoginUser Users loginUser) {
        return userService.getUserCard(loginUser);
    }

}
