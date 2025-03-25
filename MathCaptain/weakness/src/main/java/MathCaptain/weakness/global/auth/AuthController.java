package MathCaptain.weakness.global.auth;

import MathCaptain.weakness.domain.User.repository.UserRepository;
import MathCaptain.weakness.global.auth.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
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
