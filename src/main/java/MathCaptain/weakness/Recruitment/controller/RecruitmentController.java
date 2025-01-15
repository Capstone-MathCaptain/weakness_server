package MathCaptain.weakness.Recruitment.controller;

import MathCaptain.weakness.Recruitment.dto.request.CreateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.CreateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.response.RecruitmentDetailResponseDto;
import MathCaptain.weakness.Recruitment.dto.response.RecruitmentResponseDto;
import MathCaptain.weakness.Recruitment.service.CommentService;
import MathCaptain.weakness.Recruitment.service.RecruitmentService;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final CommentService commentService;

    @GetMapping
    public ApiResponse<List<RecruitmentResponseDto>> recruitmentList() {
        return recruitmentService.getAllRecruitments();
    }

    @PostMapping
    public ApiResponse<RecruitmentResponseDto> createRecruitment(@RequestBody CreateRecruitmentRequestDto createRecruitmentRequestDto) {
        return recruitmentService.createRecruitment(createRecruitmentRequestDto);
    }

    @GetMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentDetailResponseDto> recruitmentDetailInfo(@PathVariable Long recruitmentId) {
        return recruitmentService.getRecruitment(recruitmentId);
    }

    @PutMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentResponseDto> updateRecruitment(@PathVariable Long recruitmentId, @RequestBody UpdateRecruitmentRequestDto updateRecruitmentRequestDto) {
        return recruitmentService.updateRecruitment(recruitmentId, updateRecruitmentRequestDto);
    }

    @PostMapping("/comment/{recruitmentId}")
    public RedirectView createComment(@PathVariable Long recruitmentId, @RequestBody CreateCommentRequestDto createCommentRequestDto) {
        Long commentId = commentService.createComment(recruitmentId, createCommentRequestDto);
        log.info("Comment Saved, redirect to /recruitment/{}?commentId={}", recruitmentId, commentId);
        return new RedirectView("/recruitment/" + recruitmentId + "?commentId=" + commentId);    }

    @PutMapping("/comment/{recruitmentId}/{commentId}")
    public RedirectView updateComment(@PathVariable Long recruitmentId, @PathVariable Long commentId,
                                                      @RequestBody UpdateCommentRequestDto updateCommentRequestDto) {
        commentService.updateComment(recruitmentId, commentId, updateCommentRequestDto);
        log.info("Comment Updated, redirect to /recruitment/{}?commentId={}", recruitmentId, commentId);
        return new RedirectView("/recruitment/" + recruitmentId + "?commentId=" + commentId);
    }

    @DeleteMapping("/comment/{recruitmentId}/{commentId}")
    public RedirectView deleteComment(@PathVariable Long recruitmentId, @PathVariable Long commentId) {
        commentService.deleteComment(recruitmentId, commentId);
        log.info("Comment deleted, redirect to /recruitment/{}?commentId={}", recruitmentId, commentId);
        return new RedirectView("/recruitment/" + recruitmentId + "?commentId=" + commentId);
    }

}
