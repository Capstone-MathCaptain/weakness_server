package MathCaptain.weakness.Ranking.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Ranking.dto.response.GroupRankingResponseDto;
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

    public Page<GroupRankingResponseDto> getGroupRankings(Pageable pageable) {
        // Repository 호출하여 페이징된 데이터 가져오기
        Page<Group> groupPage = groupRepository.findAllOrderByGroupPoint(pageable);

        // 순위 계산 및 DTO 변환
        AtomicInteger rankCounter = new AtomicInteger((int) pageable.getOffset() + 1);

        return groupPage.map(group -> GroupRankingResponseDto.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .groupPoint(group.getGroupPoint())
                .ranking(rankCounter.getAndIncrement())
                .build());
    }

}