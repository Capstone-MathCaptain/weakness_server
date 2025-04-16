package MathCaptain.weakness;

import MathCaptain.weakness.domain.Group.dto.request.GroupCreateRequest;
import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import MathCaptain.weakness.domain.Record.entity.UserLog.ExerciseInfo;
import MathCaptain.weakness.domain.Record.entity.UserLog.FitnessDetail;
import MathCaptain.weakness.domain.Record.entity.UserLog.RunningDetail;
import MathCaptain.weakness.domain.Record.entity.UserLog.StudyDetail;
import MathCaptain.weakness.domain.Record.repository.record.RecordRepository;
import MathCaptain.weakness.domain.Record.repository.userLog.FitnessLogRepository;
import MathCaptain.weakness.domain.Record.repository.userLog.RunningLogRepository;
import MathCaptain.weakness.domain.Record.repository.userLog.StudyLogRepository;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateRecruitmentRequest;
import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.domain.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.domain.User.dto.request.SaveUserRequest;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private final FitnessLogRepository fitnessLogRepository;
    private final RunningLogRepository runningLogRepository;
    private final StudyLogRepository studyLogRepository;


    @PostConstruct
    @Transactional
    public void init() {

        String email1 = "dlwpdyd201@naver.com";

        String email2 = "test2@example.com";

        String email3 = "test@test.com";

        SaveUserRequest saveUserRequest1 = SaveUserRequest.of(
                email1, passwordEncoder.encode("password1"),
                "tester01", "tester01", "01012345678"
        );

        SaveUserRequest saveUserRequest2 = SaveUserRequest.of(
                email2, passwordEncoder.encode("password2"),
                "tester02", "tester02", "01056781234"
        );

        SaveUserRequest saveUserRequest3 = SaveUserRequest.of(
                email3, passwordEncoder.encode("test"),
                "tester", "tester", "01011112111"
        );

        /// 테스트 유저 생성
        Users users1 = Users.of(saveUserRequest1);

        Users users2 = Users.of(saveUserRequest2);

        Users users3 = Users.of(saveUserRequest3);

        userRepository.save(users1);
        userRepository.save(users2);
        userRepository.save(users3);

        for (int i = 4; i <= 12; i++) {
            SaveUserRequest saveUserRequest = SaveUserRequest.of(
                    "test" + i + "@test.com", passwordEncoder.encode("test"),
                    "tester" + i, "tester" + i, "0101111111" + i % 10
            );

            Users user = Users.of(saveUserRequest);
            userRepository.save(user);
        }

        log.info("======== 👤테스트 유저 데이터 생성 완료 =========");

        Users leader = userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        /// 테스트 그룹 생성
        GroupCreateRequest groupCreateRequest1 = GroupCreateRequest.of(users1.getUserId(), "testGroup1",
                CategoryStatus.STUDY, 2, 3, 0L, null, "test1", 3, 4);

        GroupCreateRequest groupCreateRequest2 = GroupCreateRequest.of(users1.getUserId(), "testGroup2",
                CategoryStatus.FITNESS, 2, 3, 0L, null, "test2", 3, 4);

        GroupCreateRequest groupCreateRequest3 = GroupCreateRequest.of(users1.getUserId(), "testGroup3",
                CategoryStatus.RUNNING, 2, 3, 0L, null, "test3", 3, 4);

        Group group1 = Group.of(groupCreateRequest1);
        Group group2 = Group.of(groupCreateRequest2);
        Group group3 = Group.of(groupCreateRequest3);

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

        log.info("======== 👥 테스트 그룹 데이터 생성 완료 =========");

        RelationBetweenUserAndGroup join1 = RelationBetweenUserAndGroup.of(users1, group1, groupCreateRequest1);
        RelationBetweenUserAndGroup join2 = RelationBetweenUserAndGroup.of(users2, group2, groupCreateRequest2);
        RelationBetweenUserAndGroup join3 = RelationBetweenUserAndGroup.of(users3, group3, groupCreateRequest3);

        relationRepository.save(join1);
        relationRepository.save(join2);
        relationRepository.save(join3);

        for (int i = 4; i <= 12; i++) {
            Users member = userRepository.findByUserId((long) i)
                    .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));
            RelationBetweenUserAndGroup join = RelationBetweenUserAndGroup.of(member, group3, 3, 5);
            relationRepository.save(join);
        }

        log.info("======== 👥 테스트 관계 데이터 생성 완료 =========");

        CreateRecruitmentRequest createRecruitmentRequest = CreateRecruitmentRequest.of(group1.getId(), "testRecruitment", "testContent");
        Recruitment recruitment = Recruitment.of(users1, group1, createRecruitmentRequest);
        recruitmentRepository.save(recruitment);

        log.info("======== 🔖테스트 모집글 생성 완료 =========");

        /// 테스트 댓글 생성
        Comment comment = Comment.of(recruitment, users1, "testComment");
        commentRepository.save(comment);

        log.info("======== 💬테스트 댓글 생성 완료 =========");

        // ActivityRecord 생성 (currentProgress 설정: 10 이하로 조정)

        // 이번 주의 시작과 끝 시간 계산
        LocalDateTime startOfWeek = LocalDateTime.now().with(java.time.temporal.TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));

        // User ID: 4 -> currentProgress: 5
        createActivityRecords(userRepository.findByUserId(4L)
                        .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group3, startOfWeek, 5, CategoryStatus.RUNNING, chestList);

        // User ID: 5 -> currentProgress: 8
        createActivityRecords(userRepository.findByUserId(5L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group3, startOfWeek, 8, CategoryStatus.RUNNING, chestList);

        // User ID: 6 -> currentProgress: 10
        createActivityRecords(userRepository.findByUserId(6L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group3, startOfWeek, 10, CategoryStatus.RUNNING, chestList);

        // User ID: 7 -> currentProgress: 7
        createActivityRecords(userRepository.findByUserId(7L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group3, startOfWeek, 7, CategoryStatus.RUNNING, chestList);

        // User ID: 8 -> currentProgress: 6
        createActivityRecords(userRepository.findByUserId(8L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group3, startOfWeek, 6, CategoryStatus.RUNNING, chestList);

        createActivityRecords(userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group1, startOfWeek, 4, CategoryStatus.FITNESS, chestList);
        createActivityRecords(userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group1, startOfWeek, 3, CategoryStatus.FITNESS, backList);
        createActivityRecords(userRepository.findByUserId(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group1, startOfWeek, 3, CategoryStatus.FITNESS, legList);

        createActivityRecords(userRepository.findByUserId(2L).
                orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다.")),
                group2, startOfWeek, 3, CategoryStatus.STUDY, chestList);

        log.info("======== 🏃‍♂️테스트 목표 기록 생성 완료 =========");
    }

    // Helper 메서드: ActivityRecord 생성 (이번 주 내에서만 생성되도록 수정)
    private void createActivityRecords(
            Users user, Group group, LocalDateTime startOfWeek,
            int recordCount, CategoryStatus category,
            List<ExerciseInfo> exerciseInfoList
    ) {
        for (int i = 0; i < recordCount; i++) {
            // 시작 시간은 주어진 startOfWeek에서 i일을 더한 값
            LocalDateTime startTime = startOfWeek.plusDays(i % 7);

            // 종료 시간은 시작 시간에서 1시간을 더한 값으로 설정 (예시)
            LocalDateTime endTime = startTime.plusHours(1);

            // 활동 시간은 60분으로 설정 (예시)
            Long activityTime = 60L;

            // 요일은 시작 시간의 DayOfWeek를 사용
            DayOfWeek dayOfWeek = startTime.getDayOfWeek();

            // ActivityRecord 객체 생성
            ActivityRecord record = ActivityRecord.of(user, group, startTime, endTime, activityTime, dayOfWeek);

            // 저장소에 저장
            recordRepository.save(record);

            if (category == CategoryStatus.FITNESS) {
                fitnessLogCreate(record);
            } else if (category == CategoryStatus.RUNNING) {
                runningLogCreate(record);
            } else if (category == CategoryStatus.STUDY) {
                studyLogCreate(record);
            }
        }
    }

    ExerciseInfo chest1 = ExerciseInfo.of("덤벨 프레스", 70, 10, 5);
    ExerciseInfo chest2 = ExerciseInfo.of("벤치 프레스", 80, 10, 5);
    ExerciseInfo chest3 = ExerciseInfo.of("밀리터리 프레스", 60, 10, 5);
    ExerciseInfo chest4 = ExerciseInfo.of("디클라인 벤치 프레스", 70, 10, 5);
    List<ExerciseInfo> chestList = List.of(chest1, chest2, chest3, chest4);

    ExerciseInfo back1 = ExerciseInfo.of("데드 리프트", 120, 10, 5);
    ExerciseInfo back2 = ExerciseInfo.of("랫풀다운", 80, 10, 5);
    ExerciseInfo back3 = ExerciseInfo.of("시티드 로우", 70, 10, 5);
    List<ExerciseInfo> backList = List.of(back1, back2, back3);

    ExerciseInfo leg1 = ExerciseInfo.of("스쿼트", 100, 10, 5);
    ExerciseInfo leg2 = ExerciseInfo.of("레그 프레스", 150, 10, 5);
    ExerciseInfo leg3 = ExerciseInfo.of("레그 익스텐션", 80, 10, 5);
    ExerciseInfo leg4 = ExerciseInfo.of("레그 컬", 70, 10, 5);
    List<ExerciseInfo> legList = List.of(leg1, leg2, leg3, leg4);



    private void studyLogCreate(ActivityRecord record) {
        StudyDetail log = StudyDetail.of(record, "수학", 60L, "지수함수의 미분");
        studyLogRepository.save(log);
    }

    private void runningLogCreate(ActivityRecord record) {
        RunningDetail log = RunningDetail.of(record, 5L, "원인재에서 동춘역까지");
        runningLogRepository.save(log);
    }

    private void fitnessLogCreate(ActivityRecord record) {
         FitnessDetail log = FitnessDetail.of(record, chestList);
         fitnessLogRepository.save(log);
    }
}
//