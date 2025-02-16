package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupDetailResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupMemberListResponseDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.enums.GroupRole;
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

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;
    private final UserRepository userRepository;
    private final RelationService relationService;
    private final JwtService jwtService;

    // 그룹 생성 (CREATE)
    public ApiResponse<GroupResponseDto> createGroup(GroupCreateRequestDto groupCreateRequestDto, String accessToken, HttpServletResponse response) {

        if (groupRepository.existsByName(groupCreateRequestDto.getGroup_name())) {
            throw new DuplicatedException("이미 존재하는 그룹 이름입니다.");
        }

        String leaderEmail = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new IllegalArgumentException("토큰이 유효하지 않습니다."));

        Users leader = userRepository.findByEmail(leaderEmail)
                .orElseThrow(() -> new ResourceNotFoundException("해당 이메일을 가진 사용자가 없습니다."));
        // 그룹 생성
        Group group = buildGroup(leader, groupCreateRequestDto);

        Long groupId = groupRepository.save(group).getId();

        int leaderDailyGoal = groupCreateRequestDto.getPersonalDailyGoal();
        int leaderWeeklyGoal = groupCreateRequestDto.getPersonalWeeklyGoal();

        GroupJoinRequestDto joinLeader = GroupJoinRequestDto.builder()
                .personalDailyGoal(leaderDailyGoal)
                .personalWeeklyGoal(leaderWeeklyGoal)
                .build();

        relationService.leaderJoin(groupId, leaderEmail, joinLeader);

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

        return buildGroupDetailResponseDto(group);
    }

    public List<GroupResponseDto> getUsersGroups(String accessToken) {

        if (!jwtService.isTokenValid(accessToken)) {
            throw new IllegalArgumentException("유효하지 않은 토큰입니다.");
        }

        String userEmail = jwtService.extractEmail(accessToken)
                .orElseThrow(() -> new ResourceNotFoundException("JWT에 이메일 정보가 없습니다."));

        // 유저가 속한 그룹을 모두 보여줌
        List<Long> groupsIdByEmail = relationRepository.findGroupsIdByEmail(userEmail);

        return groupsIdByEmail.stream()
                .map(this::convertToGroupResponseDto) // 각 그룹 ID를 GroupResponseDto로 변환
                .toList(); // 결과를 리스트로 변환
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

    /// 비지니스 로직
    public boolean isGroupMember(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 존재하지 않습니다."));

        Users member = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 유저가 존재하지 않습니다."));

        return relationRepository.findByMemberAndJoinGroup(member, group).isPresent();
    }

    /// 검증 로직
    private void updateGroupInfo(Group group, GroupUpdateRequestDto groupUpdateRequestDto) {

        if (groupRepository.findByName(groupUpdateRequestDto.getGroupName()).isPresent()) {
            throw new DuplicatedException("이미 존재하는 그룹 이름입니다.");
        }

        if (!group.getName().equals(groupUpdateRequestDto.getGroupName())) {
            group.updateName(groupUpdateRequestDto.getGroupName());
        }

        if (group.getMin_daily_hours() != groupUpdateRequestDto.getMin_daily_hours()) {
            group.updateMinDailyHours(groupUpdateRequestDto.getMin_daily_hours());
        }

        if (group.getMin_weekly_days() != groupUpdateRequestDto.getMin_weekly_days()) {
            group.updateMinWeeklyDays(groupUpdateRequestDto.getMin_weekly_days());
        }

        if (!group.getHashtags().equals(groupUpdateRequestDto.getHashtags())) {
            group.updateHashtags(groupUpdateRequestDto.getHashtags());
        }

        if (!group.getGroup_image_url().equals(groupUpdateRequestDto.getGroup_image_url())) {
            group.updateGroupImageUrl(groupUpdateRequestDto.getGroup_image_url());
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
                .min_daily_hours(group.getMin_daily_hours())
                .min_weekly_days(group.getMin_weekly_days())
                .group_point(group.getGroup_point())
                .hashtags(group.getHashtags())
                .disturb_mode(group.getDisturb_mode())
                .created_date(group.getCreate_date())
                .group_image_url(group.getGroup_image_url())
                .build();
    }

    private Group buildGroup(Users leader, GroupCreateRequestDto groupCreateRequestDto) {

        return Group.builder()
                .leader(leader)
                .name(groupCreateRequestDto.getGroup_name())
                .category(groupCreateRequestDto.getCategory())
                .min_daily_hours(groupCreateRequestDto.getMin_daily_hours())
                .min_weekly_days(groupCreateRequestDto.getMin_weekly_days())
                .group_point(groupCreateRequestDto.getGroup_point())
                .hashtags(groupCreateRequestDto.getHashtags())
                .disturb_mode(groupCreateRequestDto.getDisturb_mode())
                .group_image_url(groupCreateRequestDto.getGroup_image_url())
                .build();
    }

    private GroupDetailResponseDto buildGroupDetailResponseDto(Group group) {
        return GroupDetailResponseDto.builder()
                .groupId(group.getId())
                .groupName(group.getName())
                .leaderId(group.getLeader().getUserId())
                .leaderName(group.getLeader().getName())
                .minDailyHours(group.getMin_daily_hours())
                .minWeeklyDays(group.getMin_weekly_days())
                .groupPoint(group.getGroup_point())
                .hashtags(group.getHashtags())
                .group_image_url(group.getGroup_image_url())
                .weeklyGoalAcheive(group.getWeeklyGoalAchieve())
                .build();
    }

}
