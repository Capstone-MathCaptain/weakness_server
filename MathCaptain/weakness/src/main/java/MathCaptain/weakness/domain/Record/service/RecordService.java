package MathCaptain.weakness.domain.Record.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Record.dto.request.*;
import MathCaptain.weakness.domain.Record.dto.response.FitnessLogResponse;
import MathCaptain.weakness.domain.Record.dto.response.RunningLogResponse;
import MathCaptain.weakness.domain.Record.dto.response.StudyLogResponse;
import MathCaptain.weakness.domain.Record.entity.ActivityRecord;
import MathCaptain.weakness.domain.Record.dto.response.RecordSummaryResponse;
import MathCaptain.weakness.domain.Record.entity.UserLog.FitnessDetail;
import MathCaptain.weakness.domain.Record.entity.UserLog.RunningDetail;
import MathCaptain.weakness.domain.Record.entity.UserLog.StudyDetail;
import MathCaptain.weakness.domain.Record.repository.record.RecordRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.PointSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

import static MathCaptain.weakness.domain.Group.enums.CategoryStatus.*;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    public static final Long DAILY_GOAL_ACHIEVE = PointSet.DailyGoalAchieve;
    public static final Long WEEKLY_GOAL_ACHIEVE_BASE = PointSet.WeeklyGoalAchieveBase;

    private final RecordRepository recordRepository;
    private final RelationRepository relationRepository;
    private final ActivityDetailService activityDetailService;

    /// 기록

    // 기록 저장
    @Transactional
    public RecordSummaryResponse endActivity(Users user, Long groupId, ActivityLogEnrollRequest logRequest, CategoryStatus activityType) {
        // 활동 기록 저장
        RelationBetweenUserAndGroup relation = findRelationByMemberAndGroup(user, groupId);

        ActivityRecord record = ActivityRecord.of(relation, logRequest);
        updateGoalAchieve(relation, record);
         recordRepository.save(record);

        // 활동 로그 저장 및 응답 생성
        return createRecordSummaryResponse(activityType, logRequest, record, relation);
    }

    private RecordSummaryResponse createRecordSummaryResponse(
            CategoryStatus activityType,
            ActivityLogEnrollRequest logRequest,
            ActivityRecord record,
            RelationBetweenUserAndGroup relation) {

        FitnessLogResponse fitnessLogResponse = null;
        RunningLogResponse runningLogResponse = null;
        StudyLogResponse studyLogResponse = null;

        switch (activityType) {
            case FITNESS:
                log.info("피트니스 로그 저장 시작");
                fitnessLogResponse = activityDetailService.enrollFitnessLog(record, (FitnessLogEnrollRequest) logRequest);
                break;
            case RUNNING:
                log.info("러닝 로그 저장 시작");
                runningLogResponse = activityDetailService.enrollRunningLog(record, (RunningLogEnrollRequest) logRequest);
                break;
            case STUDY:
                log.info("스터디 로그 저장 시작");
                studyLogResponse = activityDetailService.enrollStudyLog(record, (StudyLogEnrollRequest) logRequest);
                break;
            default:
                throw new IllegalArgumentException("지원되지 않는 인증 타입입니다: " + activityType);
        }

        return RecordSummaryResponse.of(record, relation, fitnessLogResponse, runningLogResponse, studyLogResponse);
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
        return relation.isDailyGoalAchieved();
    }
    private boolean isWeeklyGoalAchieved(RelationBetweenUserAndGroup relation) {
        return relation.isWeeklyGoalAchieved();
    }

    // 그룹의 요일 목표 수행 카운트 증가
    private void updateGoalAchieve(RelationBetweenUserAndGroup relation, ActivityRecord record) {

        // 일간 달성 시간 업데이트 (분)
        relation.updatePersonalDailyGoalAchieved(
                Optional.ofNullable(relation.getPersonalDailyGoalAchieve())
                        .orElse(0L) + record.getDurationInMinutes());
        // 일간 목표 시간 달성시
        if (isDailyGoalAchieved(relation)) {
            record.updateDailyGoalAchieved(true);
            relation.increaseWeeklyGroupCountOf(record.getDayOfWeek());
            addPoint(relation, DAILY_GOAL_ACHIEVE);
            relation.updatePersonalWeeklyGoalAchieved();
        }
        // 주간 목표 달성시
        if (isWeeklyGoalAchieved(relation)) {
            record.updateWeeklyGoalAchieved(true);
            relation.updateWeeklyGoalAchieveStreak();
            Long weeklyAchievePoint = WEEKLY_GOAL_ACHIEVE_BASE * relation.weeklyAchieveBase();
            addPoint(relation, weeklyAchievePoint);
        }
    }

    private void addPoint(RelationBetweenUserAndGroup relation, Long point) {
        relation.addPoint(point);
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

    private RelationBetweenUserAndGroup findRelationByMemberAndGroup(Users user, Long groupId) {
        return relationRepository.findByMemberAndGroup_Id(user, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));
    }
}
