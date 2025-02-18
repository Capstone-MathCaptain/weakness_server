package MathCaptain.weakness.Ranking.controller;

import MathCaptain.weakness.Ranking.dto.response.PersonalRankingResponseDto;
import MathCaptain.weakness.Ranking.dto.response.GroupRankingResponseDto;
import MathCaptain.weakness.Ranking.service.RankingService;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final RankingService rankingService;

    // 개인 랭킹 조회
    @GetMapping("/personal")
    public ApiResponse<List<PersonalRankingResponseDto>> getPersonalRankings() {
        return ApiResponse.ok(rankingService.getPersonalRankings());
    }

    // 그룹 랭킹 조회
    @GetMapping("/group")
    public ApiResponse<List<GroupRankingResponseDto>> getGroupRankings() {
        return ApiResponse.ok(rankingService.getGroupRankings());
    }
}
