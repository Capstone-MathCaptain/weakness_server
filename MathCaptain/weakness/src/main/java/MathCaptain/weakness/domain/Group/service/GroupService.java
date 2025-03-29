package MathCaptain.weakness.domain.Group.service;

import MathCaptain.weakness.domain.Group.entity.Group;
import MathCaptain.weakness.domain.Group.entity.RelationBetweenUserAndGroup;
import MathCaptain.weakness.domain.Group.dto.request.GroupCreateRequest;
import MathCaptain.weakness.domain.Group.dto.request.GroupUpdateRequest;
import MathCaptain.weakness.domain.Group.dto.response.GroupDetailResponse;
import MathCaptain.weakness.domain.Group.dto.response.GroupResponse;
import MathCaptain.weakness.domain.Group.dto.response.UserGroupCardResponse;
import MathCaptain.weakness.domain.Group.enums.CategoryStatus;
import MathCaptain.weakness.domain.Record.service.RecordService;
import MathCaptain.weakness.domain.User.dto.response.UserResponse;
import MathCaptain.weakness.domain.Group.repository.GroupRepository;
import MathCaptain.weakness.domain.Group.repository.RelationRepository;
import MathCaptain.weakness.domain.User.entity.Users;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;
    private final RecordService recordService;

    // 그룹 생성 (CREATE)
    public ApiResponse<GroupResponse> createGroup(Users leader, GroupCreateRequest groupCreateRequest) {
        // 그룹 생성
        Group group = Group.of(groupCreateRequest);
        groupRepository.save(group);

        RelationBetweenUserAndGroup relation = RelationBetweenUserAndGroup.of(leader, group, groupCreateRequest);
        relationRepository.save(relation);
        return ApiResponse.ok(GroupResponse.of(leader, group));
    }

    // 그룹 정보 조회 (READ)
    public GroupResponse getGroupInfo(Long groupId) {
        Group group = findById(groupId);
        Users leader = findLeaderByGroup(group);
        return GroupResponse.of(leader, group);
    }

    public GroupResponse getGroupInfo(Group group) {
        Users leader = findLeaderByGroup(group);
        return GroupResponse.of(leader, group);
    }

    public ApiResponse<List<GroupResponse>> getGroupInfo(String groupName) {
        List<Group> groups = groupRepository.findByNameContaining(groupName)
                .orElseThrow(() -> new ResourceNotFoundException("검색 결과가 없습니다."));
        // 조회된 그룹들을 GroupResponseDto로 변환
        return ApiResponse.ok(groups.stream()
                .map(group -> {
                    Users leader = findLeaderByGroup(group);
                    return GroupResponse.of(leader, group);
                })
                .collect(Collectors.toList()));
    }

    // 그룹 정보 업데이트 (UPDATE)
    public ApiResponse<GroupResponse> updateGroupInfo(Users leader, Long groupId, GroupUpdateRequest groupUpdateRequest) {
        Group group = findById(groupId);
        group.updateGroup(groupUpdateRequest);
        return ApiResponse.ok(GroupResponse.of(leader, group));
    }

    // 그룹 내 멤버 조회
    public List<UserResponse> getGroupMembers(Long groupId) {
        Group group = findById(groupId);
        List<Users> members = relationRepository.findMembersByGroup(group);
        return members.stream()
                .map(UserResponse::of)
                .collect(Collectors.toList());
    }
    // 그룹 상세 정보 조회
    public ApiResponse<GroupDetailResponse> getGroupDetail(Long groupId) {
        Group group = findById(groupId);
        Users leader = findLeaderByGroup(group);
        Integer totalWeeklyGoalCount = relationRepository.sumPersonalWeeklyGoalByGroupId(groupId);
        Long memberCount = relationRepository.countByGroup(group);
        return ApiResponse.ok(GroupDetailResponse.of(leader, group, memberCount, totalWeeklyGoalCount));
    }

    // 유저가 속한 그룹을 모두 보여줌
    public ApiResponse<List<GroupResponse>> getUsersGroups(Users user) {
        List<Group> groups = relationRepository.findGroupsByMember(user);
        List<GroupResponse> groupResponses = groups.stream()
                .map(group -> {
                    Users leader = findLeaderByGroup(group);
                    return GroupResponse.of(leader, group);
                })
                .toList(); // 결과를 리스트로 변환
        return ApiResponse.ok(groupResponses);
    }

    public List<GroupResponse> getUsersGroups(List<Group> groups) {
        return groups.stream()
                .map(this::getGroupInfo) // 각 그룹 ID를 GroupResponseDto로 변환
                .toList(); // 결과를 리스트로 변환
    }

    // 그룹 삭제
    public ApiResponse<?> deleteGroup(Long groupId) {
        Group group = findById(groupId);
        groupRepository.delete(group);
        return ApiResponse.ok("그룹이 삭제되었습니다.");
    }

    // 유저가 속한 그룹의 카드 생성
    public List<UserGroupCardResponse> getUserGroupCard(Users user) {
        List<Group> groups = relationRepository.findGroupsByMember(user);
        return groups.stream()
                .map(group -> {
                    RelationBetweenUserAndGroup relation = findRelationByMemberAndGroup(user, group);
                    Map<DayOfWeek, Boolean> userAchieveInGroup = recordService.getWeeklyGoalStatus(user, group, LocalDateTime.now());
                    return UserGroupCardResponse.of(group, relation, userAchieveInGroup);
                })
                .toList();
    }

    public ApiResponse<?> getGroups(String category) {
        if (category == null) {
            return ApiResponse.ok(groupRepository.findAll().stream()
                    .map(group -> {
                        Users leader = findLeaderByGroup(group);
                        return GroupResponse.of(leader, group);
                    })
                    .toList());
        }
        try {
            // category를 CategoryStatus로 변환
            CategoryStatus categoryStatus = CategoryStatus.valueOf(category.toUpperCase());
            // 특정 카테고리의 그룹 반환
            return ApiResponse.ok(groupRepository.findAllByCategory(categoryStatus).stream()
                    .map(group -> {
                        Users leader = findLeaderByGroup(group);
                        return GroupResponse.of(leader, group);
                    })
                    .toList());
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리 값 처리
            return ApiResponse.fail("유효하지 않은 카테고리입니다: ", category);
        }
    }

    /// 비지니스 로직

    private Group findById(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));
    }

    private Users findLeaderByGroup(Group group) {
        return relationRepository.findLeaderByGroup(group)
                .orElseThrow(() -> new ResourceNotFoundException("그룹에 리더가 존재하지 않습니다."));
    }

    private RelationBetweenUserAndGroup findRelationByMemberAndGroup(Users user, Group group) {
        return relationRepository.findByMemberAndGroup(user, group)
                .orElseThrow(() -> new ResourceNotFoundException("해당 관계가 존재하지 않습니다."));
    }
}
