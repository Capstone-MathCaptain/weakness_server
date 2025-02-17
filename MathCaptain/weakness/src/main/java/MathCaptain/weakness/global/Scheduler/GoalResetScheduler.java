package MathCaptain.weakness.global.Scheduler;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.global.PointSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@EnableScheduling
@RequiredArgsConstructor
public class GoalResetScheduler {

    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;

    private static final int BATCH_SIZE = 100; // 100개씩 처리

    // 매일 00시 실행 (일간 목표 초기화)
    @Scheduled(cron = "0 0 0 * * ?") // 매일 00:00에 실행
    public void resetDailyGoals() {

        log.info("일간 목표 초기화 시작");
        int pageNumber = 0; // 페이징 시작 페이지 번호

        Page<RelationBetweenUserAndGroup> page;
        do {
            // 페이징 요청
            page = relationRepository.findAll(PageRequest.of(pageNumber, BATCH_SIZE));

            // 데이터 처리
            for (RelationBetweenUserAndGroup relation : page.getContent()) {
                relation.resetPersonalDailyGoalAchieve();
            }

            // 변경 사항 저장
            relationRepository.saveAll(page.getContent());
            pageNumber++; // 다음 페이지로 이동
        } while (!page.isLast()); // 마지막 페이지인지 확인

        log.info("일간 목표 초기화 완료");
    }

    // 매주 월요일 00시 실행 (주간 목표 초기화)
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00:00에 실행
    public void resetWeeklyGoals() {

        log.info("주간 목표 초기화 시작");
        int groupPageNumber = 0; // 페이징 시작 페이지 번호

        Page<Group> groupsPage;
        do {
            // 페이징 요청
            groupsPage = groupRepository.findAll(PageRequest.of(groupPageNumber, BATCH_SIZE));

            // 데이터 처리
            for (Group group : groupsPage.getContent()) {
                if (isAllMembersAchievedWeeklyGoal(group.getId())) {
                    group.addPoint(PointSet.AllGroupMembersAchieveBase * relationRepository.countByJoinGroup_Id(group.getId()));
                }
            }
            // 변경 사항 저장
            groupRepository.saveAll(groupsPage.getContent());
            groupPageNumber++; // 다음 페이지로 이동
        } while (!groupsPage.isLast()); // 마지막 페이지인지 확인

        int relationPageNumber = 0;

        Page<RelationBetweenUserAndGroup> relationPage;
        do {
            // 페이징 요청
            relationPage = relationRepository.findAll(PageRequest.of(relationPageNumber, BATCH_SIZE));

            // 데이터 처리
            for (RelationBetweenUserAndGroup relation : relationPage.getContent()) {

                // 주간 목표 미달성시 연속 달성 횟수 초기화 & 패널티 부여
                if (!relation.isWeeklyGoalAchieved()) {
                    relation.resetWeeklyGoalAchieveStreak();
                    relation.getMember().subtractPoint(PointSet.WeeklyGoalFailPenaltyBase * calculateFailedDays(relation));
                    relation.getJoinGroup().subtractPoint(PointSet.WeeklyGoalFailPenaltyBase * calculateFailedDays(relation));
                }

                relation.resetPersonalWeeklyGoalAchieve();
            }

            // 변경 사항 저장
            relationRepository.saveAll(relationPage.getContent());
            relationPageNumber++; // 다음 페이지로 이동
        } while (!relationPage.isLast()); // 마지막 페이지인지 확인

        log.info("주간 목표 초기화 완료");
    }

    private int calculateFailedDays(RelationBetweenUserAndGroup relation) {
        return Math.max(relation.getPersonalWeeklyGoal() - relation.getPersonalWeeklyGoalAchieve(), 0);
    }

    private boolean isAllMembersAchievedWeeklyGoal(Long groupId) {
        return relationRepository.allMembersAchievedWeeklyGoal(groupId);
    }
}
