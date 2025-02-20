package MathCaptain.weakness.Recruitment.controller;

import MathCaptain.weakness.Recruitment.dto.request.CreateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.CreateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.response.*;
import MathCaptain.weakness.Recruitment.service.CommentService;
import MathCaptain.weakness.Recruitment.service.RecruitmentService;
import MathCaptain.weakness.global.Api.ApiResponse;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    /// 모집 CRUD

    // 모집글 리스트 조회
    @GetMapping
    public ApiResponse<List<RecruitmentResponseDto>> recruitmentList() {
        return recruitmentService.getAllRecruitments();
    }

    // 모집글 작성 요청
    @GetMapping("/create")
    public ApiResponse<RecruitmentCreateResponseDto> createRecruitmentPage(@RequestHeader("Authorization") String authorizationHeader) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return recruitmentService.createRequest(accessToken);
    }

    // 모집글 생성
    @PostMapping("/create")
    public ApiResponse<RecruitmentSuccessDto> createRecruitment(@Valid @RequestHeader("Authorization") String authorizationHeader,
                                                                @RequestBody CreateRecruitmentRequestDto createRecruitmentRequestDto) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return recruitmentService.createRecruitment(accessToken, createRecruitmentRequestDto);
    }

    // 모집글 상세 조회
    @GetMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentDetailResponseDto> recruitmentDetailInfo(@PathVariable Long recruitmentId) {
        return recruitmentService.getRecruitment(recruitmentId);
    }

    // 모집글 수정
    @PutMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentSuccessDto> updateRecruitment(@Valid @PathVariable Long recruitmentId, @RequestBody UpdateRecruitmentRequestDto updateRecruitmentRequestDto) {
        return recruitmentService.updateRecruitment(recruitmentId, updateRecruitmentRequestDto);
    }

    // 모집글 삭제
    @DeleteMapping("/{recruitmentId}")
    public ApiResponse<RecruitmentSuccessDto> deleteRecruitment(@PathVariable Long recruitmentId) {
        return recruitmentService.deleteRecruitment(recruitmentId);
    }

    @PostMapping("/comment/{recruitmentId}")
    public ApiResponse<CommentSuccessDto> createComment(@Valid @PathVariable Long recruitmentId,
                                                        @RequestHeader("Authorization") String authorizationHeader,
                                                        @RequestBody CreateCommentRequestDto createCommentRequestDto) {
        String accessToken = authorizationHeader.replace("Bearer ", "");
        return commentService.createComment(accessToken, recruitmentId, createCommentRequestDto);
    }

    // 댓글 수정
    @PutMapping("/comment/{recruitmentId}/{commentId}")
    public ApiResponse<CommentSuccessDto> updateComment(@Valid @PathVariable Long recruitmentId, @PathVariable Long commentId,
                                      @RequestBody UpdateCommentRequestDto updateCommentRequestDto) {
        return commentService.updateComment(recruitmentId, commentId, updateCommentRequestDto);
    }

    // 댓글 삭제
    @DeleteMapping("/comment/{recruitmentId}/{commentId}")
    public ApiResponse<CommentSuccessDto> deleteComment(@PathVariable Long recruitmentId, @PathVariable Long commentId) {
        return commentService.deleteComment(recruitmentId, commentId);
    }

}
