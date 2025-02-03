package MathCaptain.weakness.Record.service;

import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Record.domain.ActivityRecord;
import MathCaptain.weakness.Record.dto.response.recordSummaryResponseDto;
import MathCaptain.weakness.Record.repository.RecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class RecordService {

    private final RecordRepository recordRepository;
    private final RelationRepository relationRepository;

    // 기록 시작
    public Long startRecord(Long userId, Long groupId) {

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberIdAndJoinGroupId(userId, groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));

        // 기존 미완료된 활동이 있는지 확인
        Optional<ActivityRecord> existingActivity = recordRepository.findByUserAndGroupAndEndTimeIsNull(
                relation.getMember(), relation.getJoinGroup());

        if (existingActivity.isPresent()) {
            recordRepository.delete(existingActivity.get());
            log.info("기존 미완료된 활동 삭제");
        }

        ActivityRecord record = ActivityRecord.builder()
                .user(relation.getMember())
                .group(relation.getJoinGroup())
                .startTime(LocalDateTime.now())
                .build();

        return recordRepository.save(record).getId();
    }

    // 기록 종료
    public recordSummaryResponseDto endActivity(Long activityId) {
        // 진행 중인 활동 찾기 (존재하지 않으면 예외 발생)
        ActivityRecord activityRecord = recordRepository.findById(activityId)
                .orElseThrow(() -> new IllegalArgumentException("현재 진행중인 인증이 존재하지 않습니다."));

        // 종료 시간 업데이트
        activityRecord.updateEndTime(LocalDateTime.now());
        activityRecord.calculateDuration(); // 활동 시간 계산 (분 단위)

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberIdAndJoinGroupId(
                activityRecord.getUser().getUserId(),
                activityRecord.getGroup().getId()
        ).orElseThrow(() -> new IllegalArgumentException("해당 그룹에 속하지 않은 사용자입니다."));

        Long remainingDailyGoalMinutes = checkDailyGoalAchieved(activityRecord, relation);
        int remainingWeeklyGoal = checkWeeklyGoalAchieved(activityRecord, relation);

        recordRepository.save(activityRecord);

        return buildRecordSummaryResponseDto(activityRecord, remainingDailyGoalMinutes, remainingWeeklyGoal);
    }

    //==비지니스 로직==//

    // 일간 목표 달성 여부 확인 및 업데이트
    private Long checkDailyGoalAchieved(ActivityRecord activityRecord, RelationBetweenUserAndGroup relation) {

        long remainingDailyGoalMinutes = 0L;

        // 일간 달성 시간 업데이트 (분)
        Long dailyAchieved = activityRecord.getDurationInMinutes() + relation.getPersonalDailyGoalAchieve();
        relation.updatePersonalDailyGoalAchieved(dailyAchieved);

        // 일간 목표 시간 달성시
        if (activityRecord.getDurationInMinutes() >= relation.getPersonalDailyGoal()) {
            activityRecord.updateDailyGoalAchieved(true);

            // 주간 목표 + 1 (일간 목표 충족시 업데이트)
            int weeklyAchieved = relation.getPersonalWeeklyGoalAchieve() + 1;
            relation.updatePersonalWeeklyGoalAchieved(weeklyAchieved);

        // 일간 목표 시간 미달성시 남은 시간을 반환
        } else {
            remainingDailyGoalMinutes = relation.getPersonalDailyGoal() - activityRecord.getDurationInMinutes();
            activityRecord.updateDailyGoalAchieved(false);
        }

        return remainingDailyGoalMinutes;
    }

    private int checkWeeklyGoalAchieved(ActivityRecord activityRecord, RelationBetweenUserAndGroup relation) {

        int remainingWeeklyGoal = 0;

        // 주간 목표 달성 여부 확인 & 업데이트
        // 주간 목표 충족시
        if (activityRecord.getDurationInMinutes() >= relation.getPersonalWeeklyGoal()) {
            activityRecord.updateWeeklyGoalAchieved(true);
        }
        // 주간 목표 미달성시 남은 일 수를 알려줌
        else {

            remainingWeeklyGoal = relation.getPersonalWeeklyGoal() - relation.getPersonalWeeklyGoalAchieve();
            activityRecord.updateWeeklyGoalAchieved(false);
        }

        return remainingWeeklyGoal;
    }

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
