package MathCaptain.weakness.domain.Ranking.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Ranking.dto.response.GroupRankingResponse;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.concurrent.atomic.AtomicInteger;

@Builder
@Service
@Transactional
@RequiredArgsConstructor
public class RankingService {

    private final GroupRepository groupRepository;

    public Page<GroupRankingResponse> getGroupRankings(Pageable pageable) {
        // Repository 호출하여 페이징된 데이터 가져오기
        Page<Group> groupPage = groupRepository.findAllOrderByGroupPoint(pageable);

        // 순위 계산 및 DTO 변환
        AtomicInteger rankCounter = new AtomicInteger((int) pageable.getOffset() + 1);

        // 그룹 순위 업데이트 및 DTO 변환
        groupPage.forEach(group -> {
            int currentRank = rankCounter.getAndIncrement();
            group.updateGroupRanking(currentRank); // 그룹 엔티티의 랭킹 필드 업데이트
        });

        // 변경 사항을 데이터베이스에 반영
        return groupPage.map(GroupRankingResponse::of);
    }
}