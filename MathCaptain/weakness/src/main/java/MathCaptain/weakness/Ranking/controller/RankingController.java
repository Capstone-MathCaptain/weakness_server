package MathCaptain.weakness.Ranking.controller;

import MathCaptain.weakness.Ranking.dto.response.GroupRankingResponseDto;
import MathCaptain.weakness.Ranking.service.RankingService;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    // 그룹 랭킹 조회
    @GetMapping("/{page}")
    public ApiResponse<Page<GroupRankingResponseDto>> getGroupRankings(
            @PathVariable("page") int page) {
        // 페이지 번호를 기반으로 Pageable 생성
        Pageable pageable = PageRequest.of(page - 1, 10, Sort.by(Sort.Direction.DESC, "groupPoint"));

        return ApiResponse.ok(rankingService.getGroupRankings(pageable));
    }
}