package MathCaptain.weakness.Recruitment.service;

import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.dto.request.CreateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateCommentRequestDto;
import MathCaptain.weakness.Recruitment.dto.response.CommentResponseDto;
import MathCaptain.weakness.Recruitment.repository.CommentRepository;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
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

    public Long createComment(Long recuritmentId, CreateCommentRequestDto createCommentRequestDto) {

        Users author = userRepository.findById(createCommentRequestDto.getAuthorId())
                .orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Recruitment recruitment = recruitmentRepository.findById(recuritmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        Comment comment = Comment.builder()
                .post(recruitment)
                .author(author)
                .content(createCommentRequestDto.getContent())
                .lastModifiedTime(LocalDateTime.now())
                .build();

        commentRepository.save(comment);

        log.info("createComment");

        return comment.getCommentId();
    }

    public void updateComment(Long recruitmentId, Long commentId, UpdateCommentRequestDto updateCommentRequestDto) {

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (!recruitmentId.equals(comment.getPost().getPostId())) {
            throw new IllegalArgumentException("해당 댓글이 해당 모집글에 속해있지 않습니다.");
        }

        if (!comment.getContent().equals(updateCommentRequestDto.getContent())) {
            comment.updateContent(updateCommentRequestDto.getContent());
        }

        log.info("updateComment");
    }

    public List<CommentResponseDto> getComments(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        List<Comment> commentList = commentRepository.findAllByPost(recruitment);

        return commentList.stream()
                .map(this::buildCommentResponseDto)
                .toList();
    }

    public void deleteComment(Long recruitmentId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("해당 댓글이 없습니다."));

        if (!recruitmentId.equals(comment.getPost().getPostId())) {
            throw new IllegalArgumentException("해당 댓글이 해당 모집글에 속해있지 않습니다.");
        }

        commentRepository.delete(comment);
    }

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
