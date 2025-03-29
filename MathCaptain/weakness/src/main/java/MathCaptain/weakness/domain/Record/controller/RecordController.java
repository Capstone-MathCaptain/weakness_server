package MathCaptain.weakness.domain.Record.controller;

import MathCaptain.weakness.domain.Record.dto.request.recordEndRequest;
import MathCaptain.weakness.domain.Record.dto.response.RecordSummaryResponse;
import MathCaptain.weakness.domain.Record.service.RecordService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record/")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/end/{groupId}")
    public ApiResponse<RecordSummaryResponse> endActivity(@Valid @LoginUser Users loginUser, @PathVariable Long groupId, @RequestBody recordEndRequest requestDto) {
        return ApiResponse.ok(recordService.endActivity(loginUser, groupId, requestDto));
    }
}
