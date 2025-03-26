package MathCaptain.weakness.domain.Record.controller;

import MathCaptain.weakness.domain.Record.dto.request.recordEndRequestDto;
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

    @PostMapping("/end/{groupId}")
    public ApiResponse<recordSummaryResponseDto> endActivity(@LoginUser Users loginUser, @PathVariable Long groupId, @RequestBody recordEndRequestDto requestDto) {
        return ApiResponse.ok(recordService.endActivity(loginUser, groupId, requestDto));
    }
}
