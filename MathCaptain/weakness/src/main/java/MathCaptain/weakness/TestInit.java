package MathCaptain.weakness;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Group.enums.GroupRole;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import MathCaptain.weakness.domain.Record.repository.RecordRepository;
import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.enums.RecruitmentStatus;
import MathCaptain.weakness.domain.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.domain.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.domain.User.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.DependsOn;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;

@Slf4j
@Component
@RequiredArgsConstructor
@Transactional
@DependsOn("entityManagerFactory")
public class TestInit {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final CommentRepository commentRepository;
    private final RecordRepository recordRepository;


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
                .build();

        Users users2 = Users.builder()
                .userId(2L)
                .email(email2)
                .password(passwordEncoder.encode("password2"))
                .name("tester02")
                .nickname("tester02")
                .phoneNumber("01056781234")
                .build();

        Users users3 = Users.builder()
                .userId(3L)
                .email(email3)
                .password(passwordEncoder.encode("test"))
                .name("tester")
                .nickname("tester")
                .phoneNumber("01011111111")
                .build();

        userRepository.save(users1);
        userRepository.save(users2);
        userRepository.save(users3);

        for (int i = 4; i <= 12; i++) {
            Users users = Users.builder()
                    .userId((long) i)
                    .email("test" + i + "@test.com")
                    .password(passwordEncoder.encode("test"))
                    .name("tester" + i)
                    .nickname("tester" + i)
                    .phoneNumber("0101111111" + i % 10)
                    .build();
            userRepository.save(users);
        }

        log.info("======== 👤테스트 유저 생성 완료 =========");

        Users leader = userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        /// 테스트 그룹 생성
        Group group1 = Group.builder()
                .name("testGroup1")
                .category(CategoryStatus.STUDY)
                .minDailyHours(2)
                .minWeeklyDays(3)
                .groupPoint(0L)
                .hashtags(null)
                .groupImageUrl("test")
                .build();

        Group group2 = Group.builder()
                .name("testGroup2")
                .category(CategoryStatus.FITNESS)
                .minDailyHours(2)
                .minWeeklyDays(3)
                .groupPoint(0L)
                .hashtags(null)
                .groupImageUrl("test1")
                .build();

        Group group3 = Group.builder()
                .name("testGroup3")
                .category(CategoryStatus.READING)
                .minDailyHours(2)
                .minWeeklyDays(3)
                .groupPoint(0L)
                .hashtags(null)
                .groupImageUrl("test3")
                .build();

        groupRepository.save(group1);
        groupRepository.save(group2);
        groupRepository.save(group3);

        group3.updateWeeklyGoalAchieveMap(DayOfWeek.MONDAY, 2);
        group3.updateWeeklyGoalAchieveMap(DayOfWeek.TUESDAY, 4);
        group3.updateWeeklyGoalAchieveMap(DayOfWeek.WEDNESDAY, 6);
        group3.updateWeeklyGoalAchieveMap(DayOfWeek.THURSDAY, 8);
        group3.updateWeeklyGoalAchieveMap(DayOfWeek.FRIDAY, 10);
        group3.updateWeeklyGoalAchieveMap(DayOfWeek.SATURDAY, 1);
        group3.updateWeeklyGoalAchieveMap(DayOfWeek.SUNDAY, 0);

        groupRepository.save(group3);

        log.info("======== 👥 테스트 그룹 생성 완료 =========");

        RelationBetweenUserAndGroup join1 = RelationBetweenUserAndGroup.builder()
                .member(users1)
                .groupRole(GroupRole.LEADER)
                .group(group1)
                .personalDailyGoal(2)
                .personalWeeklyGoal(3)
                .build();

        RelationBetweenUserAndGroup join2 = RelationBetweenUserAndGroup.builder()
                .member(users2)
                .groupRole(GroupRole.LEADER)
                .group(group2)
                .personalDailyGoal(2)
                .personalWeeklyGoal(3)
                .build();

        RelationBetweenUserAndGroup join3 = RelationBetweenUserAndGroup.builder()
                .member(users3)
                .groupRole(GroupRole.LEADER)
                .group(group3)
                .personalDailyGoal(2)
                .personalWeeklyGoal(3)
                .build();

        relationRepository.save(join1);
        relationRepository.save(join2);
        relationRepository.save(join3);

        for (int i = 4; i <= 12; i++) {
            RelationBetweenUserAndGroup join = RelationBetweenUserAndGroup.builder()
                    .member(userRepository.findByUserId((long) i)
                            .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."))
                    )
                    .groupRole(GroupRole.MEMBER)
                    .group(group3)
                    .personalDailyGoal(3)
                    .personalWeeklyGoal(5)
                    .build();
            relationRepository.save(join);
        }

        /// 테스트 모집글 생성
        Recruitment recruitment = Recruitment.builder()
                .postId(1L)
                .author(users1)
                .recruitGroup(group1)
                .category(CategoryStatus.STUDY)
                .title("testRecruitment")
                .content("testContent")
                .recruitmentStatus(RecruitmentStatus.RECRUITING)
                .build();

        recruitmentRepository.save(recruitment);
        log.info("======== 🔖테스트 모집글 생성 완료 =========");

        /// 테스트 댓글 생성
        Comment comment = Comment.builder()
                .commentId(1L)
                .author(users1)
                .post(recruitment)
                .content("testComment")
                .build();

        commentRepository.save(comment);
        log.info("======== 💬테스트 댓글 생성 완료 =========");

        // ActivityRecord 생성 (currentProgress 설정: 10 이하로 조정)

        // 이번 주의 시작과 끝 시간 계산
        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

        // User ID: 4 -> currentProgress: 5
        createActivityRecords(userRepository.findByUserId(4L).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")), group3, startOfWeek, 5);

        // User ID: 5 -> currentProgress: 8
        createActivityRecords(userRepository.findByUserId(5L).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")), group3, startOfWeek, 8);

        // User ID: 6 -> currentProgress: 10
        createActivityRecords(userRepository.findByUserId(6L).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")), group3, startOfWeek, 10);

        // User ID: 7 -> currentProgress: 7
        createActivityRecords(userRepository.findByUserId(7L).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")), group3, startOfWeek, 7);

        // User ID: 8 -> currentProgress: 6
        createActivityRecords(userRepository.findByUserId(8L).orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")), group3, startOfWeek, 6);

        log.info("======== 🏃‍♂️테스트 목표 기록 생성 완료 =========");
    }

    // Helper 메서드: ActivityRecord 생성 (이번 주 내에서만 생성되도록 수정)
    private void createActivityRecords(Users user, Group group, LocalDateTime startOfWeek, int recordCount) {
        for (int i = 0; i < recordCount; i++) {
            ActivityRecord record = ActivityRecord.builder()
                    .user(user)
                    .group(group)
                    // 이번 주 내에서만 startTime 설정
                    .startTime(startOfWeek.plusDays(i % 7)) // 월요일부터 시작해서 순차적으로 날짜 설정
                    .dailyGoalAchieved(true) // 일간 목표 달성 여부 설정
                    .weeklyGoalAchieved(false) // 주간 목표는 기본값으로 false
                    .build();
            recordRepository.save(record);
        }
    }
}
