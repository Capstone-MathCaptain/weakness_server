package MathCaptain.weakness.Recruitment.service;

import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.dto.request.CreateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.response.CommentResponseDto;
import MathCaptain.weakness.Recruitment.dto.response.CommentSuccessDto;
import MathCaptain.weakness.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final RecruitmentRepository recruitmentRepository;
    private final JwtService jwtService;

    /// 댓글 CRUD

    // 댓글 생성
    public ApiResponse<CommentSuccessDto> createComment(String accessToken, Long recruitmentId, CreateCommentRequestDto createCommentRequestDto) {

        String email = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));

        Users author = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("해당 이메일을 가진 사용자가 없습니다."));

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        String content = createCommentRequestDto.getContent();

        Comment comment = Comment.builder()
                .post(recruitment)
                .author(author)
                .content(content)
                .build();

        commentRepository.save(comment);

        return ApiResponse.ok(CommentSuccessDto.builder()
                .commentId(comment.getCommentId())
                .recruitmentId(recruitmentId)
                .build());
    }

    // 댓글 수정
    public ApiResponse<CommentSuccessDto> updateComment(Long recruitmentId, Long commentId, UpdateCommentRequestDto updateCommentRequestDto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (!recruitmentId.equals(comment.getPost().getPostId())) {
            throw new IllegalArgumentException("해당 댓글이 해당 모집글에 속해있지 않습니다.");
        }

        if (!comment.getContent().equals(updateCommentRequestDto.getContent())) {
            comment.updateContent(updateCommentRequestDto.getContent());
        }

        // TODO 알림 기능 추가

        return ApiResponse.ok(CommentSuccessDto.builder()
                .commentId(commentId)
                .recruitmentId(recruitmentId)
                .build());
    }

    // 댓글 삭제
    public ApiResponse<CommentSuccessDto> deleteComment(Long recruitmentId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (!recruitmentId.equals(comment.getPost().getPostId())) {
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
