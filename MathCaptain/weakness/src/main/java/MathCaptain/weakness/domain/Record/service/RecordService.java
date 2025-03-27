package MathCaptain.weakness.domain.Record.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import MathCaptain.weakness.domain.Record.dto.request.recordEndRequestDto;
import MathCaptain.weakness.domain.Record.dto.response.recordSummaryResponseDto;
import MathCaptain.weakness.domain.Record.repository.RecordRepository;
import MathCaptain.weakness.domain.User.entity.Users;
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

    /// 기록

    // 기록 저장
    public recordSummaryResponseDto endActivity(Users user, Long groupId, recordEndRequestDto endRequest) {

        // 관계 식별
        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndGroup_Id(user, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

        ActivityRecord record = buildRecord(user, relation, endRequest);

        // 그룹 식별
        Group group = relation.getGroup();

        // 목표에 수행 결과 빈영
        updateGoalAchieve(user, relation, record, group);

        recordRepository.save(record);

        return buildRecordSummaryResponseDto(record, relation, calculateRemainingDailyGoalMinutes(relation), calculateRemainingWeeklyGoal(relation));
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

    private void updateGoalAchieve(Users user, RelationBetweenUserAndGroup relation, ActivityRecord record, Group group) {

        // 일간 달성 시간 업데이트 (분)
        relation.updatePersonalDailyGoalAchieved(
                Optional.ofNullable(relation.getPersonalDailyGoalAchieve())
                        .orElse(0L) + record.getDurationInMinutes());

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

    private Long calculateRemainingDailyGoalMinutes(RelationBetweenUserAndGroup relation) {
        return Math.max(relation.getPersonalDailyGoal() * 60L - relation.getPersonalDailyGoalAchieve(), 0L);
    }

    private int calculateRemainingWeeklyGoal(RelationBetweenUserAndGroup relation) {
        return Math.max(relation.getPersonalWeeklyGoal() - relation.getPersonalWeeklyGoalAchieve(), 0);
    }

    /// 빌더

    private recordSummaryResponseDto buildRecordSummaryResponseDto(ActivityRecord activityRecord, RelationBetweenUserAndGroup relation, Long remainingDailyGoalMinutes, int remainingWeeklyGoal) {
        return recordSummaryResponseDto.builder()
                .userName(activityRecord.getUser().getName())
                .groupName(activityRecord.getGroup().getName())
                .durationInMinutes(activityRecord.getDurationInMinutes())
                .dailyGoalAchieved(activityRecord.isDailyGoalAchieved())
                .weeklyGoalAchieved(activityRecord.isWeeklyGoalAchieved())
                .remainingDailyGoalMinutes(remainingDailyGoalMinutes)
                .remainingWeeklyGoalDays(remainingWeeklyGoal)
                .personalDailyGoal(relation.getPersonalDailyGoal() * 60L)
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }

    private static ActivityRecord buildRecord(Users user, RelationBetweenUserAndGroup relation, recordEndRequestDto endRequest) {
        return ActivityRecord.builder()
                .user(user)
                .group(relation.getGroup())
                .startTime(endRequest.getStartTime())
                .endTime(endRequest.getEndTime())
                .durationInMinutes(endRequest.getActivityTime())
                .dayOfWeek(LocalDateTime.now().getDayOfWeek())
                .build();
    }
}
