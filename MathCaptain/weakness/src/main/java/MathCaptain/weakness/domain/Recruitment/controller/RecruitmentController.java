package MathCaptain.weakness.domain.Recruitment.controller;

import MathCaptain.weakness.domain.Notification.service.NotificationService;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateCommentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateRecruitmentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateCommentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateRecruitmentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.response.*;
import MathCaptain.weakness.domain.Recruitment.service.CommentService;
import MathCaptain.weakness.domain.Recruitment.service.RecruitmentService;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.annotation.LoginUser;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/recruitment")
public class RecruitmentController {

    private final RecruitmentService recruitmentService;
    private final CommentService commentService;
    private final NotificationService notificationService;

    /// 모집 CRUD

    // 모집글 리스트 조회
    @GetMapping
    public ApiResponse<List<RecruitmentResponse>> recruitmentList() {
        return recruitmentService.getAllRecruitments();
    }

    // 모집글 생성
    @PostMapping("/create")
    public ApiResponse<RecruitmentDetailResponse> createRecruitment(
            @Valid @LoginUser Users loginUser,
            @RequestBody CreateRecruitmentRequest createRecruitmentRequest) {
        return recruitmentService.createRecruitment(loginUser, createRecruitmentRequest);
    }

    // 모집글 상세 조회
    @GetMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentDetailResponse> recruitmentDetailInfo(
            @LoginUser Users loginUser,
            @PathVariable Long recruitmentId
    ) {
        return recruitmentService.getRecruitment(recruitmentId, loginUser);
    }

    // 모집글 수정
    @PutMapping("/{recruitmentId}")
    public ApiResponse<Long> updateRecruitment(@Valid @PathVariable Long recruitmentId, @RequestBody UpdateRecruitmentRequest updateRecruitmentRequest) {
        return recruitmentService.updateRecruitment(recruitmentId, updateRecruitmentRequest);
    }

    // 모집글 삭제
    @DeleteMapping("/{recruitmentId}")
    public ApiResponse<Long> deleteRecruitment(@PathVariable Long recruitmentId) {
        return recruitmentService.deleteRecruitment(recruitmentId);
    }

    // 댓글 작성
    @PostMapping("/comment/{recruitmentId}")
    public ApiResponse<CommentSuccessResponse> createComment(@Valid @PathVariable Long recruitmentId,
                                                             @LoginUser Users loginUser,
                                                             @RequestBody CreateCommentRequest createCommentRequest) {
        CommentSuccessResponse commentSuccessResponse = commentService.createComment(loginUser, recruitmentId, createCommentRequest);
        notificationService.notifyComment(recruitmentId, commentSuccessResponse.getCommentId());
        return ApiResponse.ok(commentSuccessResponse);
    }

    // 댓글 수정
    @PutMapping("/comment/{recruitmentId}/{commentId}")
    public ApiResponse<CommentSuccessResponse> updateComment(@Valid @PathVariable Long recruitmentId, @PathVariable Long commentId,
                                                             @RequestBody UpdateCommentRequest updateCommentRequest) {
        return commentService.updateComment(recruitmentId, commentId, updateCommentRequest);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{recruitmentId}/{commentId}")
    public ApiResponse<CommentSuccessResponse> deleteComment(@PathVariable Long recruitmentId, @PathVariable Long commentId) {
        return commentService.deleteComment(recruitmentId, commentId);
    }

}
