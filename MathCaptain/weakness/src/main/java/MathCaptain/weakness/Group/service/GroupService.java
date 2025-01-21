package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupCreateRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.request.GroupUpdateRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.GroupRepository;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.service.UserService;
import MathCaptain.weakness.global.Api.ApiResponse;
import MathCaptain.weakness.global.exception.DuplicatedException;
import MathCaptain.weakness.global.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class GroupService {

    private final GroupRepository groupRepository;
    private final RelationRepository relationRepository;
    private final RelationService relationService;
    private final UserService userService;

    // 그룹 생성 (CREATE)
    public ApiResponse<GroupResponseDto> createGroup(GroupCreateRequestDto groupCreateRequestDto) {

        // 그룹 생성
        Group group = buildGroup(groupCreateRequestDto);
        groupRepository.save(group);

        // 리더에 대한 정보 추출
        Users leader = userService.getUserById(groupCreateRequestDto.getLeader_id());
        int leaderDailyGoal = groupCreateRequestDto.getPersonalDailyGoal();
        int leaderWeeklyGoal = groupCreateRequestDto.getPersonalWeeklyGoal();

        checkJoin(leader, group, leaderDailyGoal, leaderWeeklyGoal);

        // 리더 -> 생성그룹 가입
        RelationBetweenUserAndGroup leaderAndCreateGroup = buildLeaderRelation(leader, group, leaderDailyGoal, leaderWeeklyGoal);
        relationRepository.save(leaderAndCreateGroup);

        return ApiResponse.ok(buildGroupResponseDto(group));
    }

    // 그룹 정보 조회 (READ)
    public ApiResponse<GroupResponseDto> getGroupInfo(Long groupId) {
        Group group = getGroup(groupId);

        return ApiResponse.ok(buildGroupResponseDto(group));
    }

    // 그룹 정보 업데이트 (UPDATE)
    public ApiResponse<GroupResponseDto> updateGroupInfo(Long groupId, GroupUpdateRequestDto groupUpdateRequestDto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 없습니다."));

        updateGroupInfo(group, groupUpdateRequestDto);

        return ApiResponse.ok(buildGroupResponseDto(group));
    }

    // 그룹 참여
    public ApiResponse<?> joinGroup(Long groupId, GroupJoinRequestDto groupJoinRequestDto) {

        Users joinUser = userService.getUserById(groupJoinRequestDto.getUserId());

        Group group = getGroup(groupId);

        // 이미 가입한 경우 & 목표 조건 달성 여부
        checkJoin(joinUser, group, groupJoinRequestDto.getPersonalDailyGoal(), groupJoinRequestDto.getPersonalWeeklyGoal());

        relationService.saveRelation(joinUser, group, groupJoinRequestDto);

        return ApiResponse.ok(null);
    }

    // 그룹 내 멤버 조회
    public ApiResponse<List<UserResponseDto>> getGroupMembers(Long groupId) {
        Group group = getGroup(groupId);

        List<Users> members = relationRepository.findMembersByGroup(group);

        return ApiResponse.ok(members.stream()
                .map(member -> UserResponseDto.builder()
                        .userId(member.getUserId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .nickname(member.getNickname())
                        .phoneNumber(member.getPhoneNumber())
                        .build())
                .collect(Collectors.toList()));
    }

    //== 비지니스 로직 ==//
    public boolean isGroupMember(Long groupId, Long userId) {
        Group group = getGroup(groupId);
        Users member = userService.getUserById(userId);

        return relationRepository.findByMemberAndJoinGroup(member, group).isPresent();
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("해당 그룹이 존재하지 않습니다."));
    }

    //==검증 로직==/
    private void checkJoin(Users member, Group joinGroup, int personalDailyGoal, int personalWeeklyGoal) {

        relationService.checkRelation(member, joinGroup);

        if (personalDailyGoal < joinGroup.getMin_daily_hours()) {
            throw new IllegalArgumentException("하루 목표 시간은 " + joinGroup.getMin_daily_hours() + "시간 이상이어야 합니다.");
        }

        if (personalWeeklyGoal < joinGroup.getMin_weekly_days()) {
            throw new IllegalArgumentException("주간 목표 일수는 " + joinGroup.getMin_weekly_days() + "일 이상이어야 합니다.");
        }
    }

    //==로직들==/
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

    //==빌드==/
    private GroupResponseDto buildGroupResponseDto(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
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

    private Group buildGroup(GroupCreateRequestDto groupCreateRequestDto) {
        Users leader = userService.getUserById(groupCreateRequestDto.getLeader_id());

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

    private RelationBetweenUserAndGroup buildLeaderRelation(Users leader, Group group, int dailyGoal, int weeklyGoal) {
        return RelationBetweenUserAndGroup.builder()
                .member(leader)
                .groupRole(GroupRole.LEADER)
                .joinGroup(group)
                .personalDailyGoal(dailyGoal)
                .personalWeeklyGoal(weeklyGoal)
                .build();
    }



}
