package MathCaptain.weakness.domain.Recruitment.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.enums.GroupRole;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.Recruitment.dto.response.*;
import MathCaptain.weakness.domain.Recruitment.entity.Recruitment;
import MathCaptain.weakness.domain.Recruitment.dto.request.CreateRecruitmentRequest;
import MathCaptain.weakness.domain.Recruitment.dto.request.UpdateRecruitmentRequest;
import MathCaptain.weakness.domain.Recruitment.repository.RecruitmentRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ApiResponse<RecruitmentCreateResponse> createRequest(Users user) {
        RelationBetweenUserAndGroup relation = findLeaderRelationBy(user);
        return ApiResponse.ok(RecruitmentCreateResponse.of(relation));
    }

    // 모집글 생성
    public ApiResponse<Long>createRecruitment(Users author, CreateRecruitmentRequest createRecruitmentRequest) {
        Group group = getGroupBy(createRecruitmentRequest);
        Recruitment recruitment = Recruitment.of(author, group, createRecruitmentRequest);
        Long recruitmentId = recruitmentRepository.save(recruitment).getPostId();
        return ApiResponse.ok(recruitmentId);
    }

    // 모집글 조회
    public ApiResponse<RecruitmentDetailResponse> getRecruitment(Long recruitmentId) {
        Recruitment recruitment = findRecruitmentBy(recruitmentId);
        List<CommentResponse> comments = commentService.getComments(recruitmentId);
        return ApiResponse.ok(RecruitmentDetailResponse.of(recruitment, comments));
    }

    // 모집글 수정
    public ApiResponse<Long> updateRecruitment(Long recruitmentId, UpdateRecruitmentRequest updateRecruitmentRequest) {
        Recruitment recruitment = findRecruitmentBy(recruitmentId);
        recruitment.updateRecruitment(updateRecruitmentRequest);
        return ApiResponse.ok(recruitmentId);
    }

    public ApiResponse<Long> deleteRecruitment(Long recruitmentId) {
        Recruitment recruitment = findRecruitmentBy(recruitmentId);
        recruitmentRepository.delete(recruitment);
        return ApiResponse.ok(recruitmentId);
    }

    // 모집글 전체 조회
    public ApiResponse<List<RecruitmentResponse>> getAllRecruitments() {
        List<Recruitment> recruitments = recruitmentRepository.findAll();
        return ApiResponse.ok(recruitments.stream()
                .map(RecruitmentResponse::of)
                .toList());
    }

    private Group getGroupBy(CreateRecruitmentRequest createRecruitmentRequest) {
        return groupRepository.findById(createRecruitmentRequest.getRecruitGroupId()).
                orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));
    }

    private RelationBetweenUserAndGroup findLeaderRelationBy(Users user) {
        return relationRepository.findByMemberAndGroupRole(user, GroupRole.LEADER)
                .orElseThrow(() -> new IllegalArgumentException("그룹장만 모집글을 작성할 수 있습니다."));
    }

    private Recruitment findRecruitmentBy(Long recruitmentId) {
        return recruitmentRepository.findById(recruitmentId).
                orElseThrow(() -> new ResourceNotFoundException("해당 모집글이 없습니다."));
    }

}
