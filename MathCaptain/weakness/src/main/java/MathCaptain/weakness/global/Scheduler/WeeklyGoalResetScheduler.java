package MathCaptain.weakness.global.Scheduler;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.repository.GroupRepository;
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
public class WeeklyGoalResetScheduler {

    private final GroupRepository groupRepository;

    private static final int BATCH_SIZE = 100; // 100개씩 처리

    // 매주 월요일 00시 실행 (주간 목표 초기화)
    @Scheduled(cron = "0 0 0 * * MON") // 매주 월요일 00:00에 실행
    public void resetWeeklyGoals() {

        log.info("주간 목표 초기화 시작");
        int pageNumber = 0; // 페이징 시작 페이지 번호

        Page<Group> page;
        do {
            // 페이징 요청
            page = groupRepository.findAll(PageRequest.of(pageNumber, BATCH_SIZE));

            // 데이터 처리
            for (Group group : page.getContent()) {
                group.resetWeeklyGoalAchieve();
            }

            // 변경 사항 저장
            groupRepository.saveAll(page.getContent());
            pageNumber++; // 다음 페이지로 이동
        } while (!page.isLast()); // 마지막 페이지인지 확인

        log.info("주간 목표 초기화 완료");
    }
}
