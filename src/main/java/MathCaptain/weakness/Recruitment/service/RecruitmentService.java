package MathCaptain.weakness.Recruitment.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Recruitment.domain.Comment;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.dto.request.CreateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.response.CommentResponseDto;
import MathCaptain.weakness.Recruitment.dto.response.RecruitmentDetailResponseDto;
import MathCaptain.weakness.Recruitment.dto.response.RecruitmentResponseDto;
import MathCaptain.weakness.Recruitment.enums.RecruitmentStatus;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class RecruitmentService {

    private final RecruitmentRepository recruitmentRepository;
    private final UserRepository userRepository;
    private final GroupRepository groupRepository;
    private final CommentService commentService;

    public RecruitmentResponseDto createRecruitment(CreateRecruitmentRequestDto createRecruitmentRequestDto) {

        Users author = userRepository.findByUserId(createRecruitmentRequestDto.getAuthorId()).
                orElseThrow(() -> new IllegalArgumentException("해당 유저가 없습니다."));

        Group relatedGroup = groupRepository.findById(createRecruitmentRequestDto.getRecruitGroupId()).
                orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다."));

        Recruitment recruitment = Recruitment.builder()
                .author(author)
                .recruitGroup(relatedGroup)
                .title(createRecruitmentRequestDto.getTitle())
                .content(createRecruitmentRequestDto.getContent())
                .category(createRecruitmentRequestDto.getCategory())
                .lastModifiedTime(LocalDateTime.now())
                .build();

        recruitmentRepository.save(recruitment);

        log.info("Recruitment created: {}", recruitment);

        return buildRecruitmentResponseDto(recruitment);
    }

    public RecruitmentDetailResponseDto getRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).
                orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        List<CommentResponseDto> comments = commentService.getComments(recruitmentId);

        return buildRecruitmentDetailResponseDto(recruitment, comments);
    }

    public RecruitmentResponseDto updateRecruitment(Long recruitmentId, UpdateRecruitmentRequestDto updateRecruitmentRequestDto) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).
                orElseThrow(() -> new IllegalArgumentException("해당 모집글이 없습니다."));

        if(!recruitment.getAuthor().getUserId().equals(updateRecruitmentRequestDto.getAuthorId())) {
            throw new IllegalArgumentException("작성자만 수정할 수 있습니다.");
        }

        if (!recruitment.getTitle().equals(updateRecruitmentRequestDto.getTitle())) {
            recruitment.updateTitle(updateRecruitmentRequestDto.getTitle());
        }

        if (!recruitment.getContent().equals(updateRecruitmentRequestDto.getContent())) {
            recruitment.updateContent(updateRecruitmentRequestDto.getContent());
        }

        if (!recruitment.getRecruitmentStatus().equals(updateRecruitmentRequestDto.getRecruitmentStatus())) {
            recruitment.updateRecruitmentStatus(updateRecruitmentRequestDto.getRecruitmentStatus());
        }

        log.info("Recruitment updated: {}", recruitment);

        return buildRecruitmentResponseDto(recruitment);
    }

    public List<RecruitmentResponseDto> getAllRecruitments() {
        List<Recruitment> recruitments = recruitmentRepository.findAll();

        return recruitments.stream()
                .map(this::buildRecruitmentResponseDto)
                .toList();
    }

    private RecruitmentResponseDto buildRecruitmentResponseDto(Recruitment recruitment) {
        return RecruitmentResponseDto.builder()
                .authorName(recruitment.getAuthor().getName())
                .recruitGroupName(recruitment.getRecruitGroup().getName())
                .title(recruitment.getTitle())
                .category(recruitment.getCategory())
                .content(recruitment.getContent())
                .recruitmentStatus(recruitment.getRecruitmentStatus())
                .createdAt(recruitment.getPostTime())
                .updatedAt(recruitment.getLastModifiedTime())
                .build();
    }

    private RecruitmentDetailResponseDto buildRecruitmentDetailResponseDto(Recruitment recruitment, List<CommentResponseDto> comments) {


        return RecruitmentDetailResponseDto.builder()
                .authorName(recruitment.getAuthor().getName())
                .recruitGroupName(recruitment.getRecruitGroup().getName())
                .title(recruitment.getTitle())
                .category(recruitment.getCategory())
                .content(recruitment.getContent())
                .recruitmentStatus(recruitment.getRecruitmentStatus())
                .createdAt(recruitment.getPostTime())
                .updatedAt(recruitment.getLastModifiedTime())
                .comments(comments)
                .build();
    }

}
