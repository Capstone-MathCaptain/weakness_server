package MathCaptain.weakness.domain.User.controller;

import MathCaptain.weakness.domain.User.dto.request.*;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.User.dto.response.ChangePwdDto;
import MathCaptain.weakness.domain.User.dto.response.UserCardResponseDto;
import MathCaptain.weakness.domain.User.dto.response.UserResponseDto;
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
    public ApiResponse<UserResponseDto> userInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    // CREATE
    @PostMapping("/user/signup")
    public ApiResponse<UserResponseDto> saveUser(@Valid @RequestBody SaveUserRequestDto user) {
        return userService.saveUser(user);
    }

    // UPDATE
    @PutMapping("/user")
    public ApiResponse<UserResponseDto> updateUser(@Valid @LoginUser Users loginUser, @RequestBody UpdateUserRequestDto updateUser) {
        return userService.updateUser(loginUser, updateUser);
    }

    // DELETE
    @DeleteMapping("/user")
    public ApiResponse<?> deleteUser(@LoginUser Users loginUser, @RequestBody UserDeleteRequestDto userDeleteRequestDto) {
        return userService.deleteUser(loginUser, userDeleteRequestDto);
    }

    @PostMapping("/user/find/email")
    public ApiResponse<?> findEmail(@Valid @RequestBody FindEmailRequestDto findEmailRequestDto) {
        return userService.findEmail(findEmailRequestDto);
    }

    @PostMapping("/user/find/password")
    public ApiResponse<?> FindPwd(@Valid @RequestBody FindPwdRequestDto findPwdRequestDto) {
        userService.findPwdRequest(findPwdRequestDto);
        return ApiResponse.ok("이메일로 비밀번호 재설정 링크를 보냈습니다.");
    }

    @PostMapping("/user/reset/password")
    public ApiResponse<?> ChangedPwd(@ModelAttribute ChangePwdDto changePwdDto) {
        userService.changePwd(changePwdDto);
        return ApiResponse.ok("비밀번호가 변경되었습니다.");
    }

    @GetMapping("/user/mypage")
    public ApiResponse<UserCardResponseDto> getUserCard(@LoginUser Users loginUser) {
        return userService.getUserCard(loginUser);
    }

}
