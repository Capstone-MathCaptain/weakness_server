package MathCaptain.weakness.domain.Recruitment.service;

import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateCommentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateCommentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.response.CommentResponse;
import MathCaptain.weakness.domain.Recruitment.dto.response.CommentSuccessResponse;
import MathCaptain.weakness.domain.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.domain.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final RecruitmentRepository recruitmentRepository;

    /// 댓글 CRUD

    // 댓글 생성
    public CommentSuccessResponse createComment(Users user, Long recruitmentId, CreateCommentRequest createCommentRequest) {
        Recruitment recruitment = findRecruitmentBy(recruitmentId);
        Comment comment = Comment.of(recruitment, user, createCommentRequest);
        commentRepository.save(comment);
        return CommentSuccessResponse.of(recruitmentId, comment);
    }

    // 댓글 수정
    public ApiResponse<CommentSuccessResponse> updateComment(Long recruitmentId, Long commentId, UpdateCommentRequest updateCommentRequest) {
        Comment comment = findCommentBy(commentId, recruitmentId);
        comment.updateComment(updateCommentRequest);
        return ApiResponse.ok(CommentSuccessResponse.of(recruitmentId, commentId));
    }

    // 댓글 삭제
    public ApiResponse<CommentSuccessResponse> deleteComment(Long recruitmentId, Long commentId) {
        Comment comment = findCommentBy(commentId, recruitmentId);
        commentRepository.delete(comment);
        return ApiResponse.ok(CommentSuccessResponse.of(recruitmentId, commentId));
    }

    // 댓글 조회
    public List<CommentResponse> getComments(Long recruitmentId) {
        Recruitment recruitment = findRecruitmentBy(recruitmentId);
        List<Comment> commentList = commentRepository.findAllByPost(recruitment);
        return commentList.stream()
                .map(CommentResponse::of)
                .toList();
    }

    private Recruitment findRecruitmentBy(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));
    }

    private Comment findCommentBy(Long commentId, Long recruitmentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (comment.isBelongToPost(recruitmentId)) {
            throw new IllegalArgumentException("해당 댓글이 해당 모집글에 속해있지 않습니다.");
        }

        return comment;
    }
}
