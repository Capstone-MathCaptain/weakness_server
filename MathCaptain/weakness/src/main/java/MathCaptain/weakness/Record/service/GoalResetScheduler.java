package MathCaptain.weakness.Record.service;

import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.repository.RelationRepository;
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
        int pageNumber = 0; // 페이징 시작 페이지 번호

        Page<RelationBetweenUserAndGroup> page;
        do {
            // 페이징 요청
            page = relationRepository.findAll(PageRequest.of(pageNumber, BATCH_SIZE));

            // 데이터 처리
            for (RelationBetweenUserAndGroup relation : page.getContent()) {
                relation.resetPersonalWeeklyGoalAchieve();
            }

            // 변경 사항 저장
            relationRepository.saveAll(page.getContent());
            pageNumber++; // 다음 페이지로 이동
        } while (!page.isLast()); // 마지막 페이지인지 확인

        log.info("주간 목표 초기화 완료");
    }
}
