package MathCaptain.weakness.domain.Group.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.dto.response.GroupMemberListResponse;
import MathCaptain.weakness.domain.Group.dto.response.GroupResponse;
import MathCaptain.weakness.domain.Group.dto.response.RelationResponse;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Record.repository.RecordRepository;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import MathCaptain.weakness.domain.Group.enums.GroupRole;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final GroupRepository groupRepository;
    private final RecordRepository recordRepository;

    // 그룹 탈퇴
    public ApiResponse<?> leaveGroup(Users user, Long groupId) {

        Group group = findGroupById(groupId);
        RelationBetweenUserAndGroup relation = findRelationByMemberAndGroup(user, group);
        if (isLeader(relation)) {
            throw new IllegalArgumentException("리더는 그룹을 탈퇴할 수 없습니다.");
        }
        relationRepository.delete(relation);
        return ApiResponse.ok("그룹 탈퇴가 완료되었습니다.");
    }

    // 그룹 멤버 리스트 (그룹 상세 페이지)
    public List<GroupMemberListResponse> getGroupMemberList(Long groupId) {
        List<RelationBetweenUserAndGroup> relations = findRelationsByGroupId(groupId);
        // 이번 주의 시작과 끝 시간 계산
        LocalDateTime startOfWeek = LocalDateTime.now().with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDateTime endOfWeek = startOfWeek.plusWeeks(1);
        // 멤버 리스트 생성
        return relations.stream()
                .map(relation -> toResponse(relation, startOfWeek, endOfWeek))
                .collect(Collectors.toList());
    }

    public RelationResponse getRelationResponse(Long relationId) {
        RelationBetweenUserAndGroup relation = findRelationById(relationId);
        GroupResponse groupResponse = GroupResponse.of(relation);
        UserResponse member = UserResponse.of(relation);
        return RelationResponse.of(relation, member, groupResponse);
    }

    ///  로직
    public RelationBetweenUserAndGroup findRelationByMemberAndGroup(Users member, Group group) {
        return relationRepository.findByMemberAndGroup(member, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));
    }

    private boolean isLeader(RelationBetweenUserAndGroup relation) {
        return relation.getGroupRole().equals(GroupRole.LEADER);
    }

    private GroupMemberListResponse toResponse(RelationBetweenUserAndGroup relation, LocalDateTime startOfWeek, LocalDateTime endOfWeek) {
        // 기록 리포지토리에서 현재 진행 상황 가져오기 (null 방지)
        Integer currentProgress = recordRepository.countDailyGoalAchieved(
                relation.getGroup().getId(), relation.getMember().getUserId(),
                startOfWeek, endOfWeek
        ).orElse(0);
        return GroupMemberListResponse.of(relation, currentProgress);
    }

    private List<RelationBetweenUserAndGroup> findRelationsByGroupId(Long groupId) {
        return relationRepository.findAllByGroup_id(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹에 멤버가 없습니다."));
    }

    private Group findGroupById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
    }

    private RelationBetweenUserAndGroup findRelationById(Long relationId) {
        return relationRepository.findById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 없습니다."));
    }
}
