package MathCaptain.weakness;

import MathCaptain.weakness.Security.jwt.JwtTestUtil;
import MathCaptain.weakness.User.Domain.Users;
import MathCaptain.weakness.User.Repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestUser {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    @Transactional
    public void init() {

        String email = "test@example.com";
        String refreshToken = JwtTestUtil.createTestJwt(email);

        Users users = Users.builder()
                .userId(1L)
                .email(email)
                .password(passwordEncoder.encode("password"))
                .name("tester")
                .nickname("tester01")
                .phoneNumber("01012345678")
                .refreshToken(refreshToken)
                .build();

        userRepository.save(users);
        log.info("테스트 유저 생성 완료");
        JwtTestUtil.createTestJwt(users.getEmail());
    }
}
