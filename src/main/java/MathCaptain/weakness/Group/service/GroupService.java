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

    // 그룹 생성
    public Long createGroup(GroupCreateRequestDto groupCreateRequestDto) {

        Users leader = userService.getUserById(groupCreateRequestDto.getLeader_id());

        Group group = Group.builder()
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

        Long groupId = groupRepository.save(group).getId();

//        Group leader_group = groupRepository.findById(groupId)
//                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다."));

        RelationBetweenUserAndGroup leaderAndCreateGroup = RelationBetweenUserAndGroup.builder()
                .member(leader)
                .groupRole(GroupRole.LEADER)
                .joinGroup(group)
                .personalDailyGoal(groupCreateRequestDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupCreateRequestDto.getPersonalWeeklyGoal())
                .build();

        relationRepository.save(leaderAndCreateGroup);

        return groupId;
    }

    // 그룹 정보 조회
    public GroupResponseDto getGroupInfo(Long groupId) {
        Group group = getGroup(groupId);

        return buildGroupResponseDto(group);
    }

    // 그룹 참여
    public void joinGroup(Long groupId, GroupJoinRequestDto groupJoinRequestDto) {

        Users joinUser = userService.getUserById(groupJoinRequestDto.getUserId());

        Group group = getGroup(groupId);

        // 이미 가입한 경우 & 목표 조건 닭성 여부
        checkJoin(joinUser, group, groupJoinRequestDto);

        relationService.saveRelation(joinUser, group, groupJoinRequestDto);
    }

    // 그룹 내 멤버 조회
    public List<UserResponseDto> getGroupMembers(Long groupId) {
        Group group = getGroup(groupId);

        List<Users> members = relationRepository.findMembersByGroup(group);

        return members.stream()
                .map(member -> UserResponseDto.builder()
                        .userId(member.getUserId())
                        .email(member.getEmail())
                        .name(member.getName())
                        .nickname(member.getNickname())
                        .phoneNumber(member.getPhoneNumber())
                        .build())
                .collect(Collectors.toList());
    }

    public boolean isGroupMember(Long groupId, Long userId) {
        Group group = getGroup(groupId);
        Users member = userService.getUserById(userId);

        return relationRepository.findByMemberAndJoinGroup(member, group).isPresent();
    }

    // 그룹 정보 업데이트
    public GroupResponseDto updateGroupInfo(Long groupId, GroupUpdateRequestDto groupUpdateRequestDto) {
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 없습니다."));

        updateGroupInfo(group, groupUpdateRequestDto);

        return buildGroupResponseDto(group);
    }

    public Group getGroup(Long groupId) {
        return groupRepository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("해당 그룹이 존재하지 않습니다."));
    }

    //==검증 로직==/
    private void checkJoin(Users member, Group joinGroup, GroupJoinRequestDto groupJoinRequestDto) {

        relationService.checkRelation(member, joinGroup);

        int dailyGoal = groupJoinRequestDto.getPersonalDailyGoal();
        int weeklyGoal = groupJoinRequestDto.getPersonalWeeklyGoal();

        if (dailyGoal < joinGroup.getMin_daily_hours()) {
            throw new IllegalArgumentException("하루 목표 시간은 " + joinGroup.getMin_daily_hours() + "시간 이상이어야 합니다.");
        }

        if (weeklyGoal < joinGroup.getMin_weekly_days() * joinGroup.getMin_daily_hours()) {
            throw new IllegalArgumentException("주간 목표 시간은 " + joinGroup.getMin_weekly_days() * joinGroup.getMin_daily_hours() + "시간 이상이어야 합니다.");
        }
    }

    //==업데이트==/
    private void updateGroupInfo(Group group, GroupUpdateRequestDto groupUpdateRequestDto) {

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



}
