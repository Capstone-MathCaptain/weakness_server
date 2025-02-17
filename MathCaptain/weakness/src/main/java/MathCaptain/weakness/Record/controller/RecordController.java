package MathCaptain.weakness.Record.controller;

import MathCaptain.weakness.Record.dto.request.recordEndRequestDto;
import MathCaptain.weakness.Record.dto.request.recordStartRequestDto;
import MathCaptain.weakness.Record.dto.response.recordStartResponseDto;
import MathCaptain.weakness.Record.dto.response.recordSummaryResponseDto;
import MathCaptain.weakness.Record.service.RecordService;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record/")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/start/{groupId}")
    public ApiResponse<recordStartResponseDto> startRecord(@RequestHeader("Authorization") String authorizationHeader, @PathVariable Long groupId) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        recordStartResponseDto startResponse = recordService.startRecord(accessToken, groupId);
        return ApiResponse.ok(startResponse);
    }

    @PostMapping("/end/{recordId}")
    public ApiResponse<recordSummaryResponseDto> endActivity(@PathVariable Long recordId, @RequestBody recordEndRequestDto requestDto) {
        return ApiResponse.ok(recordService.endActivity(recordId, requestDto));
    }
}
