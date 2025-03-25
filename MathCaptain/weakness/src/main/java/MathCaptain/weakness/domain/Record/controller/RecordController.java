package MathCaptain.weakness.domain.Record.controller;

import MathCaptain.weakness.domain.Record.dto.request.recordEndRequestDto;
import MathCaptain.weakness.domain.Record.dto.response.recordStartResponseDto;
import MathCaptain.weakness.domain.Record.dto.response.recordSummaryResponseDto;
import MathCaptain.weakness.domain.Record.service.RecordService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record/")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/start/{groupId}")
    public ApiResponse<recordStartResponseDto> startRecord(@LoginUser Users loginUser, @PathVariable Long groupId) {
        recordStartResponseDto startResponse = recordService.startRecord(loginUser, groupId);
        return ApiResponse.ok(startResponse);
    }

    @PostMapping("/end/{recordId}")
    public ApiResponse<recordSummaryResponseDto> endActivity(@PathVariable Long recordId, @RequestBody recordEndRequestDto requestDto) {
        return ApiResponse.ok(recordService.endActivity(recordId, requestDto));
    }
}
