package MathCaptain.weakness.Ranking.service;

import MathCaptain.weakness.Ranking.Ranking;
import MathCaptain.weakness.Ranking.domain.GroupRanking;
import MathCaptain.weakness.Ranking.domain.PersonalRanking;
import MathCaptain.weakness.Ranking.dto.response.PersonalRankingResponseDto;
import MathCaptain.weakness.Ranking.dto.response.GroupRankingResponseDto;
import MathCaptain.weakness.Ranking.repository.RankingRepository;
import MathCaptain.weakness.Ranking.repository.GroupRankingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class RankingService {

    private final RankingRepository rankingRepository;
    private final GroupRankingRepository groupRankingRepository;

    // 개인 랭킹 조회
    public List<PersonalRankingResponseDto> getPersonalRankings() {
        List<PersonalRanking> rankings = rankingRepository.findTopRankingUsers();
        return IntStream.range(0, rankings.size())
                .mapToObj(i -> PersonalRankingResponseDto.builder()
                        .userId(rankings.get(i).getUser().getUserId())
                        .nickname(rankings.get(i).getUser().getNickname())
                        .personalPoint(rankings.get(i).getPersonalPoint())
                        .ranking(i + 1)
                        .build())
                .toList();
    }

    // 그룹 랭킹 조회
    public List<GroupRankingResponseDto> getGroupRankings() {
        List<GroupRanking> rankings = groupRankingRepository.findTopRankingGroups();
        return IntStream.range(0, rankings.size())
                .mapToObj(i -> GroupRankingResponseDto.builder()
                        .groupId(rankings.get(i).getGroup().getId())
                        .groupName(rankings.get(i).getGroup().getName())
                        .totalGroupPoint(rankings.get(i).getTotalGroupPoint())
                        .ranking(i + 1)
                        .build())
                .toList();
    }
}
