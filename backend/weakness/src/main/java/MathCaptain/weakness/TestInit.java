package MathCaptain.weakness;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.global.Security.jwt.JwtTestUtil;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class TestInit {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;


    @PostConstruct
    @Transactional
    public void init() {

        String email1 = "test1@example.com";
        String refreshToken1 = JwtTestUtil.createTestJwt(email1);

        String email2 = "test2@example.com";
        String refreshToken2 = JwtTestUtil.createTestJwt(email2);

        // 테스트 유저 생성
        Users users1 = Users.builder()
                .userId(1L)
                .email(email1)
                .password(passwordEncoder.encode("password1"))
                .name("tester01")
                .nickname("tester01")
                .phoneNumber("01012345678")
                .refreshToken(refreshToken1)
                .build();

        Users users2 = Users.builder()
                .userId(2L)
                .email(email2)
                .password(passwordEncoder.encode("password2"))
                .name("tester02")
                .nickname("tester02")
                .phoneNumber("01056781234")
                .refreshToken(refreshToken2)
                .build();


        userRepository.save(users1);
        userRepository.save(users2);
        log.info("테스트 유저 생성 완료");

        Users leader = userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

//         테스트 그룹 생성
        Group group = Group.builder()
                .leader(leader)
                .name("testGroup")
                .category(CategoryStatus.STUDY)
                .min_daily_hours(2)
                .min_weekly_days(3)
                .group_point(0L)
                .hashtags(null)
                .disturb_mode(false)
                .group_image_url("test")
                .build();

        groupRepository.save(group);
        log.info("테스트 그룹 생성 완료");
    }
}
