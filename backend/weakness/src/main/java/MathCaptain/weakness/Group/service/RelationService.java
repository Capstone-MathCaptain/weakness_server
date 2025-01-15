package MathCaptain.weakness.Group.service;

import MathCaptain.weakness.Group.domain.Group;
import MathCaptain.weakness.Group.domain.RelationBetweenUserAndGroup;
import MathCaptain.weakness.Group.dto.request.GroupJoinRequestDto;
import MathCaptain.weakness.Group.dto.response.GroupResponseDto;
import MathCaptain.weakness.Group.dto.response.RelationResponseDto;
import MathCaptain.weakness.User.dto.response.UserResponseDto;
import MathCaptain.weakness.Group.enums.GroupRole;
import MathCaptain.weakness.Group.repository.RelationRepository;
import MathCaptain.weakness.User.domain.Users;
import MathCaptain.weakness.User.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class RelationService {

    private final RelationRepository relationRepository;
    private final UserService userService;

    public void saveRelation(Users member, Group group, GroupJoinRequestDto groupJoinRequestDto) {
        relationRepository.save(RelationBetweenUserAndGroup.builder()
                .member(member)
                .groupRole(GroupRole.MEMBER)
                .joinGroup(group)
                .personalDailyGoal(groupJoinRequestDto.getPersonalDailyGoal())
                .personalWeeklyGoal(groupJoinRequestDto.getPersonalWeeklyGoal())
                .build());
    }

    public void checkRelation(Users member, Group group) {
        if (relationRepository.findByMemberAndJoinGroup(member, group).isPresent()) {
            throw new IllegalArgumentException("해당 멤버가 이미 가입되어 있습니다.");
        }
    }

    public RelationBetweenUserAndGroup getRelation(Users member, Group group) {
        return relationRepository.findByMemberAndJoinGroup(member, group)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 존재하지 않습니다."));
    }

    public RelationResponseDto getRelationInfo(Long relationId) {
        RelationBetweenUserAndGroup relation = relationRepository.findById(relationId)
                .orElseThrow(() -> new IllegalArgumentException("해당 관계가 없습니다."));

        GroupResponseDto group = buidGropuResponseDto(relation.getJoinGroup());

        UserResponseDto member = UserResponseDto.builder()
                .userId(relation.getMember().getUserId())
                .email(relation.getMember().getEmail())
                .name(relation.getMember().getName())
                .nickname(relation.getMember().getNickname())
                .phoneNumber(relation.getMember().getPhoneNumber())
                .build();

        return RelationResponseDto.builder()
                .id(relation.getId())
                .member(member)
                .groupRole(relation.getGroupRole())
                .group(group)
                .joinDate(relation.getJoinDate())
                .personalDailyGoal(relation.getPersonalDailyGoal())
                .personalWeeklyGoal(relation.getPersonalWeeklyGoal())
                .build();
    }

    private GroupResponseDto buidGropuResponseDto(Group group) {
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
