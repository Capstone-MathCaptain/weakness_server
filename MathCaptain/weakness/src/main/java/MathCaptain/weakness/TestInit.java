package MathCaptain.weakness;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
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
    private final RelationRepository relationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CommentRepository commentRepository;


    @PostConstruct
    @Transactional
    public void init() {

        String email1 = "dlwpdyd201@naver.com";

        String email2 = "test2@example.com";

        String email3 = "test@test.com";

        /// 테스트 유저 생성
        Users users1 = Users.builder()
                .userId(1L)
                .email(email1)
                .password(passwordEncoder.encode("password1"))
                .name("tester01")
                .nickname("tester01")
                .phoneNumber("01012345678")
//                .refreshToken(refreshToken1)
                .build();

        Users users2 = Users.builder()
                .userId(2L)
                .email(email2)
                .password(passwordEncoder.encode("password2"))
                .name("tester02")
                .nickname("tester02")
                .phoneNumber("01056781234")
//                .refreshToken(refreshToken2)
                .build();

        Users users3 = Users.builder()
                .userId(3L)
                .email(email3)
                .password(passwordEncoder.encode("test"))
                .name("tester")
                .nickname("tester")
                .phoneNumber("01011111111")
//                .refreshToken(refreshToken3)
                .build();


        userRepository.save(users1);
        userRepository.save(users2);
        userRepository.save(users3);
        log.info("테스트 유저 생성 완료");

        Users leader = userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        /// 테스트 그룹 생성
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

        /// 테스트 모집글 생성
        Recruitment recruitment = Recruitment.builder()
                .postId(1L)
                .author(users1)
                .recruitGroup(group)
                .category(CategoryStatus.STUDY)
                .title("testRecruitment")
                .content("testContent")
                .recruitmentStatus(RecruitmentStatus.RECRUITING)
                .build();

        recruitmentRepository.save(recruitment);
        log.info("테스트 모집글 생성 완료");

        /// 테스트 댓글 생성
        Comment comment = Comment.builder()
                .commentId(1L)
                .author(users1)
                .post(recruitment)
                .content("testComment")
                .build();

        commentRepository.save(comment);
        log.info("테스트 댓글 생성 완료");
    }
}
