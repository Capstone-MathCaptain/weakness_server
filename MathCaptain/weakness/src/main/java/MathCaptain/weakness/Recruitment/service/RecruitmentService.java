package MathCaptain.weakness.Recruitment.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.Recruitment.domain.Recruitment;
import MathCaptain.weakness.Recruitment.dto.request.CreateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.request.UpdateRecruitmentRequestDto;
import MathCaptain.weakness.Recruitment.dto.response.*;
import MathCaptain.weakness.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
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
    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;
    private final CommentService commentService;

    /// 모집 CRUD
    // 모집글 작성 요청 (그룹 정보 반환)
    public ApiResponse<RecruitmentCreateResponseDto> createRequest(Users user) {

        RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndGroupRole(user, GroupRole.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("그룹장만 모집글을 작성할 수 있습니다."));

        return ApiResponse.ok(RecruitmentCreateResponseDto.builder()
                    .groupId(relation.getGroup().getId())
                    .groupName(relation.getGroup().getName())
                    .leaderName(relation.getMember().getName())
                    .build());
    }

    // 모집글 생성
    public ApiResponse<RecruitmentSuccessDto>createRecruitment(Users author, CreateRecruitmentRequestDto createRecruitmentRequestDto) {

        Recruitment recruitment = buildRecruitment(author, createRecruitmentRequestDto);

        return ApiResponse.ok(RecruitmentSuccessDto.builder()
                .recruitmentId(recruitmentRepository.save(recruitment).getPostId())
                .build());
    }

    // 모집글 조회
    public ApiResponse<RecruitmentDetailResponseDto> getRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).
                orElseThrow(() -> new ResourceNotFoundException("해당 모집글이 없습니다."));

        List<CommentResponseDto> comments = commentService.getComments(recruitmentId);

        return ApiResponse.ok(buildRecruitmentDetailResponseDto(recruitment, comments));
    }

    // 모집글 수정
    public ApiResponse<RecruitmentSuccessDto> updateRecruitment(Long recruitmentId, UpdateRecruitmentRequestDto updateRecruitmentRequestDto) {

        Recruitment recruitment = recruitmentRepository.findById(recruitmentId).
                orElseThrow(() -> new ResourceNotFoundException("해당 모집글이 없습니다."));

        recruitment.updateRecruitment(updateRecruitmentRequestDto);

        log.info("모집글 수정 완료, 모집글 ID : {}", recruitmentId);

        return ApiResponse.ok(RecruitmentSuccessDto.builder()
                .recruitmentId(recruitmentId)
                .build());
    }

    public ApiResponse<RecruitmentSuccessDto> deleteRecruitment(Long recruitmentId) {
        Recruitment recruitment = recruitmentRepository.findById(recruitmentId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 모집글이 없습니다."));

        recruitmentRepository.delete(recruitment);

        return ApiResponse.ok(RecruitmentSuccessDto.builder()
                .recruitmentId(recruitmentId)
                .build());
    }

    // 모집글 전체 조회
    public ApiResponse<List<RecruitmentResponseDto>> getAllRecruitments() {
        List<Recruitment> recruitments = recruitmentRepository.findAll();

        return ApiResponse.ok(recruitments.stream()
                .map(this::buildRecruitmentResponseDto)
                .toList());
    }

    /// 빌드

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

    private Recruitment buildRecruitment(Users author, CreateRecruitmentRequestDto createRecruitmentRequestDto) {

        Group relatedGroup = groupRepository.findById(createRecruitmentRequestDto.getRecruitGroupId()).
                orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        return Recruitment.builder()
                .author(author)
                .recruitGroup(relatedGroup)
                .title(createRecruitmentRequestDto.getTitle())
                .content(createRecruitmentRequestDto.getContent())
                .category(relatedGroup.getCategory())
                .lastModifiedTime(LocalDateTime.now())
                .build();
    }

}
