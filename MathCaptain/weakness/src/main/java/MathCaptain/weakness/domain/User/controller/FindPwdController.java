package MathCaptain.weakness.domain.User.controller;

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
