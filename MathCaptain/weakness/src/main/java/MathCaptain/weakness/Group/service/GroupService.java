package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupDetailResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.UserGroupCardResponseDto;
import MathCaptain.weakness.Group.enums.CategoryStatus;
import MathCaptain.weakness.Record.repository.RecordRepository;
import MathCaptain.weakness.Record.service.RecordService;
import MathCaptain.weakness.User.domain.UserDetailsImpl;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.repository.UserRepository;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.Security.jwt.JwtService;
import MathCaptain.weakness.global.exception.DuplicatedException;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;
    private final RecordRepository recordRepository;
    private final RelationService relationService;
    private final RecordService recordService;

    // 그룹 생성 (CREATE)
    public ApiResponse<GroupResponseDto> createGroup(Users leader, GroupCreateRequestDto groupCreateRequestDto, HttpServletResponse response) {

        if (groupRepository.existsByName(groupCreateRequestDto.getGroupName())) {
            throw new DuplicatedException("이미 존재하는 그룹 이름입니다.");
        }

        // 그룹 생성
        Group group = buildGroup(leader, groupCreateRequestDto);

        Long groupId = groupRepository.save(group).getId();

        int leaderDailyGoal = groupCreateRequestDto.getPersonalDailyGoal();
        int leaderWeeklyGoal = groupCreateRequestDto.getPersonalWeeklyGoal();

        GroupJoinRequestDto joinLeader = GroupJoinRequestDto.builder()
                .personalDailyGoal(leaderDailyGoal)
                .personalWeeklyGoal(leaderWeeklyGoal)
                .build();

        relationService.leaderJoin(groupId, leader, joinLeader);

        return ApiResponse.ok(buildGroupResponseDto(group));
    }

    // 그룹 정보 조회 (READ)
    public GroupResponseDto getGroupInfo(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        return buildGroupResponseDto(group);
    }

    public GroupResponseDto getGroupInfo(String groupName) {
        Group group = groupRepository.findByName(groupName)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        return buildGroupResponseDto(group);
    }

    // 그룹 정보 업데이트 (UPDATE)
    public ApiResponse<GroupResponseDto> updateGroupInfo(Long groupId, GroupUpdateRequestDto groupUpdateRequestDto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        updateGroupInfo(group, groupUpdateRequestDto);

        return ApiResponse.ok(buildGroupResponseDto(group));
    }

    // 그룹 내 멤버 조회
    public List<UserResponseDto> getGroupMembers(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        List<Users> members = relationRepository.findMembersByGroup(group);

        return members.stream()
                .map(user -> UserResponseDto.builder()
                        .userId(user.getUserId())
                        .name(user.getName())
                        .nickname(user.getNickname())
                        .email(user.getEmail())
                        .phoneNumber(user.getPhoneNumber())
                        .build())
                .collect(Collectors.toList());
    }

    // 그룹 상세 정보 조회
    public GroupDetailResponseDto getGroupDetail(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        Integer totalWeeklyGoalCount = relationRepository.sumPersonalWeeklyGoalByGroupId(groupId);

        Long memberCount = relationRepository.countByJoinGroup_Id(groupId);

        return buildGroupDetailResponseDto(group, memberCount, totalWeeklyGoalCount);
    }

    public ApiResponse<List<GroupResponseDto>> getUsersGroups(Users user) {

        // 유저가 속한 그룹을 모두 보여줌
        List<Long> groupsId = relationRepository.findGroupsIdByMember(user);

        List<GroupResponseDto> groups = groupsId.stream()
                .map(this::convertToGroupResponseDto) // 각 그룹 ID를 GroupResponseDto로 변환
                .toList(); // 결과를 리스트로 변환

        return ApiResponse.ok(groups);
    }

    public List<GroupResponseDto> getUsersGroups(List<Long> groupId) {
        return groupId.stream()
                .map(this::convertToGroupResponseDto) // 각 그룹 ID를 GroupResponseDto로 변환
                .toList(); // 결과를 리스트로 변환
    }

    // 그룹 삭제
    public ApiResponse<?> deleteGroup(Long groupId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 존재하지 않습니다."));

        groupRepository.delete(group);

        return ApiResponse.ok("그룹이 삭제되었습니다.");
    }

    // 유저가 속한 그룹의 카드 생성
    public List<UserGroupCardResponseDto> getUserGroupCard(Users user) {
        List<Long> groupIds = relationRepository.findGroupsIdByMember(user);

        return groupIds.stream()
                .map(groupId -> {
                    Group group = groupRepository.findById(groupId)
                            .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 존재하지 않습니다."));

                    RelationBetweenUserAndGroup relation = relationRepository.findByMemberAndJoinGroup_Id(user, groupId)
                            .orElseThrow(() -> new ResourceNotFoundException("해당 관계가 존재하지 않습니다."));

                    Map<DayOfWeek, Boolean> userAchieveInGroup = recordService.getWeeklyGoalStatus(user, group, LocalDateTime.now());

                    return UserGroupCardResponseDto.builder()
                            .groupId(group.getId())
                            .groupName(group.getName())
                            .groupImageUrl(group.getGroupImageUrl())
                            .groupRole(relation.getGroupRole())
                            .groupRanking(group.getGroupRanking())
                            .groupPoint(group.getGroupPoint())
                            .userAchieve(userAchieveInGroup)
                            .userDailyGoal(relation.getPersonalDailyGoal())
                            .userWeeklyGoal(relation.getPersonalWeeklyGoal())
                            .build();
                })
                .toList();

    }

    public ApiResponse<?> getGroups(String category) {

        if (category == null) {
            return ApiResponse.ok(groupRepository.findAll().stream()
                    .map(this::buildGroupResponseDto)
                    .toList());
        }
        try {
            // category를 CategoryStatus로 변환
            CategoryStatus categoryStatus = CategoryStatus.valueOf(category.toUpperCase());

            // 특정 카테고리의 그룹 반환
            return ApiResponse.ok(groupRepository.findAllByCategory(categoryStatus).stream()
                    .map(this::buildGroupResponseDto)
                    .toList());
        } catch (IllegalArgumentException e) {
            // 잘못된 카테고리 값 처리
            return ApiResponse.fail("유효하지 않은 카테고리입니다: ", category);
        }
    }

    /// 비지니스 로직

    /// 검증 로직
    private void updateGroupInfo(Group group, GroupUpdateRequestDto groupUpdateRequestDto) {

        if (groupRepository.findByName(groupUpdateRequestDto.getGroupName()).isPresent()) {
            throw new DuplicatedException("이미 존재하는 그룹 이름입니다.");
        }

        if (!group.getName().equals(groupUpdateRequestDto.getGroupName())) {
            group.updateName(groupUpdateRequestDto.getGroupName());
        }

        if (group.getMinDailyHours() != groupUpdateRequestDto.getMinDailyHours()) {
            group.updateMinDailyHours(groupUpdateRequestDto.getMinDailyHours());
        }

        if (group.getMinWeeklyDays() != groupUpdateRequestDto.getMinWeeklyDays()) {
            group.updateMinWeeklyDays(groupUpdateRequestDto.getMinWeeklyDays());
        }

        if (!group.getHashtags().equals(groupUpdateRequestDto.getHashtags())) {
            group.updateHashtags(groupUpdateRequestDto.getHashtags());
        }

        if (!group.getGroupImageUrl().equals(groupUpdateRequestDto.getGroupImageUrl())) {
            group.updateGroupImageUrl(groupUpdateRequestDto.getGroupImageUrl());
        }
    }

    private GroupResponseDto convertToGroupResponseDto(Long groupId) {
        try {
            // 그룹 정보를 조회하고 DTO로 변환
            return getGroupInfo(groupId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("잘못된 그룹 ID 형식: " + groupId, e);
        }
    }

    /// 빌드
    private GroupResponseDto buildGroupResponseDto(Group group) {
        return GroupResponseDto.builder()
                .groupId(group.getId())
                .leaderId(group.getLeader().getUserId())
                .leaderName(group.getLeader().getName())
                .groupName(group.getName())
                .category(group.getCategory())
                .minDailyHours(group.getMinDailyHours())
                .minWeeklyDays(group.getMinWeeklyDays())
                .groupPoint(group.getGroupPoint())
                .groupRanking(group.getGroupRanking())
                .hashtags(group.getHashtags())
                .created_date(group.getCreateDate())
                .groupImageUrl(group.getGroupImageUrl())
                .build();
    }

    private Group buildGroup(Users leader, GroupCreateRequestDto groupCreateRequestDto) {

        return Group.builder()
                .leader(leader)
                .name(groupCreateRequestDto.getGroupName())
                .category(groupCreateRequestDto.getCategory())
                .minDailyHours(groupCreateRequestDto.getMinDailyHours())
                .minWeeklyDays(groupCreateRequestDto.getMinWeeklyDays())
                .groupPoint(groupCreateRequestDto.getGroupPoint())
                .hashtags(groupCreateRequestDto.getHashtags())
                .groupImageUrl(groupCreateRequestDto.getGroupImageUrl())
                .build();
    }

    private GroupDetailResponseDto buildGroupDetailResponseDto(Group group, Long memberCount, Integer totalWeeklyGoalCount) {
        return GroupDetailResponseDto.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .leaderId(group.getLeader().getUserId())
                .leaderName(group.getLeader().getName())
                .minDailyHours(group.getMinDailyHours())
                .minWeeklyDays(group.getMinWeeklyDays())
                .groupPoint(group.getGroupPoint())
                .groupRanking(group.getGroupRanking())
                .hashtags(group.getHashtags())
                .groupImageUrl(group.getGroupImageUrl())
                .weeklyGoalAchieve(group.getWeeklyGoalAchieve())
                .totalWeeklyGoalCount(totalWeeklyGoalCount)
                .memberCount(memberCount)
                .build();
    }

}
