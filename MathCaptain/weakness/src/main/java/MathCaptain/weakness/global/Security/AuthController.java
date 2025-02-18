package MathCaptain.weakness.global.Security;

import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;

    @PostMapping("/refresh-token")
    public void refreshToken(HttpServletRequest request) {
    }
}
