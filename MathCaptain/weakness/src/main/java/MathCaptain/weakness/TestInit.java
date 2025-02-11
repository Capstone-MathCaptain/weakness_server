package MathCaptain.weakness;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Group.service.GroupService;
import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.enums.RecruitmentStatus;
import MathCaptain.weakness.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
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
    private final RecruitmentRepository recruitmentRepository;
    private final CommentRepository commentRepository;
    private final GroupService groupService;
    private final RelationRepository relationRepository;


    @PostConstruct
    @Transactional
    public void init() {

        String email1 = "dlwpdyd201@naver.com";
        String refreshToken1 = JwtTestUtil.createTestJwt(email1);

        String email2 = "test2@example.com";
        String refreshToken2 = JwtTestUtil.createTestJwt(email2);

        String email3 = "test";
        String refreshToken3 = JwtTestUtil.createTestJwt(email3);

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

        Users users3 = Users.builder()
                .userId(3L)
                .email(email3)
                .password(passwordEncoder.encode("test"))
                .name("tester")
                .nickname("tester")
                .phoneNumber("01011111111")
                .refreshToken(refreshToken3)
                .build();


        userRepository.save(users1);
        userRepository.save(users2);
        userRepository.save(users3);
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

        RelationBetweenUserAndGroup join = RelationBetweenUserAndGroup.builder()
                .member(users1)
                .groupRole(GroupRole.LEADER)
                .joinGroup(group)
                .personalDailyGoal(2)
                .personalWeeklyGoal(3)
                .build();

        relationRepository.save(join);

//        Recruitment recruitment = Recruitment.builder()
//                .postId(1L)
//                .author(leader)
//                .recruitGroup(group)
//                .title("테스트용 모집글")
//                .content("테스트용 모집글입니다.")
//                .category(CategoryStatus.STUDY)
//                .build();
//
//        recruitmentRepository.save(recruitment);
//        log.info("테스트 모집글 생성 완료");
//
//        Comment comment = Comment.builder()
//                .author(leader)
//                .post(recruitment)
//                .content("테스트용 댓글입니다.")
//                .build();
//
//        commentRepository.save(comment);
//        log.info("테스트 댓글 생성 완료");
//
//        log.info("테스트 데이터 생성 완료");

    }
}
