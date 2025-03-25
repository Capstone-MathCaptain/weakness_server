package MathCaptain.weakness.domain.Recruitment.service;

import MathCaptain.weakness.domain.Recruitment.entity.Comment;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateCommentRequestDto;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.domain.Recruitment.dto.response.CommentResponseDto;
import MathCaptain.weakness.domain.Recruitment.dto.response.CommentSuccessDto;
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
    public CommentSuccessDto createComment(Users user, Long recruitmentId, CreateCommentRequestDto createCommentRequestDto) {

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        String content = createCommentRequestDto.getContent();

        Comment comment = Comment.builder()
                .post(recruitment)
                .author(user)
                .content(content)
                .build();

        commentRepository.save(comment);

        return CommentSuccessDto.builder()
                .commentId(comment.getCommentId())
                .recruitmentId(recruitmentId)
                .build();
    }

    // 댓글 수정
    public ApiResponse<CommentSuccessDto> updateComment(Long recruitmentId, Long commentId, UpdateCommentRequestDto updateCommentRequestDto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (comment.isBelongToPost(recruitmentId)) {
            throw new IllegalArgumentException("해당 댓글이 해당 모집글에 속해있지 않습니다.");
        }

        comment.updateComment(updateCommentRequestDto);

        return ApiResponse.ok(CommentSuccessDto.builder()
                .commentId(commentId)
                .recruitmentId(recruitmentId)
                .build());
    }

    // 댓글 삭제

    public ApiResponse<CommentSuccessDto> deleteComment(Long recruitmentId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (comment.isBelongToPost(recruitmentId)) {
            throw new IllegalArgumentException("해당 댓글이 해당 모집글에 속해있지 않습니다.");
        }

        commentRepository.delete(comment);

        return ApiResponse.ok(CommentSuccessDto.builder()
                .commentId(commentId)
                .recruitmentId(recruitmentId)
                .build());
    }
    // 댓글 조회

    public List<CommentResponseDto> getComments(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        List<Comment> commentList = commentRepository.findAllByPost(recruitment);

        return commentList.stream()
                .map(this::buildCommentResponseDto)
                .toList();
    }

    /// 빌더

    private CommentResponseDto buildCommentResponseDto(Comment comment) {
        return CommentResponseDto.builder()
                .commentId(comment.getCommentId())
                .authorName(comment.getAuthor().getName())
                .authorName(comment.getAuthor().getName())
                .content(comment.getContent())
                .createdAt(comment.getCommentTime())
                .updatedAt(comment.getLastModifiedTime())
                .build();
    }


}
