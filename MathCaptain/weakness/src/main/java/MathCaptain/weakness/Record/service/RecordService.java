package MathCaptain.weakness.Record.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Record.domain.ActivityRecord;
import MathCaptain.weakness.Record.dto.request.recordEndRequestDto;
import MathCaptain.weakness.Record.dto.response.recordStartResponseDto;
import MathCaptain.weakness.Record.dto.response.recordSummaryResponseDto;
import MathCaptain.weakness.Record.repository.RecordRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.PointSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    public static final Long DAILY_GOAL_ACHIEVE = PointSet.DailyGoalAchieve;
    public static final Long WEEKLY_GOAL_ACHIEVE_BASE = PointSet.WeeklyGoalAchieveBase;

    private final RecordRepository recordRepository;
    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;

    /// 기록

    // 기록 시작
    public recordStartResponseDto startRecord(Users user, Long groupId) {

        // 사용자 식별
        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndGroup_Id(user, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

        // 기존 미완료된 기록이 있는지 확인
        checkRemainRecord(user, relation.getGroup());

        ActivityRecord record = buildRecord(user, relation);

        Long recordId = recordRepository.save(record).getId();

        return buildRecordStartResponse(recordId, relation);
    }

    // 기록 종료
    public recordSummaryResponseDto endActivity(Long recordId, recordEndRequestDto endRequest) {

        // 진행 중인 활동 찾기
        ActivityRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("현재 진행중인 인증이 존재하지 않습니다."));

        // 사용자 식별
        Users user = record.getUser();
        Group group = record.getGroup();

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndGroup(user,group)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹에 속하지 않은 사용자입니다."));

        // 활동 기록 업데이트
        updateRecord(record, relation, endRequest);

        // 일간 목표 시간 달성시
        if (isDailyGoalAchieved(relation)) {
            // 일간 목표 달성 성공 업데이트 (기록)
            record.updateDailyGoalAchieved(true);

            // 그룹의 요일 목표 수행 카운트 증가 (그룹)
            increaseWeeklyGoalAchieveCount(relation, record);

            // 일간 목표 달성 포인트 획득
            addPoint(user, group, DAILY_GOAL_ACHIEVE);

            // 주간 목표 + 1 (일간 목표 충족시 업데이트)
            relation.updatePersonalWeeklyGoalAchieved(relation.getPersonalWeeklyGoalAchieve() + 1);
        }

        // 주간 목표 달성시
        if (isWeeklyGoalAchieved(relation)) {
            // 주간 목표 달성 성공 업데이트 (기록)
            record.updateWeeklyGoalAchieved(true);

            // 주간 목표 달성 스트릭 업데이트 (관계)
            relation.updateWeeklyGoalAchieveStreak(relation.getWeeklyGoalAchieveStreak() + 1);

            // 주간 목표 달성 포인트 계산
            Long weeklyAchievePoint = WEEKLY_GOAL_ACHIEVE_BASE * (relation.getWeeklyGoalAchieveStreak() + relation.getPersonalWeeklyGoal());

            // 주간 목표 달성 포인트 획득
            addPoint(user, group, weeklyAchievePoint);
        }

        recordRepository.save(record);

        return buildRecordSummaryResponseDto(record, calculateRemainingDailyGoalMinutes(relation), calculateRemainingWeeklyGoal(relation));
    }

    // 주간 목표 달성 여부 조회
    public Map<DayOfWeek, Boolean> getWeeklyGoalStatus(Users user, Group group, LocalDateTime weekStart) {
        // 주의 시작과 끝 계산 (월요일 ~ 다음 주 월요일)
        LocalDateTime startOfWeek = calculateStartOfWeek(weekStart);
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

        // 활동 기록이 있는 요일 조회
        List<DayOfWeek> activeDays = recordRepository.findDaysWithActivity(user, group, startOfWeek, endOfWeek);

        // 요일별 활동 기록 여부 맵 생성
        return createWeeklyGoalStatusMap(activeDays);
    }

    /// 로직

    // 일간 목표 달성 여부 확인
    private boolean isDailyGoalAchieved(RelationBetweenUserAndGroup relation) {
        return relation.getPersonalDailyGoalAchieve() >= relation.getPersonalDailyGoal();
    }

    private boolean isWeeklyGoalAchieved(RelationBetweenUserAndGroup relation) {
        return relation.getPersonalWeeklyGoalAchieve() >= relation.getPersonalWeeklyGoal();
    }

    // 그룹의 요일 목표 수행 카운트 증가
    public void increaseWeeklyGoalAchieveCount(RelationBetweenUserAndGroup relation, ActivityRecord record) {
        // 활동 요일
        DayOfWeek dayOfWeek = record.getDayOfWeek();
        Group group = relation.getGroup();
        group.increaseWeeklyGoalAchieveMap(dayOfWeek);
    }

    private void addPoint(Users user, Group group, Long point) {
        // 개인 포인트 획득 (일간 목표 달성)
        user.addPoint(point);

        // 그룹 포인트 획득 (일간 목표 달성)
        group.addPoint(point);
    }

    private LocalDateTime calculateStartOfWeek(LocalDateTime weekStart) {
        return weekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
    }

    private Map<DayOfWeek, Boolean> createWeeklyGoalStatusMap(List<DayOfWeek> activeDays) {
        // 모든 요일을 false로 초기화
        Map<DayOfWeek, Boolean> weeklyGoalStatus = Arrays.stream(DayOfWeek.values())
                .collect(Collectors.toMap(day -> day, day -> false, (a, b) -> b, () -> new EnumMap<>(DayOfWeek.class)));

        // 활동 기록이 있는 요일을 true로 설정
        activeDays.forEach(day -> weeklyGoalStatus.put(day, true));

        return weeklyGoalStatus;
    }

    private void checkRemainRecord(Users user, Group group) {
        // 기존 미완료된 활동이 있는지 확인
        Optional<ActivityRecord> existingActivity = recordRepository.findByUserAndGroupAndEndTimeIsNull(
                user, group);

        if (existingActivity.isPresent()) {
            recordRepository.delete(existingActivity.get());
            log.info("기존 미완료된 활동 삭제");
        }
    }

    private void updateRecord(ActivityRecord record, RelationBetweenUserAndGroup relation, recordEndRequestDto endRequest) {
        record.updateEndTime(LocalDateTime.now());
        record.updateDurationInMinutes(endRequest.getActivityTime());
        record.updateDayOfWeek(LocalDateTime.now().getDayOfWeek());

        // 일간 달성 시간 업데이트 (분)
        relation.updatePersonalDailyGoalAchieved(
                Optional.ofNullable(relation.getPersonalDailyGoalAchieve())
                        .orElse(0L) + record.getDurationInMinutes());
    }

    private Long calculateRemainingDailyGoalMinutes(RelationBetweenUserAndGroup relation) {
        return Math.max(relation.getPersonalDailyGoal() * 60L - relation.getPersonalDailyGoalAchieve(), 0L);
    }

    private int calculateRemainingWeeklyGoal(RelationBetweenUserAndGroup relation) {
        return Math.max(relation.getPersonalWeeklyGoal() - relation.getPersonalWeeklyGoalAchieve(), 0);
    }

    /// 빌더

    private recordSummaryResponseDto buildRecordSummaryResponseDto(ActivityRecord activityRecord, Long remainingDailyGoalMinutes, int remainingWeeklyGoal) {
        return recordSummaryResponseDto.builder()
                .userName(activityRecord.getUser().getName())
                .groupName(activityRecord.getGroup().getName())
                .durationInMinutes(activityRecord.getDurationInMinutes())
                .dailyGoalAchieved(activityRecord.isDailyGoalAchieved())
                .weeklyGoalAchieved(activityRecord.isWeeklyGoalAchieved())
                .remainingDailyGoalMinutes(remainingDailyGoalMinutes)
                .remainingWeeklyGoalDays(remainingWeeklyGoal)
                .build();
    }

    private recordStartResponseDto buildRecordStartResponse(Long recordId, RelationBetweenUserAndGroup relation) {
        return recordStartResponseDto.builder()
                .recordId(recordId)
                .userDailyGoal(relation.getPersonalDailyGoal() * 60L)
                .remainingDailyGoal(calculateRemainingDailyGoalMinutes(relation))
                .build();
    }

    private static ActivityRecord buildRecord(Users user, RelationBetweenUserAndGroup relation) {
        return ActivityRecord.builder()
                .user(user)
                .group(relation.getGroup())
                .startTime(LocalDateTime.now())
                .build();
    }
}
