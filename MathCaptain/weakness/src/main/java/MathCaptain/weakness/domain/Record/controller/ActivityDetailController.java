package MathCaptain.weakness.domain.Record.controller;

import MathCaptain.weakness.domain.Record.service.ActivityDetailService;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/record/detail")
public class ActivityDetailController {

    private final ActivityDetailService activityDetailService;

    @GetMapping("/fitness/{activityId}")
    public ApiResponse<?> getFitnessLog(@PathVariable Long activityId) {
        return ApiResponse.ok(activityDetailService.getFitnessLog(activityId));
    }

    @GetMapping("/study/{activityId}")
    public ApiResponse<?> getStudyLog(@PathVariable Long activityId) {
        return ApiResponse.ok(activityDetailService.getStudyLog(activityId));
    }

    @GetMapping("/running/{activityId}")
    public ApiResponse<?> getRunningLog(@PathVariable Long activityId) {
        return ApiResponse.ok(activityDetailService.getRunningLog(activityId));
    }
}
