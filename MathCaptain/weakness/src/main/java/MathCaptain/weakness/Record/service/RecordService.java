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
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.PointSet;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;

    /// 기록

    // 기록 시작
    public recordStartResponseDto startRecord(Users user, Long groupId) {

        // 사용자 식별
        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndJoinGroup_Id(user, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

        // 기존 미완료된 기록이 있는지 확인
        checkRemainRecord(user, relation.getJoinGroup());

        ActivityRecord record = ActivityRecord.builder()
                .user(user)
                .group(relation.getJoinGroup())
                .startTime(LocalDateTime.now())
                .build();

        Long recordId = recordRepository.save(record).getId();

        return recordStartResponseDto.builder()
                .recordId(recordId)
                .userDailyGoal(relation.getPersonalDailyGoal() * 60L)
                .remainingDailyGoal(calculateRemainingDailyGoalMinutes(relation))
                .build();
    }

    // 기록 종료
    public recordSummaryResponseDto endActivity(Long recordId, recordEndRequestDto endRequest) {

        // 진행 중인 활동 찾기
        ActivityRecord record = recordRepository.findById(recordId)
                .orElseThrow(() -> new IllegalArgumentException("현재 진행중인 인증이 존재하지 않습니다."));

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberIdAndJoinGroupId(
                record.getUser().getUserId(),
                record.getGroup().getId()
        ).orElseThrow(() -> new IllegalArgumentException("해당 그룹에 속하지 않은 사용자입니다."));

        Users user = relation.getMember();

        Group group = relation.getJoinGroup();

        // 활동 기록 업데이트
        updateRecord(record, relation, endRequest);

        // 일간 목표 시간 달성시
        if (isDailyGoalAchieved(relation)) {
            // 일간 목표 달성 성공 업데이트 (기록)
            record.updateDailyGoalAchieved(true);

            // 그룹의 요일 목표 수행 카운트 증가 (그룹)
            increaseWeeklyGoalAchieveCount(relation.getJoinGroup().getId(), record.getDayOfWeek());

            // 개인 포인트 획득 (일간 목표 달성)
            user.addPoint(PointSet.DailyGoalAchieve);

            // 그룹 포인트 획득 (일간 목표 달성)
            group.addPoint(PointSet.DailyGoalAchieve);

            // 주간 목표 + 1 (일간 목표 충족시 업데이트)
            relation.updatePersonalWeeklyGoalAchieved(relation.getPersonalWeeklyGoalAchieve() + 1);
        }

        if (isWeeklyGoalAchieved(relation)) {
            record.updateWeeklyGoalAchieved(true);

            relation.updateWeeklyGoalAchieveStreak(relation.getWeeklyGoalAchieveStreak() + 1);

            // 개인 포인트 획득 (주간 목표 달성, 주간 목표 달성 보상 포인트 * (연속 달성 주차 수 + 설정한 주간 목표 일))
            user.addPoint(PointSet.WeeklyGoalAchieveBase * (relation.getWeeklyGoalAchieveStreak() + relation.getPersonalWeeklyGoal()));

            // 그룹 포인트 획득 (주간 목표 달성)
            group.addPoint(PointSet.WeeklyGoalAchieveBase * (relation.getWeeklyGoalAchieveStreak() + relation.getPersonalWeeklyGoal()));
        }

        recordRepository.save(record);

        return buildRecordSummaryResponseDto(record, calculateRemainingDailyGoalMinutes(relation), calculateRemainingWeeklyGoal(relation));
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
    public void increaseWeeklyGoalAchieveCount(Long groupId, DayOfWeek dayOfWeek) {
        groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."))
                .increaseWeeklyGoalAchieve(dayOfWeek);
    }

    //
    public Map<DayOfWeek, Boolean> getWeeklyGoalStatus(Users user, Group group, LocalDateTime weekStart) {
        // 주의 시작과 끝 계산 (월요일 ~ 다음 주 월요일)
        LocalDateTime startOfWeek = weekStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);

        // 활동 기록이 있는 요일 조회
        List<DayOfWeek> activeDays = recordRepository.findDaysWithActivity(user, group, startOfWeek, endOfWeek);

        // 결과 맵 초기화 (모든 요일 false로 초기화)
        Map<DayOfWeek, Boolean> weeklyGoalStatus = new EnumMap<>(DayOfWeek.class);
        for (DayOfWeek day : DayOfWeek.values()) {
            weeklyGoalStatus.put(day, false);
        }

        // 활동 기록이 있는 요일을 true로 설정
        for (DayOfWeek activeDay : activeDays) {
            weeklyGoalStatus.put(activeDay, true);
        }

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
}
