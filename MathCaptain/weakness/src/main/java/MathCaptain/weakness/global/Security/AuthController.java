package MathCaptain.weakness.global.Security;

import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @GetMapping("/refresh-token")
    public void refreshToken() {
    }
}
