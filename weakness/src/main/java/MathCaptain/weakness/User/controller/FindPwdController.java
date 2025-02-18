package MathCaptain.weakness.User.controller;

import MathCaptain.weakness.User.dto.request.FindPwdRequestDto;
import MathCaptain.weakness.User.dto.response.ChangePwdDto;
import MathCaptain.weakness.User.service.UserService;
import MathCaptain.weakness.global.Api.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class FindPwdController {

    @GetMapping("/user/reset/password")
    public String ChangePwdPage(@RequestParam String uuid, Model model) {
        model.addAttribute("UUID", uuid);
        return "User/ChangePwd";
    }

}
