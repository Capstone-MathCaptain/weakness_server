package MathCaptain.weakness.app.User;

import MathCaptain.weakness.domain.User.UserService;
import MathCaptain.weakness.domain.User.Users;
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

    @GetMapping("/signup")
    public String signUp(@ModelAttribute("User") Users user){
        return "회원가입 페이지 입니다.";
    }

    @PostMapping("/signup")
    public String saveUser(@Valid @RequestBody Users user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "valid error";
        }
        userService.saveUser(user);
        return "success";
    }

}
