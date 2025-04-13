package MathCaptain.weakness.domain.Record.controller;

import MathCaptain.weakness.domain.Record.dto.request.FitnessLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.request.RecordEndRequest;
import MathCaptain.weakness.domain.Record.dto.request.RunningLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.request.StudyLogEnrollRequest;
import MathCaptain.weakness.domain.Record.dto.response.RecordSummaryResponse;
import MathCaptain.weakness.domain.Record.service.RecordService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import static MathCaptain.weakness.domain.Group.enums.CategoryStatus.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record")
public class RecordController {

    private final RecordService recordService;

    @PostMapping("/end/fitness/{groupId}")
    public ApiResponse<RecordSummaryResponse> endFitnessActivity(
            @Valid @LoginUser Users loginUser,
            @PathVariable Long groupId,
            @RequestBody FitnessLogEnrollRequest logRequest) {
        return ApiResponse.ok(recordService.endActivity(loginUser, groupId, logRequest, FITNESS));
    }

    @PostMapping("/end/running/{groupId}")
    public ApiResponse<RecordSummaryResponse> endRunningActivity(
            @Valid @LoginUser Users loginUser,
            @PathVariable Long groupId,
            @RequestBody RunningLogEnrollRequest logRequest) {
        return ApiResponse.ok(recordService.endActivity(loginUser, groupId, logRequest, RUNNING));
    }

    @PostMapping("/end/study/{groupId}")
    public ApiResponse<RecordSummaryResponse> endStudyActivity(
            @Valid @LoginUser Users loginUser,
            @PathVariable Long groupId,
            @RequestBody StudyLogEnrollRequest logRequest) {
        return ApiResponse.ok(recordService.endActivity(loginUser, groupId, logRequest, STUDY));
    }
}
