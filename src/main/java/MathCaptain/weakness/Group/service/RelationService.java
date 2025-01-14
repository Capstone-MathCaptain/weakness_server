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

        GroupResponseDto group = GroupResponseDto.builder()
                .id(relation.getJoinGroup().getId())
                .leaderId(relation.getJoinGroup().getLeader().getUserId())
                .leaderName(relation.getJoinGroup().getLeader().getName())
                .groupName(relation.getJoinGroup().getName())
                .category(relation.getJoinGroup().getCategory())
                .min_daily_hours(relation.getJoinGroup().getMin_daily_hours())
                .min_weekly_days(relation.getJoinGroup().getMin_weekly_days())
                .group_point(relation.getJoinGroup().getGroup_point())
                .hashtags(relation.getJoinGroup().getHashtags())
                .disturb_mode(relation.getJoinGroup().getDisturb_mode())
                .created_date(relation.getJoinGroup().getCreate_date())
                .group_image_url(relation.getJoinGroup().getGroup_image_url())
                .build();

        UserResponseDto member = userService.getUserInfo(relation.getJoinGroup().getId());

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


}
