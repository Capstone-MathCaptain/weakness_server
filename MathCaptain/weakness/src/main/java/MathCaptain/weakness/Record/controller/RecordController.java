package MathCaptain.weakness.Record.controller;

import MathCaptain.weakness.Record.dto.request.recordStartRequestDto;
import MathCaptain.weakness.Record.dto.response.recordSummaryResponseDto;
import MathCaptain.weakness.Record.service.RecordService;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/activity")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/start")
    public ApiResponse<Long> startRecord(@RequestBody recordStartRequestDto requestDto) {
        Long recordId = recordService.startRecord(requestDto.getUserId(), requestDto.getGroupId());
        return ApiResponse.ok(recordId);
    }

    @PostMapping("/end/{activityId}")
    public ApiResponse<recordSummaryResponseDto> endActivity(@PathVariable Long activityId) {
        recordSummaryResponseDto summary = recordService.endActivity(activityId);
        return ApiResponse.ok(summary);
    }
}
